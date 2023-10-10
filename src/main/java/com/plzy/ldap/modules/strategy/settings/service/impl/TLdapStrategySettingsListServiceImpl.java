package com.plzy.ldap.modules.strategy.settings.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsValues;
import com.plzy.ldap.modules.strategy.settings.dto.*;
import com.plzy.ldap.modules.strategy.settings.mapper.TLdapStrategySettingsListMapper;
import com.plzy.ldap.modules.strategy.settings.service.TLdapOuStrategySettingsRefService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsCommandService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsListService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsValuesService;
import com.plzy.ldap.open_service.client_service.bean.StrategySettingsPublicServiceParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Service
@Slf4j
public class TLdapStrategySettingsListServiceImpl extends ServiceImpl<TLdapStrategySettingsListMapper, TLdapStrategySettingsList>
    implements TLdapStrategySettingsListService{

    @Autowired
    private TLdapStrategySettingsListMapper strategySettingsListMapper;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private TLdapOuService ouService;

    @Autowired
    private TLdapStrategySettingsCommandService strategySettingsCommandService;

    @Autowired
    private TLdapOuStrategySettingsRefService ouStrategySettingsRefService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ldap.ansiblePath}")
    private String ansiblePath;

    @Value("${ldap.strategy.strategyPyscriptPath}")
    private String strategyPyscriptPath;

    @Value("${ldap.strategy.sudo_account.account}")
    private String sudoAccount;

    @Value("${ldap.strategy.sudo_account.password}")
    private String sudoPassword;

    // 组策略缓存
    // key=domainid+uiddn
    public static Map<String, List<LdapStrategySettingsDetails>> strategySettingsCache = new ConcurrentHashMap<>();

    @Override
    public void makeCache() {

        log.info("准备初始化策略数据");

        // 清空缓存
        strategySettingsCache.clear();

        // 加载所有的ou列表
        List<TLdapOu> ouList = ouService.list();
        for(TLdapOu ou : ouList){

            // 加载此ou的策略列表
            List<LdapStrategySettingsDetails> settingsLists = strategySettingsListMapper.listByOUId(ou.getId());

            if(settingsLists != null && settingsLists.size() > 0) {
                for (LdapStrategySettingsDetails list : settingsLists) {
                    // 加载策略下的命令列表
                    List<StrategySettingsCommandDto> commandList = strategySettingsListMapper.listCommands(list.getId());
                    list.setCommands(commandList);
                }

                TLdapStrategySettingsListServiceImpl.strategySettingsCache.put(ou.getDomainId() + "-" + ou.getId() + "-" + ou.getDn(), settingsLists);
            }
        }

        log.info("策略数据初始化完成："+TLdapStrategySettingsListServiceImpl.strategySettingsCache);
    }

    @Override
    public List<LdapStrategySettingsDetails> assignmentList(Long ouId) {

        TLdapOu ouObj = ouService.getById(ouId);

        List<LdapStrategySettingsDetails> result = new ArrayList<>();

        try{
            // 搜索策略内容
            Map<String, List<LdapStrategySettingsDetails>> searchResult = getFromCache(ouId);

            // 注意这里要克隆，不要更改缓存本身的数据
            Map<String, List<LdapStrategySettingsDetails>> clone = new HashMap<>();
            for(Map.Entry<String, List<LdapStrategySettingsDetails>> entry : searchResult.entrySet()){
                byte[] raw = SerializationUtils.serialize(entry.getValue());
                List<LdapStrategySettingsDetails> value = (List<LdapStrategySettingsDetails>)SerializationUtils.deserialize(raw);
                clone.put(entry.getKey(),value);
            }

            // 更新策略所属的ou dn，便于前端显示
            for(Map.Entry<String, List<LdapStrategySettingsDetails>> entry : clone.entrySet()){
                for(LdapStrategySettingsDetails settings : entry.getValue()){
                    if(!ouObj.getDn().equals(entry.getKey())) {
                        String key = entry.getKey().replaceFirst(settings.getDomainDn(),"");
                        key = key.replaceAll("DC=","").replaceAll("OU=","");
                        String[] keys = key.split(",");

                        String nkey = "";
                        for(String k : keys){
                            nkey = k + " / " + nkey;
                        }
                        if(nkey.endsWith("/ ")){
                            nkey = nkey.substring(0,nkey.lastIndexOf("/ "));
                        }
                        settings.setExtendOU(nkey);
                    }
                }
                result.addAll(entry.getValue());
            }
        }catch (Exception e){
            log.error("加载组策略时出现错误：",e);
        }

        return result;
    }

    /**
     * 从缓存搜索策略内容
     *
     * @param ouId
     * @return
     */
    private Map<String, List<LdapStrategySettingsDetails>> getFromCache(Long ouId){

        // 如果没有构建过缓存，则构建
        if(strategySettingsCache.size() == 0){
            makeCache();
        }

        TLdapOu ou = ouService.getById(ouId);

        // 查找此机构的当前机构已经所有的父级机构
        Map<String, List<LdapStrategySettingsDetails>> searchResult = new HashMap<>();
        for(Map.Entry<String, List<LdapStrategySettingsDetails>> entry : strategySettingsCache.entrySet()){
            String dn = entry.getKey().split("-")[2];
            if(ou.getDn().endsWith(dn)){
                searchResult.put(dn, entry.getValue());
            }
        }

        // 对机构进行排序，父机构在前，当前机构在后
        Map<String, List<LdapStrategySettingsDetails>> sortMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o1.length() > o2.length()){
                    return 1;
                }else if(o1.length() < o2.length()){
                    return -1;
                }else {
                    return o1.compareTo(o2);
                }
            }
        });
        sortMap.putAll(searchResult);
        log.info("排序后的结果："+sortMap);

        return sortMap;
    }

    @Override
    public IPage<StrategySettingOuDto> getAppliedOU(Page<StrategySettingOuDto> page, Long id) {
        return strategySettingsListMapper.getAppliedOU(page,id);
    }

    @Override
    public List<LdapStrategySettingsDetails> get(Long domainId, String uid) {

        try{
            // 如果前端传的upn格式，则移除@后面的内容
            int posi = uid.indexOf("@");
            if(posi > 0){
                uid = uid.substring(0,posi);
            }

            Map user = domainUserService.getByUid(domainId, uid);
            Object ou = user.get("ou");
            Map ouMap = null;
            if(ou != null && StringUtils.hasText(ou + "") && ((List)ou).size() > 0){
                ouMap = objectMapper.readValue(((List)ou).get(0) + "", Map.class);
            }
            if(ouMap == null){
                log.error("获取用户所在OU时出现错误！");
                throw new RuntimeException("获取用户所在OU时出现错误！");
            }
            Long ouId = Long.valueOf(ouMap.get("ouCN") + "");

            // 查找策略主记录
            List<LdapStrategySettingsDetails> list = ouStrategySettingsRefService.getByOuId(ouId);
            for(LdapStrategySettingsDetails details: list){
                List<StrategySettingsCommandDto> command = strategySettingsCommandService.getCommandWithValues(details.getId());
                details.setCommands(command);
            }

            return list;
        }catch (Exception e){
            log.error("获取组策略时出现错误：",e);
        }

        return new ArrayList<LdapStrategySettingsDetails>();
    }

    @Override
    public List<LdapStrategySettingsListDTO> tree(){
        return strategySettingsListMapper.list();
    }

    @Override
    public List<LdapStrategySettingsListDTO> treeByDomain(Long domainId) {
        return strategySettingsListMapper.treeByDomain(domainId);
    }

    @Override
    public void clearCache() {
        strategySettingsCache.clear();
    }

    @Override
    public Map<String, Object> execute(StrategySettingsPublicServiceParams params) {

        try{
            // 查询此用户的所属domain
            TLdapDomain domain = domainService.getOne(new QueryWrapper<TLdapDomain>().eq("domain_name", params.getDomainName()));

            if(domain == null){
                log.error("没有找到domainName("+params.getDomainName()+")对应的域，请检查domainName是否正确！");

                Map<String, Object> result = new HashMap<>();
                result.put("personalise_shell", "# 没有找到domainName("+params.getDomainName()+")对应的域，请检查domainName是否正确！");
                return result;
            }

            // 如果前端传的upn格式，则移除@后面的内容
            int posi = params.getUid().indexOf("@");
            if(posi > 0){
                params.setUid(params.getUid().substring(0,posi));
            }

            // 查询此用户的所属OU
            boolean exist = domainUserService.isExist(domain.getId(), params.getUid());
            if(!exist){
                log.error("在域中检测不到此用户，请确认是否为LDAP域用户！");

                Map<String, Object> result = new HashMap<>();
                result.put("personalise_shell", "# 在域中检测不到此用户，请确认是否为LDAP域用户！");
                return result;
            }

            Map user = domainUserService.getByUid(domain.getId(), params.getUid());
            Object ou = user.get("ou");
            Map ouMap = null;
            if(ou != null && StringUtils.hasText(ou + "") && ((List)ou).size() > 0){
                ouMap = objectMapper.readValue(((List)ou).get(0) + "", Map.class);
            }
            if(ouMap == null){
                log.error("获取用户所在OU时出现错误！");

                Map<String, Object> result = new HashMap<>();
                result.put("personalise_shell", "# 获取用户所在OU时出现错误！");
                return result;
            }

            Long ouId = Long.valueOf(ouMap.get("ouCN") + "");
            Map<String, List<LdapStrategySettingsDetails>> searchResult = getFromCache(ouId);

            StringBuilder shellContent = new StringBuilder();
            for(Map.Entry<String, List<LdapStrategySettingsDetails>> entry : searchResult.entrySet()){
                // 拼接注释
                shellContent.append("\n").append("########## 组织单位：").append(entry.getKey());
                for(LdapStrategySettingsDetails settings : entry.getValue()){
                    shellContent.append("\n").append("###### 策略记录：").append(settings.getName()).append(",").append(settings.getCode()).append(",").append(settings.getComments());
                    for(StrategySettingsCommandDto command : settings.getCommands()){
                        shellContent.append("\n").append("### 命令：").append(command.getName()).append(",").append(command.getCode()).append(",").append(command.getType()).append(",").append(command.getComments());

                        // 替换变量内容
                        String commandText = command.getCommand();
                        Map<String, Object> args = objectMapper.readValue(command.getValue(), Map.class);
                        for(Map.Entry<String, Object> arg : args.entrySet()){
                            commandText = commandText.replaceAll("\\{"+arg.getKey()+"\\}",arg.getValue() + "");
                        }

                        shellContent.append("\n").append(commandText).append("\n");
                    }
                }
                shellContent.append("\n\n");
            }
            log.info("命令拼接完成："+shellContent);

            Map<String, Object> result = new HashMap<>();
            result.put("personalise_shell", shellContent);
            return result;

        }catch (Exception e){
            log.error("推送组策略配置时出现异常！", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("personalise_shell", "# 执行组策略时出现了错误！");
        return result;

            // 如果不存在目录，则创建
//            File dir = new File(strategyPyscriptPath);
//            if(!dir.exists()){
//                dir.mkdirs();
//            }
//
//            String shContent =
//                    "#!/usr/bin/bash\n" +
//                            ansibleShellContent;
//            String shPath = strategyPyscriptPath +File.separator +"s_" + System.currentTimeMillis() + ".sh";
//            FileUtil.write(shContent, shPath);
//
//            log.info("即将执行策略sh：" + shContent);
//            String[] commandArgs = new String[]{
//                    ansiblePath, "all",
//                    "--inventory="+params.getHostIP()+", ", //必须要有这个逗号，否则会导致错误！
//                    "--extra-vars", "ansible_user="+sudoAccount.trim()+"  ansible_password="+sudoPassword.trim() + "  ansible_sudo_pass="+sudoPassword.trim(),
//                    "-m","script",
//                    "-a", "\""+shPath+"\"",
//                    "-b","--become" // 以root方式运行
//            };
//
//            String execResult = ProcessUtil.exec(commandArgs);
    }

    @Override
    public Map<String, Integer> statStrategy(Long domainId) {
        return strategySettingsListMapper.statStrategy(domainId);
    }
}





package com.plzy.ldap.framework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonArray;
import lombok.Data;

import java.util.*;

@Data
public class TreeDataUtil {

    public String key;

    public String pid;

    public String label;

    public Object data;

    public String icon;

    public String dn;

    public List<TreeDataUtil> children;

    public TreeDataUtil() {
    }

    public TreeDataUtil(String key, String pid, String label,String dn, Object data) {
        this.key = key;
        this.pid = pid;
        this.label = label;
        this.data = data;
        this.dn = dn;
        this.children = new ArrayList<>();
    }

    public TreeDataUtil(String key, String pid, String label, String dn,Object data, String icon) {
        this.key = key;
        this.pid = pid;
        this.label = label;
        this.dn = dn;
        this.data = data;
        this.icon = icon;
        this.children = new ArrayList<>();
    }

    public static List<TreeDataUtil> getTree(List<TreeDataUtil> nodes) {

        HashMap<String, List<TreeDataUtil>> nodeMap = getPidMap(nodes);

        ArrayList<TreeDataUtil> list;
        if (nodeMap.containsKey("null")) {
            list = new ArrayList<>(nodeMap.get("null"));
        } else {
            list = new ArrayList<>();
        }
        getTreeItem(nodeMap, list);

        return list;

    }

    public static void getTreeItem(HashMap<String, List<TreeDataUtil>> nodeMap, List<TreeDataUtil> list) {

        for (TreeDataUtil node : list) {
            if (nodeMap.containsKey(node.getKey())) {
                node.getChildren().addAll(nodeMap.get(node.getKey()));
                getTreeItem(nodeMap, node.getChildren());

            }
        }
    }

    public static HashMap<String, List<TreeDataUtil>> getPidMap(List<TreeDataUtil> nodes) {
        List<TreeDataUtil> treeDataUtils = JSONArray.parseArray(JSON.toJSONString(nodes), TreeDataUtil.class);

        HashMap<String, List<TreeDataUtil>> nodeMap = new HashMap<>();

        if (treeDataUtils.size() > 0) {
            Iterator<TreeDataUtil> iterator = treeDataUtils.iterator();
            while (iterator.hasNext()) {
                TreeDataUtil item = iterator.next();

                if (nodeMap.containsKey(item.getPid())) {
                    nodeMap.get(item.getPid()).add(item);
                } else {
                    ArrayList<TreeDataUtil> list1 = new ArrayList<>();
                    list1.add(item);
                    nodeMap.put(item.getPid(), list1);
                }

                iterator.remove();
            }
        }
        return nodeMap;
    }

    public static Set<String> getChildrenIdSet(List<TreeDataUtil> nodes, String key) {

        HashMap<String, List<TreeDataUtil>> map = getPidMap(nodes);

        Set<String> set = new HashSet<>();

        set.add(key);
        if (map.containsKey(key)) {
            for (TreeDataUtil item : map.get(key)) {
                set.add(item.getKey());
                getSet(map, set, item.getKey());
            }
        }
        return set;
    }

    public static void getSet(HashMap<String, List<TreeDataUtil>> map, Set<String> set, String key) {
        if (map.containsKey(key)) {
            for (TreeDataUtil item : map.get(key)) {
                set.add(item.getKey());
                getSet(map, set, item.getKey());
            }
        }
    }

    public static Set<Long> toLongSet(Set<String> set) {

        Set<Long> objects = new HashSet<>();
        if (set.size() > 0) {
            for (String s : set) {
                objects.add(Long.valueOf(s));
            }
        }
        return objects;
    }
}

package com.plzy.ldap.modules.report.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.DateUtil;
import com.plzy.ldap.framework.utils.ExcelUtil;
import com.plzy.ldap.modules.report.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/countByDomain")
    public ResponseEntity<ResponseData> countByDomain(Long domainId) {

        if (domainId == 1) {
            return ResponseEntity.ok(ResponseData.success(reportService.countRootDomain()));
        } else {
            return ResponseEntity.ok(ResponseData.success(reportService.countByDomain(domainId)));
        }
    }

    @GetMapping("/countByOU")
    public ResponseEntity<ResponseData> countByOU(Long domainId, Long ouId) {

        Map<String, Integer> result = reportService.countByOU(domainId, ouId);

        return ResponseEntity.ok(ResponseData.success(result));
    }

    @GetMapping("/charts")
    public ResponseEntity<ResponseData> charts(Long domainId) {

        Map<String, Object> result = reportService.charts(domainId);
        return ResponseEntity.ok(ResponseData.success(result));
    }

    @GetMapping("export")
    public ResponseEntity<ResponseData> export(Long domainId, HttpServletResponse response) {

        if (domainId.equals(1L)) {
            return ResponseEntity.ok(ResponseData.success());
        }
        Map<String, Object> result = reportService.charts(domainId);


        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("统计信息");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        int num = 0;
        int lastnum = 0;

        Map passwdExpir = (Map) result.get("passwd_expir");

        Row headerRow = sheet.createRow(num);

        Cell cell01 = headerRow.createCell(0);
        cell01.setCellValue("密码过期统计");
        cell01.setCellStyle(headerCellStyle);

        for (Object key : passwdExpir.keySet()) {

            Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

            String name;
            switch (String.valueOf(key)) {
                case "seven":
                    name = "7天内过期";
                    break;
                case "fourteen":
                    name = "14天内过期";
                    break;
                case "expir":
                    name = "已过期";
                    break;
                case "other":
                    name = "其他";
                    break;
                default:
                    name = String.valueOf(key);
            }

            Cell cell = row.createCell(1);
            cell.setCellValue(name);
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(String.valueOf(passwdExpir.get(key)));
            num++;
        }

        if (num-1 >= lastnum) {
            CellRangeAddress region = new CellRangeAddress(lastnum, num-1, 0, 0);
            sheet.addMergedRegion(region);
            lastnum = num;
        }


        Map sysArch = (Map) result.get("sys_arch");

        Row headerRow1 = sheet.createRow(num);

        Cell cell11 = headerRow1.createCell(0);
        cell11.setCellValue("主机分类");
        cell11.setCellStyle(headerCellStyle);

        for (Object key : sysArch.keySet()) {

            Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

            String name = String.valueOf(key);

            Cell cell = row.createCell(1);
            cell.setCellValue(name);
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(String.valueOf(sysArch.get(key)));
            num++;
        }


        if (num-1 >= lastnum) {
            CellRangeAddress region1 = new CellRangeAddress(lastnum, num - 1, 0, 0);
            sheet.addMergedRegion(region1);
            lastnum = num;
        }


        Map user = (Map) result.get("user");

        Row headerRow2 = sheet.createRow(num);

        Cell cell21 = headerRow2.createCell(0);
        cell21.setCellValue("用户统计");
        cell21.setCellStyle(headerCellStyle);

        for (Object key : user.keySet()) {

            Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

            String name;
            switch (String.valueOf(key)) {
                case "login_in_seven":
                    name = "7天内登录";
                    break;
                case "never_login_in_seven":
                    name = "7天未登录";
                    break;
                case "total":
                    name = "总计";
                    break;
                default:
                    name = String.valueOf(key);
            }
            Cell cell = row.createCell(1);
            cell.setCellValue(name);
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(String.valueOf(user.get(key)));
            num++;
        }

        if (num-1 >= lastnum) {
            CellRangeAddress region2 = new CellRangeAddress(lastnum, num - 1, 0, 0);
            sheet.addMergedRegion(region2);
            lastnum = num;
        }


        Map strategy = (Map) result.get("strategy");

        Row headerRow3 = sheet.createRow(num);

        Cell cell31 = headerRow3.createCell(0);
        cell31.setCellValue("组策略统计");
        cell31.setCellStyle(headerCellStyle);

        for (Object key : strategy.keySet()) {

            Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

            String name;
            switch (String.valueOf(key)) {
                case "private":
                    name = "私有策略";
                    break;
                case "public":
                    name = "公共策略";
                    break;
                case "used":
                    name = "已使用策略";
                    break;
                default:
                    name = String.valueOf(key);
            }
            Cell cell = row.createCell(1);
            cell.setCellValue(name);
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(String.valueOf(strategy.get(key)));
            num++;
        }
        if (num-1 >= lastnum) {

            CellRangeAddress region3 = new CellRangeAddress(lastnum, num - 1, 0, 0);
            sheet.addMergedRegion(region3);
            lastnum = num;
        }


        Map client_version = (Map) result.get("client_version");

        Row headerRow4 = sheet.createRow(num);

        Cell cell41 = headerRow4.createCell(0);
        cell41.setCellValue("客户端版本统计");
        cell41.setCellStyle(headerCellStyle);

        for (Object key : client_version.keySet()) {

            Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

            String name = String.valueOf(key);

            Cell cell = row.createCell(1);
            cell.setCellValue(name);
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(String.valueOf(client_version.get(key)));
            num++;
        }

        if (num-1 >= lastnum) {
            CellRangeAddress region4 = new CellRangeAddress(lastnum, num - 1, 0, 0);
            sheet.addMergedRegion(region4);
            lastnum = num;
        }


        Map sys_version = (Map) result.get("sys_version");

        Row headerRow5 = sheet.createRow(num);

        Cell cell51 = headerRow5.createCell(0);
        cell51.setCellValue("系统版本");
        cell51.setCellStyle(headerCellStyle);

        for (Object key : sys_version.keySet()) {

            Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

            String name = String.valueOf(key);

            Cell cell = row.createCell(1);
            cell.setCellValue(name);
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(String.valueOf(sys_version.get(key)));
            num++;
        }

        if (num-1 >= lastnum) {
            CellRangeAddress region5 = new CellRangeAddress(lastnum, num - 1, 0, 0);
            sheet.addMergedRegion(region5);
            lastnum = num;
        }

        Map online_num = (Map) result.get("online_num");

        Row headerRow6 = sheet.createRow(num);

        Cell cell6 = headerRow6.createCell(0);
        cell6.setCellValue("在线人数");
        cell6.setCellStyle(headerCellStyle);

        for (Object key : online_num.keySet()) {

            Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

            String name = String.valueOf(key);

            Cell cell = row.createCell(1);
            cell.setCellValue(name);
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(String.valueOf(online_num.get(key)));
            num++;
        }

        if (num-1 >= lastnum) {
            CellRangeAddress region6 = new CellRangeAddress(lastnum, num - 1, 0, 0);
            sheet.addMergedRegion(region6);
            lastnum = num;
        }

        List<Map<String, Integer>> install_stat = (List<Map<String, Integer>>) result.get("install_stat");

        Row headerRow7 = sheet.createRow(num);

        Cell cell7 = headerRow7.createCell(0);
        cell7.setCellValue("客户端安装量统计");
        cell7.setCellStyle(headerCellStyle);

        for (Map<String, Integer> stringIntegerMap : install_stat) {
            for (Object key : stringIntegerMap.keySet()) {

                Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

                String name = String.valueOf(key);

                Cell cell = row.createCell(1);
                cell.setCellValue(name);
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(String.valueOf(stringIntegerMap.get(key)));
                num++;
            }
        }


        if (num-1 >= lastnum) {
            CellRangeAddress region7 = new CellRangeAddress(lastnum, num - 1, 0, 0);
            sheet.addMergedRegion(region7);
            lastnum = num;
        }


        Map install_groupby_ou = (Map) result.get("install_groupby_ou");

        Row headerRow8 = sheet.createRow(num);

        Cell cell8 = headerRow8.createCell(0);
        cell8.setCellValue("ou分组的客户端安装量统计");
        cell8.setCellStyle(headerCellStyle);

        for (Object key : install_groupby_ou.keySet()) {

            Row row = sheet.getRow(num) != null ? sheet.getRow(num) : sheet.createRow(num);

            String name = String.valueOf(key);

            Cell cell = row.createCell(1);
            cell.setCellValue(name);
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(String.valueOf(install_groupby_ou.get(key)));
            num++;
        }

        if (num-1 >= lastnum) {
            CellRangeAddress region8 = new CellRangeAddress(lastnum, num - 1, 0, 0);
            sheet.addMergedRegion(region8);
        }


        OutputStream outputStream = null;
        try {
            response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode("统计", "UTF-8") + ".xlsx");

            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            outputStream = response.getOutputStream();
            workbook.write(outputStream);

        } catch (Exception e) {
            throw new RuntimeException("导出Excel时出现错误：" + e);
        } finally {
            try {
                workbook.close();
                outputStream.close();
            } catch (Exception e) {
            }

        }

        return ResponseEntity.ok(ResponseData.success());
    }
}

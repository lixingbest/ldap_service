package com.plzy.ldap.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
/**
 * Excel工具类
 */
@Slf4j
public class ExcelUtil {

    public static File multipartFileToFile(MultipartFile multipartFile){

        String fileName = UUID.randomUUID() +".xlsx";
        File file = null;
        file = new File(fileName);
        OutputStream out = null;
        try {
            out = Files.newOutputStream(file.toPath());
            byte[] ss = multipartFile.getBytes();
            for (byte s : ss) {
                if (out != null) {
                    out.write(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 导出数据到Excel文件中
     *
     * @param sheetName
     * @param columnName
     * @param data
     * @throws IOException
     */
    public static void export(String sheetName, String[] columnName, List<Map<String, Object>> data, String fileName, HttpServletResponse response) {

        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet(sheetName);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columnName.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnName[i].split(":")[0]);
            cell.setCellStyle(headerCellStyle);
        }

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

        int rowNum = 1;
        for (Map<String, Object> record : data) {

            Row row = sheet.createRow(rowNum++);

            int columnNum = 0;
            for (String col : columnName) {
                String dataKey = col.split(":")[1];
                Object value = record.get(dataKey);

                //转换时间格式
                if (value instanceof Timestamp) {
                    value = DateUtil.formatDate(new Date(((Timestamp) value).getTime()), DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss);
                }

                row.createCell(columnNum++) .setCellValue(value == null ? "" : value + "");
            }
        }

        for (int i = 0; i < columnName.length; i++) {
            sheet.autoSizeColumn(i);
        }

        OutputStream outputStream = null;
        try {
            response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

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
    }


    /**
     * 读取Excel
     * 注意：首行必须是表头列，并且数据返回时会忽略表头行
     *
     * @param file      excel文件
     * @param sheetName tab名称
     * @param cells     列数
     * @return
     */
    public static List<LinkedHashMap<String, Object>> read(File file, String sheetName, Integer cells) {

        List<LinkedHashMap<String, Object>> resultSet = new ArrayList<>();

        Workbook wb;
        try {
            wb = WorkbookFactory.create(file);
        } catch (Exception e) {
            return resultSet;
        }

        Sheet s = wb.getSheet(sheetName);
        Row header = s.getRow(0);

        // 从1开始，跳过header
        for (int i = 1; i < s.getPhysicalNumberOfRows(); i++) {
            Row row = s.getRow(i);
            LinkedHashMap<String, Object> record = new LinkedHashMap<>();
            for (int j = 0; j < cells; j++) {
                if (row.getCell(j) != null) {
                    row.getCell(j).setCellValue(String.valueOf(row.getCell(j)));
                    record.put(header.getCell(j).getStringCellValue(), row.getCell(j).getStringCellValue());
                } else {
                    record.put(header.getCell(j).getStringCellValue(), "");
                }
            }
            resultSet.add(record);
        }
        return resultSet;
    }
}

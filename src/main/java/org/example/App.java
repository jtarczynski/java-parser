package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        List<GitPojo> gitPojoList = createListFromJson("hibernate_merged_file.json");
        exportListToExcel(gitPojoList, "result.xlsx");
    }

    private static List<GitPojo> createListFromJson(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonGitDataArray =
                    FileUtils.readFileToString(new File("src/main/resources/" + fileName),
                            StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonGitDataArray, new TypeReference<ArrayList<GitPojo>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void exportListToExcel(List<GitPojo> inputList, String fileName) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Result_1");
            int rowNum = 0;
            for (GitPojo gitPojo : inputList) {
                Row row = sheet.createRow(rowNum++);
                createList(gitPojo, row);
            }
            FileOutputStream outputFile = new FileOutputStream("src/main/results/" + fileName);
            workbook.write(outputFile);
            outputFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createList(GitPojo gitPojo, Row row) {

        Cell cell = row.createCell(0);
        cell.setCellValue(gitPojo.getCommit());

        cell = row.createCell(1);
        cell.setCellValue(gitPojo.getAuthor());

        cell = row.createCell(2);
        cell.setCellValue(gitPojo.getDate());

        cell = row.createCell(3);
        cell.setCellValue(gitPojo.getMessage());

        /*
         nie wstawiona jest lista bo trochę by to
         pokomplikowało excela a i tak nie są to dane, których będziemy używać
         */

    }
}

package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.json.XML;
import org.modelmapper.ModelMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class App {
    public static void main(String[] args) {
        convertXmlFileToJsonFile("jira_data.xml", "result_jira.json");

        List<GitPojo> gitPojoList = createListFromJson(GitPojo.class,
                readFileToString("hibernate_git.json"));

        List<JiraPojo> jiraPojoList = createListFromJson(JiraPojo.class,
                retrieveJsonArrayFromObject("result_jira.json"));

        mergeData(gitPojoList, jiraPojoList).entrySet().forEach(System.out::println);
    }

    private static HashMap<String, MergedJiraGitPojo> mergeData(List<GitPojo> gitPojoList,
                                                                List<JiraPojo> jiraPojoList) {
        HashMap<String, MergedJiraGitPojo> mergedHashMap = new HashMap<>();
        ModelMapper mapper = new ModelMapper();
        if (gitPojoList != null && jiraPojoList != null) {
            jiraPojoList.forEach(jiraPojo ->
                    mergedHashMap.put(jiraPojo.getKey(), mapper.map(jiraPojo, MergedJiraGitPojo.class)));

            gitPojoList.forEach(gitPojo -> {
                System.out.println(gitPojo.getMessage());
                String key = getKeyFromMessage(gitPojo.getMessage());
                if (key != null && mergedHashMap.containsKey(key)) {
                    mergedHashMap.get(key).getGitPojoList().add(gitPojo);
                }
            });
        }
        return mergedHashMap;
    }

    private static String getKeyFromMessage(String message) {
        try {
            int keyEndIndex = message.indexOf("-", message.indexOf("-") + 1);
            return message.substring(0, keyEndIndex);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return null;
    }

    private static String readFileToString(String fileName) {
        try {
            return FileUtils.readFileToString(new File("src/main/resources/" + fileName),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String retrieveJsonArrayFromObject(String fileName) {
        return new JSONObject(Objects.requireNonNull(readFileToString(fileName)))
                .getJSONObject("rss").getJSONObject("channel").getJSONArray("item").toString();
    }

    private static void convertXmlFileToJsonFile(String inputFile, String outputFile) {
        try (FileWriter file = new FileWriter("src/main/resources/" + outputFile)) {
            file.write(XML.toJSONObject(Objects.requireNonNull(readFileToString(inputFile))).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> List<T> createListFromJson(Class<T> clazz, String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return objectMapper
                    .readValue(json, objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
        No usage for now
    */
    private static void exportListToExcel(List<GitPojo> inputList, String fileName) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("result");
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

    /*
        No usage for now
    */
    private static void createList(GitPojo gitPojo, Row row) {

        Cell cell = row.createCell(0);
        cell.setCellValue(gitPojo.getCommit());

        cell = row.createCell(1);
        cell.setCellValue(gitPojo.getAuthor());

        cell = row.createCell(2);
        cell.setCellValue(gitPojo.getDate());

        cell = row.createCell(3);
        cell.setCellValue(gitPojo.getMessage());

    }
}

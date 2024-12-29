package com.pjsky.feishu;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.lark.oapi.service.bitable.v1.model.Condition;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordRespBody;

public class BiTableReaderTest {

    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 示例多维表格的字段名称定义
    private static final String TJ_BITABLE_FIELD_NAME = "内容名称";
    private static final String TJ_BITABLE_FIELD_PUBTIME = "发布时间（必填）";
    private static final String TJ_BITABLE_FIELD_ORIGINFILE = "原稿（选填）";
    private static final String TJ_BITABLE_FIELD_FINALFILE = "成品（必填）";
    private static final String TJ_BITABLE_FIELD_SUBJECT = "主题领域（必填）";
    private static final String TJ_BITABLE_FIELD_SERIES = "系列名称（选填）";
    private static final String TJ_BITABLE_FIELD_STYLE = "形式（必填）";
    private static final String TJ_BITABLE_FIELD_CREATOR = "作者（必填）";
    private static final String TJ_BITABLE_FIELD_LASTMODIFY = "最后更新时间";

    @BeforeEach
    public void init() {
        BiTableReader.init();
    }

    @Test
    public void testGetApp() throws Exception {
        String appToken = "Ucvlbcflha7YK5ssqN7cDKAlnRb";
        BiTableReader.getApp(appToken);
    }

    @Test
    public void testListAppTable() throws Exception {
        String appToken = "Bocgb9NrWafrTqsHEYYcU3Krnpb";
        BiTableReader.listAppTable(appToken);
    }

    @Test
    public void testListAppTableView() throws Exception {
        String appToken = "Bocgb9NrWafrTqsHEYYcU3Krnpb";
        String tableId = "tblFlMeu5kfs8Jnz";
        BiTableReader.listAppTableView(appToken, tableId);
    }

    @Test
    public void testGetAppTableView() throws Exception {
        String appToken = "Bocgb9NrWafrTqsHEYYcU3Krnpb";
        String tableId = "tblFlMeu5kfs8Jnz";
        String viewId = "vewFmXT7vm";
        BiTableReader.getAppTableView(appToken, tableId, viewId);
    }

    @Test
    public void testListAppTableField() throws Exception {
        String appToken = "Bocgb9NrWafrTqsHEYYcU3Krnpb";
        String tableId = "tblFlMeu5kfs8Jnz";
        BiTableReader.listAppTableField(appToken, tableId);
    }

    @Test
    public void testSearchAppTableRecord() throws Exception {
        String appToken = "Bocgb9NrWafrTqsHEYYcU3Krnpb";
        String tableId = "tblFlMeu5kfs8Jnz";
        String viewId = "vewFmXT7vm";
        // 设置过滤条件
        List<Condition> conditions = new ArrayList<>();
        conditions.add(Condition.newBuilder()
                .fieldName("主题领域（必填）")
                .operator("contains") // is, contains
                .value(new String[] { "市场资讯", "固收" })
                .build());

        // String lastScanTime = "2024-12-12 00:00:00";
        // Date lastScanDate = formatter.parse(lastScanTime);
        // // 转成Unix时间戳，毫秒
        // long lastScanTimeStamp = lastScanDate.getTime();
        // conditions.add(Condition.newBuilder().fieldName("最后更新时间").operator("isGreater")
        // .value(new String[] { "ExactDate", "" + lastScanTimeStamp }).build());

        Condition[] conditionsArray = conditions.toArray(new Condition[conditions.size()]);
        SearchAppTableRecordRespBody searchAppTableRecordRespBody = BiTableReader.searchAppTableRecord(appToken,
                tableId, viewId, conditionsArray);

        for (AppTableRecord record : searchAppTableRecordRespBody.getItems()) {
            // 获取记录ID
            String recordId = record.getRecordId();
            // 获取记录字段
            Map<String, Object> fields = record.getFields();

            // 获取内容名称字段
            Object name = fields.get(TJ_BITABLE_FIELD_NAME);
            Object publishTime = fields.get(TJ_BITABLE_FIELD_PUBTIME); // 发布日期，Unix时间戳，毫秒
            Object originFile = fields.get(TJ_BITABLE_FIELD_ORIGINFILE);
            Object finalFile = fields.get(TJ_BITABLE_FIELD_FINALFILE);
            Object subject = fields.get(TJ_BITABLE_FIELD_SUBJECT);
            Object series = fields.get(TJ_BITABLE_FIELD_SERIES);
            Object style = fields.get(TJ_BITABLE_FIELD_STYLE);
            Object creator = fields.get(TJ_BITABLE_FIELD_CREATOR);

            System.out.println("原始Object输出，recordId=" + recordId + ", name=" + name + ", publishTime="
                    + publishTime + ", originFile=" + originFile
                    + ", finalFile=" + finalFile + ", subject=" + subject + ", series=" + series + ", style=" + style
                    + ", creator=" + creator);

            System.out.println("转成JSON输出，recordId=" + recordId + ", name=" + Jsons.DEFAULT.toJson(name)
                    + ", publishTime="
                    + Jsons.DEFAULT.toJson(publishTime) + ", originFile=" + Jsons.DEFAULT.toJson(originFile)
                    + ", finalFile=" + Jsons.DEFAULT.toJson(finalFile) + ", subject=" + Jsons.DEFAULT.toJson(subject)
                    + ", series=" + Jsons.DEFAULT.toJson(series) + ", style=" + Jsons.DEFAULT.toJson(style)
                    + ", creator=" + Jsons.DEFAULT.toJson(creator));

            String nameJson = Jsons.DEFAULT.toJson(name);
            JsonArray nameJsonArray = JsonParser.parseString(nameJson).getAsJsonArray();
            String nameText = nameJsonArray.get(0).getAsJsonObject().get("text").getAsString();

            Double publishTimeDouble = (Double) publishTime;
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            String pubTime = nf.format(publishTimeDouble);
            Long pubTimeLong = Math.round(publishTimeDouble);

            String originFileJson = Jsons.DEFAULT.toJson(originFile);
            JsonObject originFileJsonObject = JsonParser.parseString(originFileJson).getAsJsonObject();
            String originFileLink = originFileJsonObject.get("link").getAsString();
            String originFileName = originFileJsonObject.get("text").getAsString();

            String subjectJson = Jsons.DEFAULT.toJson(subject);
            JsonArray subjectJsonArray = JsonParser.parseString(subjectJson).getAsJsonArray();
            ArrayList<String> subjectList = new ArrayList<>();
            subjectJsonArray.forEach(item -> {
                subjectList.add(item.getAsString());
            });

            System.out.println("解析后，recordId=" + recordId + ", name=" + nameText 
            + ", publishTime=" + (Double) publishTime + ", publishTime=" + pubTime 
            + ", publishTimeLong=" + pubTimeLong + ", originFile=" + originFileLink 
            + ", originFileName=" + originFileName + ", subject=" + subjectList);
        }
    }

    @Test
    public void testBatchGetAppTableRecord() throws Exception {
        String appToken = "Ucvlbcflha7YK5ssqN7cDKAlnRb";
        String tableId = "tbl0Az0LrlcJoJAV";
        String[] recordIds = { "rec5Tu3xTO" };
        BiTableReader.batchGetAppTableRecord(appToken, tableId, recordIds);
    }

    @Test
    public void testSubscribeFile() throws Exception {
        String fileToken = "Ucvlbcflha7YK5ssqN7cDKAlnRb";
        BiTableReader.subscribeFile(fileToken, "bitable", "");// 第三个参数默认不要传，传了会报参数错误
    }

}

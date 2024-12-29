package com.pjsky.feishu;

import java.util.Map;

import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.lark.oapi.service.bitable.v1.model.BatchGetAppTableRecordReq;
import com.lark.oapi.service.bitable.v1.model.BatchGetAppTableRecordReqBody;
import com.lark.oapi.service.bitable.v1.model.BatchGetAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.Condition;
import com.lark.oapi.service.bitable.v1.model.FilterInfo;
import com.lark.oapi.service.bitable.v1.model.GetAppReq;
import com.lark.oapi.service.bitable.v1.model.GetAppResp;
import com.lark.oapi.service.bitable.v1.model.GetAppTableViewReq;
import com.lark.oapi.service.bitable.v1.model.GetAppTableViewResp;
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldReq;
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldResp;
import com.lark.oapi.service.bitable.v1.model.ListAppTableReq;
import com.lark.oapi.service.bitable.v1.model.ListAppTableResp;
import com.lark.oapi.service.bitable.v1.model.ListAppTableViewReq;
import com.lark.oapi.service.bitable.v1.model.ListAppTableViewResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordRespBody;
import com.lark.oapi.service.bitable.v1.model.Sort;
import com.lark.oapi.service.drive.v1.model.SubscribeFileReq;
import com.lark.oapi.service.drive.v1.model.SubscribeFileResp;

public class BiTableReader {
    private static Client feishuClient = null;

    public static void init() {
        feishuClient = FeishuClientConfig.newFeishuClient();
    }

    // GET /open-apis/bitable/v1/apps/:app_token
    public static void getApp(String appToken) throws Exception {
        // 创建请求对象
        GetAppReq req = GetAppReq.newBuilder()
                .appToken(appToken)
                .build();

        // 发起请求
        GetAppResp resp = feishuClient.bitable().v1().app().get(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(),
                            resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    public static void listAppTable(String appToken) throws Exception {
        // 创建请求对象
        ListAppTableReq req = ListAppTableReq.newBuilder()
                .appToken(appToken)
                .pageToken("")
                .pageSize(20)
                .build();

        // 发起请求
        ListAppTableResp resp = feishuClient.bitable().v1().appTable().list(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(),
                            resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    // GET /open-apis/bitable/v1/apps/:app_token/tables/:table_id/views
    public static void listAppTableView(String appToken, String tableId) throws Exception {
        // 创建请求对象
        ListAppTableViewReq req = ListAppTableViewReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .pageSize(10)
                .userIdType("user_id")
                .build();

        // 发起请求
        ListAppTableViewResp resp = feishuClient.bitable().v1().appTableView().list(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(),
                            resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    // GET /open-apis/bitable/v1/apps/:app_token/tables/:table_id/views/:view_id
    public static void getAppTableView(String appToken, String tableId, String viewId) throws Exception {
        // 创建请求对象
        GetAppTableViewReq req = GetAppTableViewReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .viewId(viewId)
                .build();

        // 发起请求
        GetAppTableViewResp resp = feishuClient.bitable().v1().appTableView().get(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(),
                            resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    // GET /open-apis/bitable/v1/apps/:app_token/tables/:table_id/fields
    public static void listAppTableField(String appToken, String tableId) throws Exception {
        // 创建请求对象
        ListAppTableFieldReq req = ListAppTableFieldReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                // .viewId("vewOVMEXPF")
                .textFieldAsArray(true)
                // .pageToken("fldwJ4YrtB")
                .pageSize(20)
                .build();

        // 发起请求
        ListAppTableFieldResp resp = feishuClient.bitable().v1().appTableField().list(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(),
                    resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    // POST /open-apis/bitable/v1/apps/:app_token/tables/:table_id/records/search
    public static SearchAppTableRecordRespBody searchAppTableRecord(String appToken, String tableId, String viewId,
            Condition[] conditionsArray) throws Exception {
        // 创建请求对象
        SearchAppTableRecordReq req = SearchAppTableRecordReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .userIdType("user_id")
                .pageToken("")
                .pageSize(20)
                .searchAppTableRecordReqBody(SearchAppTableRecordReqBody.newBuilder()
                        .viewId(viewId)
                        .fieldNames(new String[] {})
                        .sort(new Sort[] {})
                        .filter(FilterInfo.newBuilder()
                                .conjunction("and")
                                .conditions(conditionsArray)
                                .build())
                        .automaticFields(true)
                        .build())
                .build();

        // 发起请求
        SearchAppTableRecordResp resp = feishuClient.bitable().v1().appTableRecord().search(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(),
                            resp.getRequestId()));
            return null;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));

        return resp.getData();
    }

    // POST /open-apis/bitable/v1/apps/:app_token/tables/:table_id/records/batch_get
    public static void batchGetAppTableRecord(String appToken, String tableId, String[] recordIds)
            throws Exception {
        // 创建请求对象
        BatchGetAppTableRecordReq req = BatchGetAppTableRecordReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .batchGetAppTableRecordReqBody(BatchGetAppTableRecordReqBody.newBuilder()
                        .recordIds(recordIds)
                        .userIdType("user_id")
                        .withSharedUrl(true)
                        .automaticFields(false)
                        .build())
                .build();

        // 发起请求
        BatchGetAppTableRecordResp resp = feishuClient.bitable().v1().appTableRecord().batchGet(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(),
                            resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    // POST /open-apis/drive/v1/files/:file_token/subscribe
    // 注意，文档的通知事件仅支持文档拥有者和文档管理者订阅。
    public static boolean subscribeFile(String fileToken, String fileType, String eventType) throws Exception {
        // 创建请求对象
        SubscribeFileReq req = SubscribeFileReq.newBuilder()
                .fileToken(fileToken)
                .fileType(fileType)
                .eventType(eventType)
                .build();

        // 发起请求
        SubscribeFileResp resp = feishuClient.drive().v1().file().subscribe(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(),
                    resp.getRequestId()));
            return false;
        }

        // 业务数据处理
        System.out.println(Jsons.LONG_TO_STR.toJson(resp.getData()));

        return true;
    }

}

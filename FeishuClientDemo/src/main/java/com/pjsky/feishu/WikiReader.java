package com.pjsky.feishu;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.lark.oapi.Client;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.docx.v1.enums.BlockBlockTypeEnum;
import com.lark.oapi.service.docx.v1.model.Block;
import com.lark.oapi.service.docx.v1.model.File;
import com.lark.oapi.service.docx.v1.model.Image;
import com.lark.oapi.service.docx.v1.model.ListDocumentBlockReq;
import com.lark.oapi.service.docx.v1.model.ListDocumentBlockResp;
import com.lark.oapi.service.docx.v1.model.ListDocumentBlockRespBody;
import com.lark.oapi.service.docx.v1.model.Text;
import com.lark.oapi.service.docx.v1.model.TextElement;
import com.lark.oapi.service.drive.v1.enums.ExportTaskJobStatusEnum;
import com.lark.oapi.service.drive.v1.model.CreateExportTaskReq;
import com.lark.oapi.service.drive.v1.model.CreateExportTaskResp;
import com.lark.oapi.service.drive.v1.model.DownloadExportTaskReq;
import com.lark.oapi.service.drive.v1.model.DownloadExportTaskResp;
import com.lark.oapi.service.drive.v1.model.DownloadFileReq;
import com.lark.oapi.service.drive.v1.model.DownloadFileResp;
import com.lark.oapi.service.drive.v1.model.DownloadMediaReq;
import com.lark.oapi.service.drive.v1.model.DownloadMediaResp;
import com.lark.oapi.service.drive.v1.model.ExportTask;
import com.lark.oapi.service.drive.v1.model.GetExportTaskReq;
import com.lark.oapi.service.drive.v1.model.GetExportTaskResp;
import com.lark.oapi.service.wiki.v2.model.GetNodeSpaceReq;
import com.lark.oapi.service.wiki.v2.model.GetNodeSpaceResp;
import com.lark.oapi.service.wiki.v2.model.ListSpaceNodeReq;
import com.lark.oapi.service.wiki.v2.model.ListSpaceNodeResp;
import com.lark.oapi.service.wiki.v2.model.ListSpaceReq;
import com.lark.oapi.service.wiki.v2.model.ListSpaceResp;

public class WikiReader {
    private static Client feishuClient = null;

    public static void init() {
        feishuClient = FeishuClientConfig.newFeishuClient();
    }

    // GET /open-apis/wiki/v2/spaces
    public static void listSpace() throws Exception {
        // 创建请求对象
        ListSpaceReq req = ListSpaceReq.newBuilder()
                .pageSize(20)
                .pageToken("")
                .lang("zh")
                .build();

        // 发起请求
        ListSpaceResp resp = feishuClient.wiki().v2().space().list(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    // GET /open-apis/wiki/v2/spaces/:space_id/nodes
    public static void listSpaceNode(String spaceId) throws Exception {
        // 创建请求对象
        ListSpaceNodeReq req = ListSpaceNodeReq.newBuilder()
                .spaceId(spaceId)
                .pageSize(50)
                .pageToken("")
                .parentNodeToken("")
                .build();

        // 发起请求
        ListSpaceNodeResp resp = feishuClient.wiki().v2().spaceNode().list(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    // GET /open-apis/wiki/v2/spaces/get_node
    public static void getSpaceNode(String nodeToken) throws Exception {
        // 创建请求对象
        GetNodeSpaceReq req = GetNodeSpaceReq.newBuilder()
                .token(nodeToken) // 对应 node_token
                .objType("wiki")
                .build();

        // 发起请求
        GetNodeSpaceResp resp = feishuClient.wiki().v2().space().getNode(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
    }

    // GET /open-apis/drive/v1/files/:file_token/download
    // 下载文件
    // 下载云空间中的文件，如 PDF 文件。不包含飞书文档、电子表格以及多维表格等在线文档。本接口仅支持下载云空间中的资源文件。
    // 本接口仅支持下载云空间中的资源文件。要下载云文档中的素材（如图片、附件等），需调用下载素材接口。
    public static void downloadFile(String fileToken) throws Exception {
        // 创建请求对象
        DownloadFileReq req = DownloadFileReq.newBuilder()
                .fileToken(fileToken)
                .build();

        // 发起请求
        DownloadFileResp resp = feishuClient.drive().v1().file().download(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return;
        }

        // 业务数据处理
        // System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        // 保存文件到本地
        resp.writeFile(resp.getFileName());
    }

    // POST /open-apis/drive/v1/export_tasks
    private static String createExportTask(String objToken, String objType, String fileExtension) throws Exception {
        // 创建请求对象
        CreateExportTaskReq req = CreateExportTaskReq.newBuilder()
                .exportTask(ExportTask.newBuilder()
                        .fileExtension(fileExtension)
                        .token(objToken)
                        .type(objType)
                        .subId("")
                        .build())
                .build();

        // 发起请求
        CreateExportTaskResp resp = feishuClient.drive().v1().exportTask().create(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return null;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));

        return resp.getData().getTicket();
    }

    // GET /open-apis/drive/v1/export_tasks/:ticket
    private static ExportTask getExportTask(String ticket, String objToken) throws Exception {
        // 创建请求对象
        GetExportTaskReq req = GetExportTaskReq.newBuilder()
                .ticket(ticket)
                .token(objToken)
                .build();

        // 发起请求
        GetExportTaskResp resp = feishuClient.drive().v1().exportTask().get(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return null;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));

        return resp.getData().getResult();
    }

    // GET /open-apis/drive/v1/export_tasks/file/:file_token/download
    // 下载云空间的文件，需要云空间下载文件权限
    private static void downloadExportTaskFile(String fileToken) throws Exception {
        // 创建请求对象
        DownloadExportTaskReq req = DownloadExportTaskReq.newBuilder()
                .fileToken(fileToken)
                .build();

        // 发起请求
        DownloadExportTaskResp resp = feishuClient.drive().v1().exportTask().download(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return;
        }

        // 业务数据处理
        // System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        // 保存文件到本地
        resp.writeFile(resp.getFileName());
    }

    // 同步导出 word 在线文档
    public static void exportWordSync(String objToken, String objType, String fileExtension) throws Exception {
        // 创建导出任务
        String ticket = createExportTask(objToken, objType, fileExtension);
        if (StringUtils.isEmpty(ticket)) {
            System.out.println("创建导出任务失败");
            return;
        }

        // 查询导出任务结果
        ExportTask exportTask = null;
        while (true) {
            exportTask = getExportTask(ticket, objToken);
            if (exportTask == null) {
                System.out.println("查询导出任务结果失败");
                return;
            }

            if (exportTask.getJobStatus().equals(ExportTaskJobStatusEnum.SUCCESS.getValue())) {
                break;
            } else if (exportTask.getJobStatus().equals(ExportTaskJobStatusEnum.PROCESSING.getValue())
                    || exportTask.getJobStatus().equals(ExportTaskJobStatusEnum.NEW.getValue())) {
                // 处理中，需继续轮循
                Thread.sleep(1000);
            } else {
                // 其他状态，失败
                System.out.println("导出任务失败，任务状态：" + exportTask.getJobStatus());
                return;
            }

        }

        // 下载导出文件
        if (exportTask != null) {
            System.out.println("导出任务成功，待导出文件的 file token: " + exportTask.getFileToken());
            downloadExportTaskFile(exportTask.getFileToken());
        }

    }

    // GET /open-apis/docx/v1/documents/:document_id/blocks
    public static void listDocumentBlock(String documentId) throws Exception {
        // 创建请求对象
        ListDocumentBlockReq req = ListDocumentBlockReq.newBuilder()
                .documentId(documentId)
                .pageSize(500)
                .pageToken("")
                .documentRevisionId(-1)
                .userIdType("user_id")
                .build();

        // 发起请求
        ListDocumentBlockResp resp = feishuClient.docx().v1().documentBlock().list(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));

        ListDocumentBlockRespBody data = resp.getData();
        if (data.getItems() == null) {
            System.out.println("块列表为空");
            return;
        }

        for (Block item : data.getItems()) {

            if (item.getBlockType().equals(BlockBlockTypeEnum.TEXT.getValue())) {
                // 获取文本块列表 block_type = 2
                Text text = (Text) item.getText();
                TextElement[] elements = text.getElements();
                for(TextElement element : elements) {
                    System.out.println("文本元素内容：" + element.getTextRun().getContent());
                }
            } else if (item.getBlockType().equals(BlockBlockTypeEnum.IMAGE.getValue())) {
                // 获取图片块列表 block_type = 27
                Image image = (Image) item.getImage();
                System.out.println("图片Token：" + image.getToken());
            } else if (item.getBlockType().equals(BlockBlockTypeEnum.FILE.getValue())) {
                // 获取文件块列表 block_type = 23
                File file = (File) item.getFile();
                System.out.println("文件Token：" + file.getToken() + " 文件名称：" + file.getName());
            }

        }

    }

    // GET /open-apis/drive/v1/medias/:file_token/download
    // 下载素材
    // 下载各类云文档中的素材，例如电子表格中的图片。
    //本接口仅支持下载云文档而非云空间中的资源文件。如要下载云空间中的资源文件，需调用下载文件接口。
    public static void downloadMedia(String fileToken) throws Exception {
        // 创建请求对象
        DownloadMediaReq req = DownloadMediaReq.newBuilder()
                .fileToken(fileToken)
                .extra("")
                .build();

        // 发起请求
        DownloadMediaResp resp = feishuClient.drive().v1().media().download(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s"
                    , resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return;
        }

        // 业务数据处理
        //System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        // 保存文件到本地
        resp.writeFile(resp.getFileName());
    }

}

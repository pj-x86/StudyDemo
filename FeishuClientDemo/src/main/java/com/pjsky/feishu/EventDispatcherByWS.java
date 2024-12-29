package com.pjsky.feishu;

import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.CustomEventHandler;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.service.drive.DriveService;
import com.lark.oapi.service.drive.v1.model.P2FileBitableRecordChangedV1;
import com.lark.oapi.service.drive.v1.model.SubscribeFileReq;
import com.lark.oapi.service.drive.v1.model.SubscribeFileResp;
import com.lark.oapi.service.im.ImService;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.lark.oapi.ws.Client;

import java.nio.charset.StandardCharsets;

public class EventDispatcherByWS {
    private static final EventDispatcher EVENT_HANDLER = EventDispatcher.newBuilder("", "") // 长连接不需要这两个参数，请保持空字符串
            .onP2MessageReceiveV1(new ImService.P2MessageReceiveV1Handler() {
                @Override
                public void handle(P2MessageReceiveV1 event) throws Exception {
                    System.out.printf("[ onP2MessageReceiveV1 access ], data: %s\n",
                            Jsons.DEFAULT.toJson(event.getEvent()));
                }
            })
            .onP2FileBitableRecordChangedV1(new DriveService.P2FileBitableRecordChangedV1Handler() {
                @Override
                public void handle(P2FileBitableRecordChangedV1 event) throws Exception {
                    System.out.printf("[ onP2FileBitableRecordChangedV1 access ], data: %s\n",
                            Jsons.DEFAULT.toJson(event.getEvent()));
                }
            })
            .onCustomizedEvent("这里填入你要自定义订阅的 event 的 key,例如 out_approval", new CustomEventHandler() {
                // 对于一些老事件、找不到上述结构化定义的事件，需要自行使用这种 onCustomizedEvent 的方式去订阅
                @Override
                public void handle(EventReq event) throws Exception {
                    System.out.printf("[ onCustomizedEvent access ], type: message, data: %s\n",
                            new String(event.getBody(), StandardCharsets.UTF_8));
                }
            })
            .build();

    // POST /open-apis/drive/v1/files/:file_token/subscribe
    // 注意，文档的通知事件仅支持文档拥有者和文档管理者订阅。
    public static boolean subscribeFile(String fileToken) throws Exception {
        // 构建client
        com.lark.oapi.Client client = FeishuClientConfig.newFeishuClient();

        // 创建请求对象
        SubscribeFileReq req = SubscribeFileReq.newBuilder()
                .fileToken(fileToken)
                .fileType("bitable")
                // .eventType("drive.file.bitable_record_changed_v1")
                .build();

        // 发起请求
        SubscribeFileResp resp = client.drive().v1().file().subscribe(req);

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(
                    String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return false;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        return true;
    }

    public static Client newFeishuWSClient() {
        String appId = "cli_a7b38440f277"; // 修改成 你的应用 AppID
        String appSecret = "voXsy0UNvOZwNkXArZT5Q"; // 修改成 你的应用 AppSecret
        return new Client.Builder(appId, appSecret)
                .eventHandler(EVENT_HANDLER)
                .build();
    }

    public static void main(String[] args) {
        String fileToken = "Ucvlbcflha7YK5ssqN7cDKAlnRb"; // 多维表格的 file_token

        try {
            if (!subscribeFile(fileToken)) {
                System.out.println("订阅失败");
                return;
            }
            System.out.println("订阅成功");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Client client = newFeishuWSClient();
        client.start();
    }
}

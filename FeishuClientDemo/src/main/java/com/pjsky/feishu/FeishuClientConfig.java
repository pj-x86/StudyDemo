package com.pjsky.feishu;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.lark.oapi.Client;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.lark.oapi.core.httpclient.OkHttpTransport;
import com.lark.oapi.okhttp.OkHttpClient;
import com.pjsky.interceptor.FeishuInterceptor;

public class FeishuClientConfig {
    private static String appId = "cli_a7b38440f277"; // 修改成 你的应用 AppID
    private static String appSecret = "voXsy0UNvOZwNkXArZT5Q"; // 修改成 你的应用 AppSecret

    public static Client newFeishuClient() {
        // 默认配置为自建应用
        Client.Builder builder = Client.newBuilder(appId, appSecret)
                .openBaseUrl(BaseUrlEnum.FeiShu)
                .requestTimeout(300, TimeUnit.SECONDS) // 设置 httpclient 超时时间，默认永不超时
                //.disableTokenCache() // 禁用token管理，禁用后需要开发者自己传递token
                .logReqAtDebug(true); // 在 debug 模式下会打印 http 请求和响应的 headers,body 等信息。

        String proxyHost = "";
        int proxyPort = 9090;
        Proxy proxy = null;
        if (StringUtils.isNotBlank(proxyHost)) {
            // 配置了正向代理地址，则使用代理
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        }

        builder.httpTransport(new OkHttpTransport(new OkHttpClient().newBuilder()
                .callTimeout(300, TimeUnit.SECONDS).proxy(proxy).addInterceptor(new FeishuInterceptor()).build()));

        return builder.build();
    }
}

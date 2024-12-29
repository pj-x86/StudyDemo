package com.pjsky.interceptor;

import java.io.IOException;

import com.lark.oapi.okhttp.Interceptor;
import com.lark.oapi.okhttp.Request;
import com.lark.oapi.okhttp.Response;

// 在所有发给飞书开放平台的 HTTP 请求头中添加自定义签名
public class FeishuInterceptor implements Interceptor {
    private static final String authorization = "Feishu-Sign";
    private static final String secret = "SsIQ1RjxpgfmeIDqWw";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().header(authorization, secret).build();
        return chain.proceed(request);
    }

}

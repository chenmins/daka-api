package org.chenmin.daka;

import java.nio.charset.Charset;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

import weixin.popular.api.BaseAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.message.message.ImageMessage;
import weixin.popular.bean.message.message.Message;
import weixin.popular.bean.message.message.TextMessage;
import weixin.popular.client.LocalHttpClient;

import weixin.popular.util.JsonUtil;

public class CustomserviceAPI  extends BaseAPI {

    public static BaseResult custom_send(String access_token, String json) {
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setHeader(jsonHeader)
                .setUri(BASE_URI + "/cgi-bin/message/custom/send")
                .addParameter(PARAM_ACCESS_TOKEN, access_token)
                .setEntity(new StringEntity(json, Charset.forName("UTF-8")))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest,
                BaseResult.class);
    }

    public static BaseResult custom_send(String access_token, Message json) {
        return custom_send(access_token, JsonUtil.toJSONString(json));
    }

    public static BaseResult custom_send_Text(String access_token,
                                              String touser, String content) {
        TextMessage json = new TextMessage(touser, content);
        return custom_send(access_token, json);
    }

    public static BaseResult custom_send_Image(String access_token,
                                               String touser, String mediaId) {
        ImageMessage json = new ImageMessage(touser, mediaId);
        return custom_send(access_token, json);
    }


}

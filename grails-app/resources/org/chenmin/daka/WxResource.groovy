package org.chenmin.daka

import com.alibaba.fastjson.JSONObject
import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.apache.http.client.HttpClient

import javax.ws.rs.Consumes
import javax.ws.rs.FormParam
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(value = "wx", description = "个人微信服务相关接口")
@Path('/api/wx')
class WxResource {

    WxService wxService

    WxUserService wxUserService

    ClockUserService clockUserService

    AesService aesService

    @GET
    @Path('/user/{openid}')
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据openid获得微信用户资料", notes = "")
    WxUser user(@ApiParam(required = true, value = "微信个人ID")
                @PathParam("openid")
                        String openid) {
        return wxUserService.get(openid)
    }

    @GET
    @Path('/unionid/{unionid}')
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据unionid获得打卡用户资料", notes = "")
    ClockUser unionid(@ApiParam(required = true, value = "微信个人unionid")
                      @PathParam("unionid")
                              String unionid) {
        return clockUserService.getByUnionid(unionid)
    }

    @GET
    @Path('/token')
    @Produces('text/plain')
    @ApiOperation(value = "获得token", notes = "")
    String token() {
        return wxService.getToken("wx22617d41951fcc1f")
    }

    //openid授权
    @POST
    @Path('/jscode2session/{jscode}')
    @ApiOperation(value = "openid授权", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    String jscode2session(
            @ApiParam(required = true, value = " 临时登录凭证code")
            @PathParam("jscode") String jscode,
            @FormParam("iv") String iv,
            @FormParam("encryptedData") String encryptedData
    ) {
        //https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        def url = "https://api.weixin.qq.com/sns/jscode2session?"
        url += "appid=wxc031ee452cdff633"
        url += "&secret=f938c57eb5ced6903e44f0c57df40ebd"
        url += ("&js_code=" + jscode)
        url += "&grant_type=authorization_code"
        CookiesHttpClient chc = new CookiesHttpClient()
        HttpClient hc = chc.getHttpClient();
        def p = new HashMap<String, String>()
        def jsonText = HttpClientTools.get(hc, url, p)

        chc.close()

        Map map = new HashMap();

        //解析相应内容（转换成json对象）
        JSONObject json = JSONObject.parseObject(jsonText);
        //获取会话密钥（session_key）
        String session_key = json.get("session_key").toString();
        //用户的唯一标识（openid）
//        String openid = (String) json.get("openid");

        try {
            String result = aesService.decrypt(encryptedData, session_key, iv, "UTF-8");
            println "result:${result}"
            if (null != result && result.length() > 0) {
                map.put("status", 1);
                map.put("msg", "解密成功");

                JSONObject userInfoJSON = JSONObject.parseObject(result);
                Map userInfo = new HashMap();
                userInfo.put("openId", userInfoJSON.get("openId"));
                userInfo.put("nickName", userInfoJSON.get("nickName"));
                userInfo.put("gender", userInfoJSON.get("gender"));
                userInfo.put("city", userInfoJSON.get("city"));
                userInfo.put("province", userInfoJSON.get("province"));
                userInfo.put("country", userInfoJSON.get("country"));
                userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl"));
                userInfo.put("unionId", userInfoJSON.get("unionId"));
                map.put("userInfo", userInfo);
                println map
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map as JSON
//        return jsonText
    }

}
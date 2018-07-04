package org.chenmin.daka

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('/api/wxst')
class WxstResource {

    @GET
    @Path('/get/{hashFile}')
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "存储图片", notes = "")
    WxImages get(  @ApiParam(required = true, value = "微信图片id")
                 @PathParam("hashFile")String hashFile) {
        return WxImages.findByHashFileAndValid(hashFile,true)
    }

    @POST
    @Path('/save')
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "存储图片", notes = "")
    WxImages save(WxImages wx){
        wx.save(flush: true)
        return WxImages.findByHashFile(wx.hashFile)
    }

    @GET
    @Path('/del/{hashFile}')
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "删除图片", notes = "")
    WxImages del(  @ApiParam(required = true, value = "微信图片id")
                 @PathParam("hashFile")String hashFile) {
        def  a =  WxImages.findByHashFileAndValid(hashFile)
        a.valid = false
        a.save(flush: true)
        return a
    }


}
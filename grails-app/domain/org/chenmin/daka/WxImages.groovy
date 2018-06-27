package org.chenmin.daka

class WxImages {

    String openid

    String hashFile

    String keyFile

    String imageURLs

    boolean valid = true

    /**
     * 创建时间
     */
    Date dateCreated
    /**
     * 更新时间
     */
    Date lastUpdated

    static constraints = {

    }

    static mapping = {
        table('wx_images')
        openid column:'openid', index:'openid_idx'
        hashFile column:'hashFile', index:'hashFile_idx'
    }
}

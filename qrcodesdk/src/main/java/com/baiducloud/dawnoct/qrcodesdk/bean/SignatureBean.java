package com.baiducloud.dawnoct.qrcodesdk.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by DawnOct on 2017/9/27.
 */

public class SignatureBean extends DataSupport {
    private String pk;
    //签名内容
    private String content;
    //生成时间
    private long generateTime;
    //有效时长
    private long validTime;


    public long getValidTime() {
        return validTime;
    }

    public void setValidTime(long validTime) {
        this.validTime = validTime;
    }

    public long getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(long generateTime) {
        this.generateTime = generateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }
}

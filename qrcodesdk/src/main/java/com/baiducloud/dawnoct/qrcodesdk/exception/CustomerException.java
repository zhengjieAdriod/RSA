package com.baiducloud.dawnoct.qrcodesdk.exception;

/**
 * Created by DawnOct on 2017/9/28.
 */

public class CustomerException extends RuntimeException {

    private String retCd;  //异常对应的返回码
    private String msgDes;  //异常对应的描述信息

    public CustomerException() {
        super();
    }

    public CustomerException(String message) {
        super(message);
        msgDes = message;
    }

    public CustomerException(String retCd, String msgDes) {
        super();
        this.retCd = retCd;
        this.msgDes = msgDes;
    }

    public String getRetCd() {
        return retCd;
    }

    public String getMsgDes() {
        return msgDes;
    }

    public String toString() {
        return "Error Code:" + this.getRetCd() + "," + this.getMsgDes();
    }
}

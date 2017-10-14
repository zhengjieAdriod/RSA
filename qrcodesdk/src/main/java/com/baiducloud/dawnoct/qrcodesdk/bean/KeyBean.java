package com.baiducloud.dawnoct.qrcodesdk.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by DawnOct on 2017/9/27.
 */

public class KeyBean  extends DataSupport {
    private String publicKeyStr;
    private String privateKeyStr;
    private String pk;

    public String getPublicKeyStr() {
        return publicKeyStr;
    }

    public void setPublicKeyStr(String publicKeyStr) {
        this.publicKeyStr = publicKeyStr;
    }

    public String getPrivateKeyStr() {
        return privateKeyStr;
    }

    public void setPrivateKeyStr(String privateKeyStr) {
        this.privateKeyStr = privateKeyStr;
    }


    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }
}

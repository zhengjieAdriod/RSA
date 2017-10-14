package com.baiducloud.dawnoct.qrcodesdk.QRutils;

import java.io.FileInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Created by DawnOct on 2017/10/13.
 * 证书相关的操作
 */

public class CertificateUtil {
    public static final String X509 = "X.509";
    /**
     * <p>
     * 获得证书
     * </p>
     *
     * @param certificatePath 证书存储路径
     * @return
     * @throws Exception
     */
    private static Certificate getCertificate(String certificatePath)
            throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance(X509);
        FileInputStream in = new FileInputStream(certificatePath);
        Certificate certificate = certificateFactory.generateCertificate(in);
        in.close();
        return certificate;
    }
    /**
     * <p>
     * 根据证书获得公钥
     * </p>
     *
     * @param certificatePath 证书存储路径
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey(String certificatePath)
            throws Exception {
        Certificate certificate = getCertificate(certificatePath);
        PublicKey publicKey = certificate.getPublicKey();
        return publicKey;
    }


}

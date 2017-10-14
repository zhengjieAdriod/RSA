package com.baiducloud.dawnoct.qrcodesdk.QRutils;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.baiducloud.dawnoct.qrcodesdk.SDKApplication;
import com.baiducloud.dawnoct.qrcodesdk.bean.KeyBean;
import com.baiducloud.dawnoct.qrcodesdk.bean.SignatureBean;
import com.baiducloud.dawnoct.qrcodesdk.exception.CustomerException;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.litepal.crud.DataSupport;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.List;

/**
 * Created by DawnOct on 2017/9/27.
 */

public class QRutils {
    /**
     * 重新获得公钥key
     *
     * @return key
     */
    public static String generateKey() {
        KeyPair keyPair = RSAUtil.generateRSAKeyPair(RSAUtil.DEFAULT_KEY_SIZE);

        if (keyPair != null) {
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            saveKeyToLocal(publicKey, privateKey, new GenerateKeyCallBack() {
                @Override
                public void onSaveResult(boolean success, String key) {

                }
            });
            byte[] encoded = publicKey.getEncoded();
            String hexPublicKey = Hex.encode(encoded);
            return hexPublicKey;//返回十六进制的字符公钥
        }
        return "";
    }

    /**
     * 重新获得公钥key
     *
     * @param certCallBack certCallBack
     * @return key
     */
    public static void generateKey(GenerateKeyCallBack certCallBack) throws Exception {
//        testNED();
        testHASH1();


        KeyPair keyPair = RSAUtil.generateRSAKeyPair(RSAUtil.DEFAULT_KEY_SIZE);
        if (keyPair != null) {
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            //test
//            byte[] encryptBytes = RSAUtil.encryptByPrivateKeyForSpilt("乘客".getBytes(), privateKey.getEncoded());
//            String encryStr = Base64Decoder.encode(encryptBytes);
//            Log.e("chengke", "乘客私钥加密后的数据 --22222-->" + encryStr);
            saveKeyToLocal(publicKey, privateKey, certCallBack);

        }
        certCallBack.onSaveResult(false, "");

    }

    //计算用户证书签名和用户元数据合在一起的HASH1值(成功)
    private static void testHASH1() throws NoSuchAlgorithmException {
        String content = "268081F2A8451320C31BC0CF8C156A97EFFA8E8E3AA2222E0A4E9FCF358FAC395FA03729704646581895BD07FBE4AB4D0E4F6F7688588944F009D331A2A2A4FBAE88C6034846C8FD3FDD68C7CCBDD026048CC8BDA3A58324A72E3E1CF27B26F7DB2A5A1523A61A3A87ABED0B0FD02CDAF4B3AB368F8F6C46E3CB44609ACF5CAB81FDBE5C0B4EED713365F866D86ECB2EDE03E532E239948493990900000000FFFF000000000000000001017FFFFFFF010203040506070809000102030405060708090001020304050607080900";
        //常用算法：MD5、SHA、CRC
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        byte[] decode = Hex.decode(content);
        byte[] result = digest.digest(decode);
        //消息摘要的结果一般都是转换成16 进制字符串形式展示
        String hex = Hex.encode(result);
        //MD5 结果为16 字节（128 个比特位）、转换为16 进制表示后长度是32 个字符
        //SHA 结果为20 字节（160 个比特位）、转换为16 进制表示后长度是40 个字符
        System.out.println(hex);
    }

    //使用N E 方式获得私钥 公钥 , 并对得到的96字节HaSH值. 进行加密.
    private static void testNED() throws Exception {
        //密钥的n, e值
        String e = "00010001";
        String n = "A8DA1916CB48FE8D1C0042A2209186F7AF3DF951A8E99D1595CC075125165825670EAB5113C559616E2ADFC1F53214516E02BABA786B5F57E7777C84AEF9AD7B8615B74C6D81FDBE5C0B4EED713365F866D86ECB2EDE03E532E2399484939909";
        String d = "029A06AF8559C0F352BD15C9BFF8F76489AE9450D13FEA927E87568DB0BB0F080CD615916C9DAFDE7FE72E4C03A4F3849498BFD61EB2CCE51EC780762B3E239BEF5B8F34B29944484A248E7894E5A86DE4850A121CA0332EAEE3599AFF414711";
        Long longE = Long.parseLong(e, 16);
//        Long longN = Long.parseLong(n, 16);
//        String str = new BigInteger(n, 16).toString(10);
//        Long longD = Long.parseLong(d, 16);
        String modulus = new BigInteger(n, 16).toString(10);
        String publicExponent = longE.toString();
        PublicKey publicKey1 = RSAUtil.getPublicKey(modulus, publicExponent);
        String privateExponent = new BigInteger(d, 16).toString(10);
//        PrivateKey privateKey1 = RSAUtil.getPrivateKey(modulus, privateExponent);

        //Hash值和用户元数据填充后的96个字节
        String data_96 = "4BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB00000000FFFF000000000000000001017FFFFFFF0102030405060708090001020304050607080900010203040506070809003DDF834D9B70137BB6BE5FF5D84AE7880FC6A817BC";
        //私钥加密后得到的用户签名
        String aa = "06BE150FF53E7BA8BB0AB2F5C48F4EE1D09022E784642BD65AD47B802C531AA2DDBE1792E9C253EFECA6540CBF0CE840DEAFD84B8F6FD4FE3F7E78D2A876970718E19D38F5D3C8435AAFF978A36AC053E3631294F735464C240A1EAF5A4395AE";
        byte[] decode = Hex.decode(aa);
        byte[] bytes = RSAUtil.decryptByPublicKeyForSpilt(decode, publicKey1.getEncoded());
        String encode = Hex.encode(bytes);
        //对96个字节加密.  进行私钥加密,得到用户签名
//        byte[] data_96_byte = Hex.decode(data_96);
//        byte[] bytes = RSAUtil.encryptByPrivateKeyForSpilt(data_96_byte, privateKey1.getEncoded());
//        String encode = Hex.encode(bytes);
    }

    /**
     * 写入用户证书(有回调)
     *
     * @param userCert userCert
     * @param callBack 是否成功写入用户证书的回调
     */
    public static void writeUserCert(final String userCert, @Nullable final WriteUsertCertCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SignatureBean signatureBean = updateSignature(userCert);
                if (callBack != null) {
                    if (signatureBean != null) {
                        callBack.onResult(signatureBean.save());
                    } else {
                        callBack.onResult(false);
                    }
                }
            }
        }).start();

    }

    /**
     * 写入用户证书
     *
     * @param userCert userCert
     */
    public static void writeUserCert(final String userCert) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SignatureBean signatureBean = updateSignature(userCert);
                if (signatureBean != null) {
                    signatureBean.save();
                }
            }
        }).start();

    }

    /**
     * 写入用户证书
     *
     * @param userCert userCert
     */
    public static void writeUserCert(final X509Certificate userCert) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SignatureBean signatureBean = updateSignature02(userCert);
                if (signatureBean != null) {
                    signatureBean.save();
                }
            }
        }).start();

    }

    /**
     * 查询用户证书剩余有效时长(异步)
     *
     * @param callBack callBack
     */
    public static void findSignatrueAsyn(final FindUsertCertCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<SignatureBean> all = DataSupport.findAll(SignatureBean.class);
                if (all != null && all.size() > 0) {
                    SignatureBean signatureBean = all.get(0);
                    long l1 = getRemainValidTime(signatureBean);
                    callBack.onSuccess((int) l1);
                } else {
                    callBack.onFail(-1);
                }
            }
        }).start();

    }


    /**
     * 查询用户证书剩余有效时长(同步)
     *
     * @return 签名证书的剩余有效时长(单位毫秒)
     */
    public static int findSignatrueSyn() {
        List<SignatureBean> all = DataSupport.findAll(SignatureBean.class);
        if (all != null && all.size() > 0) {
            SignatureBean signatureBean = all.get(0);
            long l = getRemainValidTime(signatureBean);
            return (int) (l);//返回剩余有效时长
        }
        return -1;
    }

    /**
     * 生成乘车二维码
     *
     * @param passengerInfo 乘客信息
     */
    public static Bitmap generateQR(String passengerInfo) throws Exception {
        String localPrivateKey = getLocalPrivateKey();//私钥字符串
        if (TextUtils.isEmpty(localPrivateKey))
            throw new CustomerException("101", "The publicKey cannot be null, need to call generateKey method first");
        PrivateKey privateKey = RSAUtil.getPrivateKey(localPrivateKey.getBytes());//抛异常
        byte[] encryptBytes = RSAUtil.encryptByPrivateKeyForSpilt(passengerInfo.getBytes(), privateKey.getEncoded());
        String encryStr = Base64Decoder.encode(encryptBytes);//加密后的乘客信息
        //先要从数据库读取用户签名
        SignatureBean signatureBean = getSignatrueSyn();
        if (signatureBean == null)
            throw new CustomerException("102", "UserCert cannot be null, need to call writeUserCert method first ");//签名不存在的情况
        long remainValidTime = getRemainValidTime(signatureBean);
        if (remainValidTime < 0)
            throw new CustomerException("103", "The signature is expired");//签名过期的情况
        //拼接的字符串(签名文件内容,加密后的乘客信息)
        //01010101005701020304050607080900000000010000000101020304050607080900FFFFFFFFFFFFFFFF0000000000FFFF000000000000000001017FFFFFFF010203040506070809000102030405060708090001020304050607080900
        String textContent = signatureBean.getContent();
        if (TextUtils.isEmpty(textContent))
            throw new CustomerException("104", "The two-dimensional code content cannot be empty");
        return CodeUtils.createImage(textContent, 400, 400, null);
//        return null;
        //带logo
//        return CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
    }

    /**
     * 生成乘车二维码02
     *
     * @param passengerInfo 用户签名数据源数据(已加密的数据)
     */
    public static Bitmap generateQR02(String passengerInfo) throws Exception {
        String localPrivateKey = getLocalPrivateKey();//私钥字符串
        if (TextUtils.isEmpty(localPrivateKey))
            throw new CustomerException("101", "The publicKey cannot be null, need to call generateKey method first");
        PrivateKey privateKey = RSAUtil.getPrivateKey(localPrivateKey.getBytes());//抛异常
        byte[] encryptBytes = RSAUtil.decryptByPublicKeyForSpilt(passengerInfo.getBytes(), privateKey.getEncoded());
        String encryStr = Base64Decoder.encode(encryptBytes);//加密后的乘客信息
        //先要从数据库读取用户签名
        SignatureBean signatureBean = getSignatrueSyn();
        if (signatureBean == null)
            throw new CustomerException("102", "UserCert cannot be null, need to call writeUserCert method first ");//签名不存在的情况
        long remainValidTime = getRemainValidTime(signatureBean);
        if (remainValidTime < 0)
            throw new CustomerException("103", "The signature is expired");//签名过期的情况
        //拼接的字符串(签名文件内容,加密后的乘客信息)
        //01010101005701020304050607080900000000010000000101020304050607080900FFFFFFFFFFFFFFFF0000000000FFFF000000000000000001017FFFFFFF010203040506070809000102030405060708090001020304050607080900
        String textContent = signatureBean.getContent();
        if (TextUtils.isEmpty(textContent))
            throw new CustomerException("104", "The two-dimensional code content cannot be empty");
        return CodeUtils.createImage(textContent, 100, 400, null);
//        return null;
        //带logo
//        return CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
    }

    /**
     * 获得本地用户证书(同步)
     *
     * @return 签名证书
     */
    private static SignatureBean getSignatrueSyn() {
        List<SignatureBean> all = DataSupport.findAll(SignatureBean.class);
        if (all != null && all.size() > 0) {
            return all.get(0);
        }
        return null;
    }

    /**
     * 将密钥对保存至本地
     * 保存的是base64编码后的密钥
     */
    private static void saveKeyToLocal(final RSAPublicKey publicKey, final RSAPrivateKey privateKey, final GenerateKeyCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataSupport.deleteAll(KeyBean.class);//保存之前先清空
                // 由密钥对象转化为密钥字符串
                String publicKeyString = Base64Decoder.encode(publicKey.getEncoded());
                // 得到私钥字符串
                String privateKeyString = Base64Decoder.encode(privateKey.getEncoded());

                KeyBean keyBean = new KeyBean();
                keyBean.setPk("01");
                keyBean.setPublicKeyStr(publicKeyString);
                keyBean.setPrivateKeyStr(privateKeyString);
                boolean success = keyBean.save();
                callBack.onSaveResult(success, Hex.encode(publicKey.getEncoded()));//返回十六进制的公钥
                //todo 测试使用
                DButil.copyDataBaseToSD(SDKApplication.getContext(), "key", "sdDemo");

            }
        }).start();
    }

    /**
     * 用私钥解密用户签名,
     *
     * @return An SignatureBean
     */
    private static SignatureBean updateSignature(String userCert) {
        DataSupport.deleteAll(SignatureBean.class);
        try {
            //todo 先要解析用户签名,整理后得到   用户签名\签名生成时间\签名有效时长

            SignatureBean signatureBean = new SignatureBean();
            signatureBean.setPk("01");
            String hexString = "01010101FB268081F2A8451320C31BC0CF8C156A97EFFA8E8E3AA2222E0A4E9FCF358FAC395FA03729704646581895BD07FBE4AB4D0E4F6F7688588944F009D331A2A2A4FBAE88C6034846C8FD3FDD68C7CCBDD026048CC8BDA3A58324A72E3E1CF27B26F7DB2A5A1523A61A3A87ABED0B0FD02CDAF4B3AB368F8F6C46E3CB44609ACF5CAB81FDBE5C0B4EED713365F866D86ECB2EDE03E532E239948493990906BE150FF53E7BA8BB0AB2F5C48F4EE1D09022E784642BD65AD47B802C531AA2DDBE1792E9C253EFECA6540CBF0CE840DEAFD84B8F6FD4FE3F7E78D2A876970718E19D38F5D3C8435AAFF978A36AC053E3631294F735464C240A1EAF5A4395AE";
            signatureBean.setContent(hexString);//用户签名
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            Date date = new Date();
            long time = date.getTime();
            signatureBean.setGenerateTime(time);//签名生成时间
            int validTime = 24 * 17 * 60 * 60 * 1000;//毫秒(假设有效时长为24小时,到月底)
            signatureBean.setValidTime(validTime);//签名有效时长
            return signatureBean;
        } catch (Exception e) {
            return null;
        }
    }

    private static SignatureBean updateSignature02(X509Certificate certificate) {
//        DataSupport.deleteAll(SignatureBean.class);
        try {
            //通过证书获得公钥(用来解密证书中其他被加密的数据)
            PublicKey publicKey = certificate.getPublicKey();
            //获得证书中的其他数据
            //todo 先要解析用户签名,整理后得到   用户签名\签名生成时间\签名有效时长
            SignatureBean signatureBean = new SignatureBean();
            signatureBean.setPk("01");
            String hexString = "00000000FFFF000000000000000001017FFFFFFF010203040506070809000102030405060708090001020304050607080900";
            signatureBean.setContent(hexString);//用户签名
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            Date date = new Date();
            long time = date.getTime();
            signatureBean.setGenerateTime(time);//签名生成时间
            int validTime = 24 * 17 * 60 * 60 * 1000;//毫秒(假设有效时长为24小时,到月底)
            signatureBean.setValidTime(validTime);//签名有效时长
            return signatureBean;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得本地保存的私钥
     *
     * @return 私钥字符串
     */
    private static String getLocalPrivateKey() {
        List<KeyBean> all = DataSupport.findAll(KeyBean.class);
        if (all != null && all.size() > 0) {
            return all.get(0).getPrivateKeyStr();
        }
        return "";
    }

    /**
     * 获得本地保存的公钥
     *
     * @return 公钥字符串
     */
    private String getLocalPublicKey() {
        List<KeyBean> all = DataSupport.findAll(KeyBean.class);
        if (all != null && all.size() > 0) {
            return all.get(0).getPublicKeyStr();
        }
        return "";
    }

    /**
     * 获得用户签名的剩余有效时间
     *
     * @return 剩余有效时长
     */
    private static long getRemainValidTime(SignatureBean signatureBean) {
        long generateTime = signatureBean.getGenerateTime();//签名的生成时间
        long validTime = signatureBean.getValidTime();//有效时长
        int t01 = (int) (validTime / 1000 / 60);
        long now = new Date().getTime();
        long l = now - generateTime;
        long l1 = validTime - l;//剩余有效时长
        long t = (long) (l1 / 1000 / 60);//转换为剩余分钟数
        return t;
    }

    public interface WriteUsertCertCallBack {
        void onResult(boolean success);
    }

    public interface GenerateKeyCallBack {
        void onSaveResult(boolean success, String key);
    }

    public interface FindUsertCertCallBack {
        void onSuccess(int validTime);

        void onFail(int f);
    }
}

package com.atguigu.common.service.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuzhaoxu
 * @date 2023年06月09日 9:02
 */
public class RSAUtil {
//    <dependency>
//            <groupId>commons-io</groupId>
//            <artifactId>commons-io</artifactId>
//            <version>2.4</version>
//       </dependency>
//     <dependency>
//     <groupId>commons-codec</groupId>
//     <artifactId>commons-codec</artifactId>
//     <version>1.10</version>
//</dependency>
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";


    public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象（KeyPairGenerator，密钥对生成器，用于生成公钥和私钥对）
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded()); //返回一个publicKey经过二次加密后的字符串
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());// 返回一个privateKey经过二次加密后的字符串

        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

    public static void main(String[] args) {
//        Map<String, String> keyMap = RSAUtil.createKeys(1024);
//        String  publicKey = keyMap.get("publicKey");
        String  publicKey ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCDNQJQlC7i_fVjETITH_J6A8C_3OhADfdeXD188YeEPZHO_Z1WHu4BqEDwX6sP2GHKfjlzNcgwVWKSqYP-oiAYR9GbKq4-MSyQ1R-JWodaZoj8DRiebZB_t5FYu3He1G7sR5eZifpY0y5AwSK1EFnoW6_4sIxL20Bg0TuwOHIYIQIDAQAB";
//        String  privateKey = keyMap.get("privateKey");
        String  privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIM1AlCULuL99WMRMhMf8noDwL_c6EAN915cPXzxh4Q9kc79nVYe7gGoQPBfqw_YYcp-OXM1yDBVYpKpg_6iIBhH0Zsqrj4xLJDVH4lah1pmiPwNGJ5tkH-3kVi7cd7UbuxHl5mJ-ljTLkDBIrUQWehbr_iwjEvbQGDRO7A4chghAgMBAAECgYBfvjvNLwSz0VvSCcc_m21mSfzKRNoZe9eJvMeFt_4Kqp8Oq5S3NmS5Qtz4SudZ7a0WhVtIzfMiCfyfRyGxmv7NjPpNXSFYWnO4KaChk68fHkdko1sKGE4ZySOhMC60YTYcuAh41r29myr2FDXStPjhU4BX8eU1DN6em8MvKpkDLQJBAMN8mY4k59JINf9RZZbTNl8eUTKykXOAlXYRc26FN4selvb_I6PXHkthpsKb1nsnre5qawEhc6OELj1FwZw5mGMCQQCr0ozSN_kzlzKcPqekHecHT-3iuXGc29hhcyoSif5KUqmHUalXQtC0wFS62_JARvsBx0WnKt7NNJXrEoSSh9qrAkEAtkdXk2A8PSqSFvkaBfH57-WABwrUb2PFeas5_CMBsJocYEF1RQ-QMu_iI_JVos5T80yNbCd6AU1-JCoIJxjZSwJAGnQeLjdj7Kd4qUmUMdZQvXV24JNAhK_sToSWk6gfOH4lj6no8oBc9Zcu9F3snCzRdGKjvKsDBoD0G-8L7itwZQJACs4fe3JpGgbx4nnN_cUhwkgNoW3submaufyl7jnVcmAscqGzDRfrIQVH2tgNE_HFq6KAOQOxkNWdPN-bERxQUw";
        System.out.println("公钥:" + publicKey);
        System.out.println("私钥：" + privateKey);

        System.out.println("公钥加密——私钥解密");
        String str = "山重水尽疑无路,String encodedData = RSAUtil.publicEncrypt(str, RSAUtil.getPublicKey(publicKey));,柳暗花明又一村";
        System.out.println("明文：" + str);
        System.out.println("明文大小：" + str.getBytes().length);
        String decodedData = null;
        try {
            String encodedData = RSAUtil.publicEncrypt(str, RSAUtil.getPublicKey(publicKey));
            System.out.println("密文：" + encodedData);
            decodedData = RSAUtil.privateDecrypt(encodedData, RSAUtil.getPrivateKey(privateKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        System.out.println("解密后文字:" + decodedData);
    }
}

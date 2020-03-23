package com.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016101800718053";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCIjjxz8Fd1Bl2Y796RaYdxU83MstnQz0+FIx+9A/kBtnce8DNNeFIqJlzspsty1Hq6N+2Gzucz9sXs1P9HRGPbd+1nK1poQDiWJQtm6WUqVJMspWmr8wLmismwhSJB9u8QRAQLu/3iWN9u23ogkm15p+DgW/+GiiiXmNr/VH26qxJBmpzpkh3cIlFUEWdcIwkwyBOc36lnOpTghlXMtAX8nfujqlRc+vGRbXF8okq7Ps3IQYw77bgSn+U51pr8IQBYEOoMLjnzrLG6TD4AA17pc0NBT1vuUJORdGWR8tJWD6vhvFdRag5B7EZbKpW6A4A52P0DPMFfRiorFR4//0rlAgMBAAECggEAUhhw76LGcWxqSM+slu9CpjcjurSrjoWT4Q7EvfXtdcIky0zNCv4TJ5P4ua1tEpP7VIjbG/i3tt3dEAzdjBLplb/70dKWf6ZcXqfWuwAF+fTupiCr7twiGkfGCYGAnve9saGybPuQHswfR55bRcnCVyk+YKPucwwMa1dSEb7R966Pr28zTK6/7GXHNkXPxeh3LCq9MDhiVNKDYWeW7bPMz5Dp3BeXC0fHVy6yCngKccYaz0F2VLQoE0Nt83zQYemaSGj6VsMz5vKiPzWTKsEDDXpuCW3kc7LabGs710cwV2CR57qu27SKTWLdpnciKLIGhMzIcUnO/VPCxTpM3zxvAQKBgQDLCcYmPdIXBxfoo3OuZrXwzgz0RHwawX5ysAM8DwE3djBgOmLDNYrq77SB2FnFNRYlXhlN4JPzBvPw3roy5+wWafCKJio29331Bj8aIw65YFo+agsib3rmhneSz2NGfxsMpX+Mrl+ZgNG3Jq77jHP7RThsi2UIubaq/O0KiiGYaQKBgQCsLPkyCFAIUbPxeWGK7rge9P1Ipl/MUD+xXjwlZu03MLp5zMJf6R8g42Kxn1TprVkSuvU3F3Ly5Txl8EZUOyBoEmMZQoDmzFGblI2DtibSP3V3y35aBj8NDdyYHE9akn4MSWkWYbt1urqIfbDt6xp3WDLns91VUbMM3trFAM/vHQKBgGbf3uxFaouMZorTNqvpEsqePGYPxSnxXFZT5G+r0pv9YGmBa8Uo/egn6DfaHTIUwdHRKdURsyTyIuuqjoF4jWCV8PCugbk0ETfzIEnn9W/r6A2zwUyDW6wt+awDNHPltxjjSgtH1tx6Lsgz63k9Iw7vdbVZs6mGH43ndVtfvcHxAoGBAJhDCXlymnxQ7MY+psF7zT4rQBbQc2W3Diyn/p7BAEVPhYmTR84T1mB6VlxoYGpqz5vd1F+NEC24BgrMSxXQWB9RK2w6QM5aoEzrOZ7iJkDGt/Zh8nJglb/Qo2Wtm4uog2oQPav6cBJ0OOPd6+rrgDNMv2/iw3jhUGuwZV3VEw4NAoGBAKJtQKPiKwP9NaR8vZ+iT8AQsGDIHA0y9dg3+M7s0tTRyba3pTqZxGJlxNP8qSPhb6CJA4Fh5bUStIPehhfT1836BGEXM3V8dM2plVy2TvHSuFVFbLB0GQczv/PlgYapa5QJ8ygbWRXtUFJVqXByCj26h1XFmcQSaUWGPzaO5rf4";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArpwm7I2hzBo10EldcI/A6fzGfAEszjVPEeSHwDjc1t2HI92+DL67aUept6F3X3jvF17vge11ybHuwlmoA401xLTFifAzzH5YQs3nXie2IyNPNgvpikFI97xSB/uTHkw1K6u1g5BmD403Jz29GjSr470/8HtBoT/lSD+EpDfh3JNp1H+457xgolsDfwV4tlyuMFAoaaf8jT3Li36wzY4U+LiyV8McTr6XjjfV02c128D4Aap+RMftysOS216BjelaaCwiS016OAA/QgBaAdm5jAcafRWM/6HdoKMM411tP6W8J5x10zMU3NrDjEKUiGKAJodUm9MIWW7zEJtUvQ67ZwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


package com.bjpowernode.p2p.web;/**
 * ClassName:AlipayController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/3/20 18:23
 * @author:zh
 */

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.bjpowernode.p2p.config.AlipayConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 作者：章昊
 * 2020/3/20
 */
@Controller
public class AlipayController {


    @RequestMapping("/api/alipay")
    public String alipay(HttpServletRequest request,Model model,
                         @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                         @RequestParam(value = "total_amount",required = true)Double total_amount,
                         @RequestParam(value = "subject",required = true)String subject) throws AlipayApiException {

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);


        //这些参数应由web工程通过请求一起传递过来
        //商户订单号，商户网站订单系统中唯一订单号，必填
        /*String out_trade_no = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
        //付款金额，必填
        String total_amount = new String(request.getParameter("WIDtotal_amount").getBytes("ISO-8859-1"),"UTF-8");
        //订单名称，必填
        String subject = new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");
        //商品描述，可空
        String body = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");*/

        //通过alipayRequest接口发送请求
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        System.out.println("result="+result);

        model.addAttribute("result",result);
        return "payToAlipay";

    }

    @RequestMapping("/api/alipayQuery")
    public @ResponseBody Object alipayQuery(HttpServletRequest request,
                                            @RequestParam(value = "out_trade_no",required = true)String out_trade_no) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();


        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no  +"\"}");

        //请求
        String result = alipayClient.execute(alipayRequest).getBody();

        //{"alipay_trade_query_response":
        // {"code":"10000",
        // "msg":"Success",
        // "buyer_logon_id":"jsq***@sandbox.com",
        // "buyer_pay_amount":"0.00",
        // "buyer_user_id":"2088102180447731",
        // "buyer_user_type":"PRIVATE",
        // "invoice_amount":"0.00",
        // "out_trade_no":"202003202042458",
        // "point_amount":"0.00",
        // "receipt_amount":"0.00",
        // "send_pay_date":"2020-03-20 20:43:24",
        // "total_amount":"1500.00",
        // "trade_no":"2020032022001447730501143269",
        // "trade_status":"TRADE_SUCCESS"},
        // "sign":"rHJAKCHJokGAW9UaPmknUVGh/ziDVF23IX91i7IdCwY3qLles8pDM4zjHOSg19d0gNsT0pBGO6vY8uCXlw23DBqClOm1XhaT3jCfQKQe8epsW7Z3bkjSPDeSNY8EdeydTRuraJ/VwC0mftWO1qhNRu4M34PkkogdQ4Jr2TCRdgEB5/qk5FEkOprJFBiKeF65oLjd+fnGbOhhqw09xeQVAS3I6XOvIUfC2nAL34HbCNanSNr4QyaAaIKrAgsI9rVuZvj4jt2Sk8KnIXRvQq6AMqNyMG5Lu9gEk8In9DtS5dBEB2wtU7PIAp9OfqiqN0UuxWRpSamXIiRwcjYrljCGwg=="}

        return result;
    }
}

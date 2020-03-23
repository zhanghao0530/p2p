package com.bjpowernode.p2p.web;/**
 * ClassName:RechargeRecordController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/3/19 20:24
 * @author:zh
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.config.AlipayConfig;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.System.out;

/**
 * 作者：章昊
 * 2020/3/19
 */
@Controller
public class RechargeRecordController {

    @Reference(interfaceClass = RechargeRecordService.class,version = "1.0.0",check = false)
    private RechargeRecordService rechargeRecordService;

    @Reference(interfaceClass = RedisService.class,version = "1.0.0",check = false)
    private RedisService redisService;

    @RequestMapping("/loan/page/toRecharge")
    public String toRecharge(){
        return "toRecharge";
    }

    @RequestMapping("/loan/toAlipayRecharge")
    public String toAlipayRecharge(HttpServletRequest request, Model model,
                                 @RequestParam(value = "rechargeMoney",required = true)Double rechargeMoney){
        out.println("------------");

        String rechargeNo="";
        try {

            //生成充值记录 :
            // 用户id uid  从session中取出
            // 充值状态 recharge_status
            // 充值金额 recharge_mone
            // 充值时间 recharge_time

            // 充值订单号 recharge_no
            // 充值描述 recharge_desc
            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setUid(sessionUser.getId());
            rechargeRecord.setRechargeStatus("0"); //0代表充值中 1充值成功 2充值失败
            rechargeRecord.setRechargeTime(new Date());
            rechargeRecord.setRechargeDesc("支付宝充值");

            //订单号得是全局唯一 充值订单号=时间戳 + redis唯一数字
            rechargeNo= DateUtils.getTimestamp() + redisService.getOnlyNumber();


            rechargeRecord.setRechargeNo(rechargeNo);
            int addRechargeCount=rechargeRecordService.addRechargeRecord(rechargeRecord);
            if(addRechargeCount<=0){
                model.addAttribute("trade_msg", "生成充值记录失败");
                return "toRechargeBack";
            }

            model.addAttribute("rechargeNo", rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeDesc","支付宝充值");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg", "生成充值记录失败");
            return "toRechargeBack";
        }

        //return "redirect:http://localhost:9090/pay/api/alipay?out_trade_no="+rechargeNo+"&total_amount="+rechargeMoney+"&subject=TEST";
        return "p2pToAlipay";

    }

    @RequestMapping("/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model,
                             @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                             @RequestParam(value = "total_amount",required = true)Double total_amount) throws Exception {

        try {
            //获取支付宝GET过来反馈信息
            Map<String,String> params = new HashMap<String,String>();

            Map<String,String[]> requestParams = request.getParameterMap();

            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            //调用SDK验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);

            //——请在这里编写您的程序（以下代码仅作参考）——
            if(signVerified) {

                Map<String,Object>paramMap=new HashMap<>();
                paramMap.put("out_trade_no", out_trade_no);
                //调用pay工程的订单查询接口
                String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);

                /*{
                    "alipay_trade_query_response": {
                    "code": "10000",
                            "msg": "Success",
                            "buyer_logon_id": "jsq***@sandbox.com",
                            "buyer_pay_amount": "0.00",
                            "buyer_user_id": "2088102180447731",
                            "buyer_user_type": "PRIVATE",
                            "invoice_amount": "0.00",
                            "out_trade_no": "202003202042458",
                            "point_amount": "0.00",
                            "receipt_amount": "0.00",
                            "send_pay_date": "2020-03-20 20:43:24",
                            "total_amount": "1500.00",
                            "trade_no": "2020032022001447730501143269",
                            "trade_status": "TRADE_SUCCESS"
                },
                    "sign": "rHJAKCHJokGAW9UaPmknUVGh/ziDVF23IX91i7IdCwY3qLles8pDM4zjHOSg19d0gNsT0pBGO6vY8uCXlw23DBqClOm1XhaT3jCfQKQe8epsW7Z3bkjSPDeSNY8EdeydTRuraJ/VwC0mftWO1qhNRu4M34PkkogdQ4Jr2TCRdgEB5/qk5FEkOprJFBiKeF65oLjd+fnGbOhhqw09xeQVAS3I6XOvIUfC2nAL34HbCNanSNr4QyaAaIKrAgsI9rVuZvj4jt2Sk8KnIXRvQq6AMqNyMG5Lu9gEk8In9DtS5dBEB2wtU7PIAp9OfqiqN0UuxWRpSamXIiRwcjYrljCGwg=="
                }*/

                //将json格式的字符串转换为json对象
                JSONObject jsonObject=JSONObject.parseObject(jsonString);

                //获取alipay_trade_query_response对应的json对象
                JSONObject tradeQueryResponse=jsonObject.getJSONObject("alipay_trade_query_response");

                //获取通信标识code
                String code=tradeQueryResponse.getString("code");

                if(!StringUtils.equals("10000", code)){
                    model.addAttribute("trade_msg", "通信异常,请稍后重试");
                    return "toRechargeBack";
                }
                String tradeStatus=tradeQueryResponse.getString("trade_status");

                    /*WAIT_BUYER_PAY	交易创建，等待买家付款
                    TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
                    TRADE_SUCCESS	交易支付成功
                    TRADE_FINISHED	交易结束，不可退款*/
                    if(StringUtils.equals("TRADE_CLOSED", tradeStatus)){
                        //更新商户系统订单状态为2,充值失败
                        RechargeRecord rechargeRecord = new RechargeRecord();
                        rechargeRecord.setRechargeNo(out_trade_no);
                        rechargeRecord.setRechargeStatus("2");
                        rechargeRecordService.modifyRechargeRecordByRechargeNo(rechargeRecord);
                        model.addAttribute("trade_msg", "订单超时关闭");
                        return "toRechargeBack";
                    }
                    if(StringUtils.equals("TRADE_SUCCESS", tradeStatus)){

                        //获取订单详情
                        RechargeRecord rechargeRecord=rechargeRecordService.queryRechargeRecordByRechargeNo(out_trade_no);

                        if(StringUtils.equals(rechargeRecord.getRechargeStatus(), "0")){


                            //给用户充值[1.更新账户可用余额 2.更新充值记录的状态为1](用户标识,充值金额,充值订单号)
                            User sessionUser= (User) request.getSession().getAttribute(Constants.SESSION_USER);
                            paramMap.put("uid", sessionUser.getId());
                            paramMap.put("rechargeMoney", total_amount);
                            paramMap.put("rechargeNo", out_trade_no);
                            rechargeRecordService.reCharge(paramMap);
                        }


                }

            }else {

                model.addAttribute("trade_msg", "充值异常,请稍后重试");
                return "toRechargeBack";


            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg", "充值失败,请稍后重试");
            return "toRechargeBack";
        }

        return "forward:/loan/myCenter";
    }


    @RequestMapping("/loan/toWxpayRecharge")
    public String toWxpayRecharge(HttpServletRequest request,Model model,
                                 @RequestParam(value = "rechargeMoney",required = true)Double rechargeMoney){


        String rechargeNo="";
        try {

            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            //生成充值记录
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setUid(sessionUser.getId());
            rechargeRecord.setRechargeStatus("0");
            rechargeRecord.setRechargeTime(new Date());
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setRechargeDesc("微信充值");
            //生成订单号
            rechargeNo=rechargeNo= DateUtils.getTimestamp() + redisService.getOnlyNumber();
            rechargeRecord.setRechargeNo(rechargeNo);

            int addRechargeCount=rechargeRecordService.addRechargeRecord(rechargeRecord);
            if(addRechargeCount<=0){
                model.addAttribute("trade_msg", "生成充值记录失败");
                return "toRechargeBack";
            }
            model.addAttribute("rechargeNo", rechargeNo);
            model.addAttribute("rechargeMoney", rechargeMoney);
            model.addAttribute("rechargeDate", new Date());

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg", "生成充值记录失败");
            return "toRechargeBack";

        }
        return "showQRCode";
    }

    @RequestMapping("/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value = "rechargeNo",required = true) String rechargeNo,
                               @RequestParam(value = "rechargeMoney",required = true) Double rechargeMoney) throws Exception {

        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("body","微信扫码支付");
        paramMap.put("out_trade_no",rechargeNo);
        paramMap.put("total_fee",rechargeMoney);

        //调用pay工程的统一下单API接口
        String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);

        //将json格式的字符串转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        //获取return_code
        String returnCode = jsonObject.getString("return_code");

        //判断通信标识
        if (!StringUtils.equals("SUCCESS",returnCode)) {
            response.sendRedirect(request.getContextPath()+"/loan/toRechargeBack");
        }

        //获取业务处理结果result_code
        String resultCode = jsonObject.getString("result_code");

        //判断业务处理结果
        if (!StringUtils.equals("SUCCESS", resultCode)) {
            response.sendRedirect(request.getContextPath()+"/loan/toRechargeBack");
        }

        //获取code_url
        String codeUrl = jsonObject.getString("code_url");

        //将code_url生成一个二维码图片

        Map<EncodeHintType,Object> encodeHintTypeObjectMap = new HashMap<EncodeHintType, Object>();
        encodeHintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE,200,200,encodeHintTypeObjectMap);

        OutputStream outputStream = response.getOutputStream();

        //将矩阵对象转换成流
        MatrixToImageWriter.writeToStream(bitMatrix,"jpg",outputStream);

        outputStream.flush();
        outputStream.close();

    }



}

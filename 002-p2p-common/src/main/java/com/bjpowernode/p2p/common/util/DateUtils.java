package com.bjpowernode.p2p.common.util;/**
 * ClassName:DateUtils
 * Package:com.bjpowernode.p2p.common.util
 * Description:
 *
 * @date:2020/3/20 17:15
 * @author:zh
 */

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：章昊
 * 2020/3/20
 */
public class DateUtils {

    public static String getTimestamp() {

        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
}

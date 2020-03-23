/**
 * ClassName:QRcodeTest
 * Package:PACKAGE_NAME
 * Description:
 *
 * @date:2020/3/23 9:52
 * @author:zh
 */

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：章昊
 * 2020/3/23
 */
public class QRcodeTest {
    public static void main(String[] args) throws WriterException, IOException {
        Map<EncodeHintType,Object> encodeHintTypeObjectMap=new HashMap<EncodeHintType, Object>();
        encodeHintTypeObjectMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");


        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode("http://www.baidu.com", BarcodeFormat.QR_CODE, 200, 200,encodeHintTypeObjectMap);

        String filePath = "E://";
        String fileName = "qrcode.png";
        Path path= FileSystems.getDefault().getPath(filePath, fileName);

        //将矩阵对象转换为二维码图片
        MatrixToImageWriter.writeToPath(bitMatrix, "png", path);

        System.out.println("生成成功");
    }
}

package cn.lmcw.bookspider.impl;

import cn.lmcw.bookspider.listener.RequestInterceptor;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Random;

public class RequestImpl implements RequestInterceptor {

    private HashMap<String, String> mHeaders = new HashMap<>();

    {
        mHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
    }

    @Override
    public Connection getConnection(String url) {
        return Jsoup.connect(url).proxy(getProxy()).headers(mHeaders).header("X-Forwarded-For", getRandomIp()).followRedirects(true);
    }

    @Override
    public Proxy getProxy(Object... args) {
        return Proxy.NO_PROXY;
    }

    @Override
    public void download(String url, File outFile) throws Exception {
        int tryNum = 0;
        boolean err = false;
        do {
            if (tryNum > 10) {
                err = true;
                break;
            }
            try {
                Connection.Response response = getConnection(url)
                        .timeout(60000).ignoreContentType(true)
                        .header("referer", "").execute();
                if (response.statusCode() == 200) {
                    try {
                        FileUtils.copyInputStreamToFile(response.bodyStream(), outFile);
                        break;
                    } catch (Exception e) {
                        //e.printStackTrace();
                        tryNum++;
                    }

                } else {
                    tryNum++;
                }
            } catch (Exception e) {
                tryNum++;
            }

        } while (true);

        if (err)
            throw new RuntimeException("download err " + url);
    }

    @Override
    public long getLength(String url) {
        Connection.Response response;
        long contentLength = 0;
        try {
            response = getConnection(url)
                    .timeout(60000).ignoreContentType(true)
                    .header("referer", "").execute();

            if (response.statusCode() == 200) {
                contentLength = Long.parseLong(response.header("content-length"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentLength;

    }


    public static String getRandomIp() {

        // ip范围
        int[][] range = {
                {607649792, 608174079}, // 36.56.0.0-36.63.255.255
                {1038614528, 1039007743}, // 61.232.0.0-61.237.255.255
                {1783627776, 1784676351}, // 106.80.0.0-106.95.255.255
                {2035023872, 2035154943}, // 121.76.0.0-121.77.255.255
                {2078801920, 2079064063}, // 123.232.0.0-123.235.255.255
                {-1950089216, -1948778497}, // 139.196.0.0-139.215.255.255
                {-1425539072, -1425014785}, // 171.8.0.0-171.15.255.255
                {-1236271104, -1235419137}, // 182.80.0.0-182.92.255.255
                {-770113536, -768606209}, // 210.25.0.0-210.47.255.255
                {-569376768, -564133889}, // 222.16.0.0-222.95.255.255
        };

        Random random = new Random();
        int index = random.nextInt(10);
        return num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));
    }

    /*
     * 将十进制转换成IP地址
     */
    public static String num2ip(int ip) {
        int[] b = new int[4];
        String ipStr = "";
        b[0] = (ip >> 24) & 0xff;
        b[1] = (ip >> 16) & 0xff;
        b[2] = (ip >> 8) & 0xff;
        b[3] = ip & 0xff;
        ipStr = b[0] + "." + b[1] + "." + b[2] + "." + b[3];

        return ipStr;
    }

}

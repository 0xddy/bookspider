package cn.lmcw.bookspider.impl;

import cn.lmcw.bookspider.listener.RequestInterceptor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.net.Proxy;
import java.util.HashMap;

public class RequestImpl implements RequestInterceptor {

    private HashMap<String, String> mHeaders = new HashMap<>();

    {
        mHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
    }

    @Override
    public Connection getConnection(String url) {
        return Jsoup.connect(url).proxy(getProxy()).headers(mHeaders).followRedirects(true);
    }

    @Override
    public Proxy getProxy(Object... args) {
        return Proxy.NO_PROXY;
    }

}

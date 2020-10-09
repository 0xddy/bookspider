package cn.lmcw.bookspider.listener;

import org.jsoup.Connection;

import java.net.Proxy;

public interface RequestInterceptor extends Interceptor {
    Connection getConnection(String url);

    Proxy getProxy(Object... args);

}

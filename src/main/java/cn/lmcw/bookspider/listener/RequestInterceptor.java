package cn.lmcw.bookspider.listener;

import org.jsoup.Connection;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

public interface RequestInterceptor extends Interceptor {
    Connection getConnection(String url);

    Proxy getProxy(Object... args);

    void download(String url, File outFile) throws Exception;

    long getLength(String url);

}

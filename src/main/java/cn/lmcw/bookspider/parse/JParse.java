package cn.lmcw.bookspider.parse;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JParse {

    private static JParse jParsed;

    public static JParse getInstance() {
        synchronized (JParse.class) {
            if (jParsed == null) {
                synchronized (JParse.class) {
                    if (jParsed == null) jParsed = new JParse();
                }
            }
        }

        return jParsed;
    }

    public Document parse(String str) {
        return Jsoup.parse(str);
    }

    public Connection connect(String url) {
        return Jsoup.connect(url);
    }
}

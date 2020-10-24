package cn.lmcw.bookspider.model;

import cn.lmcw.bookspider.utils.StringUtils;
import lombok.Data;

import java.nio.charset.Charset;
import java.util.List;

@Data
public class Book {
    private String name;
    private String author;
    private int lastime;
    private String img;
    private String intro;

    private List<Chapter> chapters;

    public String getHash() {
        return StringUtils.stringToMD5(name + author);
    }

    @Data
    public static class Chapter {
        private String title;
        private String url;
    }
}

package cn.lmcw.bookspider.model;

import lombok.Data;

import java.util.List;

@Data
public class Book {
    private String name;
    private String author;
    private String lastime;
    private String img;
    private String intro;

    private List<Chapter> chapters;


    @Data
    public static class Chapter {
        private String title;
        private String url;
    }
}

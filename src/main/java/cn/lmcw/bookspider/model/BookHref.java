package cn.lmcw.bookspider.model;

import lombok.Data;

@Data
public class BookHref {
    private String name;
    private String url;
    private String author;
    /**
     * 0 未知
     * 1 连载
     * 2 完结
     */
    private int status;

    public BookHref(String name, String url, String author,int status) {
        this.name = name;
        this.url = url;
        this.author = author;
        this.status = status;
    }

}

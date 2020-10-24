package cn.lmcw.bookspider.model;

import lombok.Data;

@Data
public class Category {

    private String title;
    private int id;
    private PageDTO page;

    private int posto;


    @Data
    public static class PageDTO {
        private int start;
        private int end;

    }
}

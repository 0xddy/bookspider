package cn.lmcw.bookspider.model;

import lombok.Data;

@Data
public class Category {

    private String title;
    private int id;
    private PageDTO page;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PageDTO getPage() {
        return page;
    }

    public void setPage(PageDTO page) {
        this.page = page;
    }

    @Data
    public static class PageDTO {
        private int start;
        private int end;

    }
}

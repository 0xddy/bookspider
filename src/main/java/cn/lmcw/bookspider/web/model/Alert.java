package cn.lmcw.bookspider.web.model;

import lombok.Data;

@Data
public class Alert {
    private String css;
    private String msg;

    public Alert(String css, String msg) {
        this.css = css;
        this.msg = msg;
    }
}

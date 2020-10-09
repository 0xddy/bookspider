package cn.lmcw.bookspider.model;

import lombok.Data;

@Data
public class Project {

    private String id;
    private String name;
    private String author;
    private String version;
    private String baseUrl;

    private String fileName;

    private boolean isRun;

}

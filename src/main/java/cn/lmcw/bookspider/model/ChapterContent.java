package cn.lmcw.bookspider.model;

import cn.lmcw.bookspider.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChapterContent implements Serializable {
    private String title;
    private String content;

    public String getHash(){
        return StringUtils.stringToMD5(title);
    }

    public String toString() {
        return "{\"title\":" + title + ",\"content\":" + content + "}";
    }

}

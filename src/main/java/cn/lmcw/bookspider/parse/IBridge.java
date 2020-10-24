package cn.lmcw.bookspider.parse;

import cn.lmcw.bookspider.model.*;

import javax.script.ScriptException;
import java.util.List;

public interface IBridge {

    String getBaseUrl();

    List<Category> getCategory() throws ScriptException, NoSuchMethodException;

    String getPageUrl(int cateId, int currentPage);

    List<BookHref> getBooks(String html) throws ScriptException, NoSuchMethodException;

    Book getBookDetail(String html) throws ScriptException, NoSuchMethodException;

    ChapterContent getChapterContent(String html) throws ScriptException, NoSuchMethodException;

    Project project();

    String getCharset();

}

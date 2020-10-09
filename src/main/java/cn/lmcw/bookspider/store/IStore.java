package cn.lmcw.bookspider.store;

import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;

public interface IStore {

    Object getBookByHash(String hash);

    void saveBookUrl(BookHref bookHref, Category category, String projectId);

    int getBookUrlCount(String projectId);

}

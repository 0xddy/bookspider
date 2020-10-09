package cn.lmcw.bookspider.store;

import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;
import cn.lmcw.bookspider.web.bookurl.entity.Bookurl;
import cn.lmcw.bookspider.web.bookurl.service.IBookurlService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MysqlStoreImpl implements IStore {

    @Autowired
    IBookurlService iBookurlService;

    @Override
    public Object getBookByHash(String hash) {
        return null;
    }

    @Override
    public void saveBookUrl(BookHref bookHref, Category category, String projectId) {
        Bookurl bookurl = new Bookurl();
        bookurl.setAuthor(bookHref.getAuthor());
        bookurl.setProjectId(projectId);
        bookurl.setName(bookHref.getName());
        bookurl.setUrl(bookHref.getUrl());
        bookurl.setCategory(category.getTitle());
        bookurl.setStatus(bookHref.getStatus());

        UpdateWrapper<Bookurl> queryWrapper = new UpdateWrapper<Bookurl>()
                .setEntity(bookurl)
                .eq("name", bookurl.getName())
                .eq("author", bookurl.getAuthor())
                .eq("project_id", projectId);
        iBookurlService.saveOrUpdate(bookurl, queryWrapper);
        //System.out.println(iBookurlService.save(bookurl));
    }

    @Override
    public int getBookUrlCount(String projectId) {
        return iBookurlService.count(new QueryWrapper<Bookurl>().eq("project_id", projectId));
    }

}

package cn.lmcw.bookspider.web.bookurl.service;

import cn.lmcw.bookspider.web.bookurl.entity.Bookurl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author dingyong
 * @since 2020-10-07
 */
public interface IBookurlService extends IService<Bookurl> {
    /**
     * 获取未完结的小说
     *
     * @return
     */
    IPage<Bookurl> getUnfinishedBookUrl(String projectId, int page);
}

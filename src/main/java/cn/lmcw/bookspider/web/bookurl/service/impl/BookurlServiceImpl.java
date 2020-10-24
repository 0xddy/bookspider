package cn.lmcw.bookspider.web.bookurl.service.impl;

import cn.lmcw.bookspider.web.bookurl.entity.Bookurl;
import cn.lmcw.bookspider.web.bookurl.mapper.BookurlMapper;
import cn.lmcw.bookspider.web.bookurl.service.IBookurlService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dingyong
 * @since 2020-10-07
 */
@Service
public class BookurlServiceImpl extends ServiceImpl<BookurlMapper, Bookurl> implements IBookurlService {

    @Override
    public IPage<Bookurl> getUnfinishedBookUrl(String projectId, int page) {

        QueryWrapper<Bookurl > queryWrapper = new QueryWrapper<Bookurl>()
                .eq("project_id", projectId).orderByDesc("id");
        return getBaseMapper().selectPage(new Page<>(page, 50), queryWrapper);
    }
}

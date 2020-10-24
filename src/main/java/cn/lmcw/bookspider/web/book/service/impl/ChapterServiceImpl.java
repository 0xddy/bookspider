package cn.lmcw.bookspider.web.book.service.impl;

import cn.lmcw.bookspider.web.book.entity.SourceChapter;
import cn.lmcw.bookspider.web.book.mapper.ChapterMapper;
import cn.lmcw.bookspider.web.book.service.IChapterService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, SourceChapter> implements IChapterService {


    @Override
    public List<SourceChapter> findChaptersByBookId(int bookId) {

        QueryWrapper<SourceChapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_id", bookId).orderByAsc("id");

        return getBaseMapper().selectList(queryWrapper);
    }



}

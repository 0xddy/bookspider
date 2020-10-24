package cn.lmcw.bookspider.web.book.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "chapter")
public class SourceChapter {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private String name;
    private int bookId;
    private String path;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private int lastime;
}

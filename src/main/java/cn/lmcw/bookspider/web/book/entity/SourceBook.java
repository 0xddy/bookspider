package cn.lmcw.bookspider.web.book.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author dingyong
 * @since 2020-10-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "book")

public class SourceBook implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private String name;

    private String author;

    private int status;

    private int category_id;

    private String thumb;


    @TableField(fill = FieldFill.INSERT)
    private int lastime;

}

package cn.lmcw.bookspider.web.bookurl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author dingyong
 * @since 2020-10-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Bookurl implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 小说名称
     */
    private String name;

    private String author;

    private String url;

    private int status;

    private String category;

    private String projectId;


}

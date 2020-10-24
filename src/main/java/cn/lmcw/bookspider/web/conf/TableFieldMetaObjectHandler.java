package cn.lmcw.bookspider.web.conf;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class TableFieldMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object lastimeObject = metaObject.getValue("lastime");
        if (lastimeObject != null) {
            int intlastime = (int) lastimeObject;
            if (intlastime == 0) {
                int nowTime = Math.toIntExact(System.currentTimeMillis() / 1000);
                setFieldValByName("lastime", nowTime, metaObject);
            }
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object lastimeObject = metaObject.getValue("lastime");
        if (lastimeObject != null) {
            int intlastime = (int) lastimeObject;
            if (intlastime == 0) {
                int nowTime = Math.toIntExact(System.currentTimeMillis() / 1000);
                setFieldValByName("lastime", nowTime, metaObject);
            }
        }
    }

}

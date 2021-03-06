package latentdragon.redisorm.annotation;

import java.lang.annotation.*;

/**
 * Created by chenshaojie on 2017/12/7,16:47.
 */

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    public String name();
}

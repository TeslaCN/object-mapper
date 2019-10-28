package vip.wuweijie.wheel.objectmapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Wu Weijie
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MapTo {

    /**
     * 映射目标类名
     *
     * @return
     */
    String[] value();
}

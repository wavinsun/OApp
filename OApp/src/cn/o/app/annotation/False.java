package cn.o.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.o.app.io.OBoolean;

/**
 * IOC for Boolean property of entity
 * 
 * @see OBoolean
 * @see Boolean#FALSE
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
public @interface False {

	public String value() default "false";

}
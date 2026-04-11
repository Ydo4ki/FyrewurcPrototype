package org.fw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// doesn't really do anything, just sit there to properly indicate fields that are gonna be accessible from jval
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Insightful {
}

package org.thread.pool;

import java.lang.annotation.*;

/**
 * @author Steven
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PoolConfig {

    int timeout() default 0;

    boolean enabledCompleted() default false;
}

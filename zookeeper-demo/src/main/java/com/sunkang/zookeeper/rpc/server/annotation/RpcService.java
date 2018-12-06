package com.sunkang.zookeeper.rpc.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Project: 3.DistributedProject
 * @description:
 * @author: sunkang
 * @create: 2018-06-24 14:32
 * @ModificationHistory who      when       What
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    Class className() ;

    String verion() default "";
}

package com.sunkang.zookeeper.rpc.client;

import com.sunkang.zookeeper.rpc.client.discovery.iServiceDiscovery;

import java.lang.reflect.Proxy;

/**
 * @Project: 3.DistributedProject
 * @description:  接口的代理
 * @author: sunkang
 * @create: 2018-06-23 11:38
 * @ModificationHistory who      when       What
 **/
public class RpcProxyClient {

    public Object createProxy(Class<?> clazz, iServiceDiscovery discovery, String version){
        return Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},new RemoteInvocationHandler(discovery,version));
    }
}

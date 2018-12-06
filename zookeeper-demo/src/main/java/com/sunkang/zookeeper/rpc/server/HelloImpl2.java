package com.sunkang.zookeeper.rpc.server;

import com.sunkang.zookeeper.rpc.api.IHello;
import com.sunkang.zookeeper.rpc.server.annotation.RpcService;

/**
 * @Project: 3.DistributedProject
 * @description:
 * @author: sunkang
 * @create: 2018-06-24 14:58
 * @ModificationHistory who      when       What
 **/
@RpcService(className = IHello.class,verion = "1")
public class HelloImpl2 implements IHello {
    @Override
    public String sayHello(String msg) {
        return  "hello world 8082"+ msg;
    }
}

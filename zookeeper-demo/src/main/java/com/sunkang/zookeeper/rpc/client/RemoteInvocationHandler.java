package com.sunkang.zookeeper.rpc.client;


import com.sunkang.zookeeper.rpc.api.RpcRequest;
import com.sunkang.zookeeper.rpc.client.discovery.iServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Project: 3.DistributedProject
 * @description: 发起远程的调用
 * @author: sunkang
 * @create: 2018-06-23 11:41
 * @ModificationHistory who      when       What
 **/
public class RemoteInvocationHandler implements InvocationHandler {
    private iServiceDiscovery discovery;
    private  String version;

    public RemoteInvocationHandler(iServiceDiscovery discovery, String version) {
        this.discovery = discovery;
        this.version =version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request =new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamters(args);
        request.setVersion(version);

        //通过负载得到地址
        String address =    discovery.discovery(request.getClassName()+"-"+version);

        String[] addr =address.split(":");
        //发起请求访问
        TcpTransport  tsp =new TcpTransport(addr[0],Integer.valueOf(addr[1]));

        return tsp.send(request);
    }
}

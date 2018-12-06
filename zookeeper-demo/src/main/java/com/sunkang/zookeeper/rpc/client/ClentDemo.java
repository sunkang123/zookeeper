package com.sunkang.zookeeper.rpc.client;


import com.sunkang.zookeeper.rpc.api.IHello;
import com.sunkang.zookeeper.rpc.api.ZkConfig;
import com.sunkang.zookeeper.rpc.client.discovery.ServiceDiscoveryImpl;
import com.sunkang.zookeeper.rpc.client.discovery.iServiceDiscovery;

import java.rmi.RemoteException;

/**
 * @Project: 3.DistributedProject
 * @description:  客户端的启动的入口
 * @author: sunkang
 * @create: 2018-06-23 11:39
 * @ModificationHistory who      when       What
 **/
public class ClentDemo {
    public static void main(String[] args) throws RemoteException {
        //注册中心
        iServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl(ZkConfig.CONNECT_STRING_RUI);

        //对接口进行代理，返回代理对象
        RpcProxyClient proxyClient = new RpcProxyClient();

        for( int i= 0;i<10;i++ ){
            //创建代理
            IHello hello =(IHello) proxyClient.createProxy(IHello.class,serviceDiscovery,"1");
            //进行访问
            System.out.println(hello.sayHello("sunkang you are very cool"));
        }
    }
}

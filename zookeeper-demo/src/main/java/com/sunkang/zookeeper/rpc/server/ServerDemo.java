package com.sunkang.zookeeper.rpc.server;

import com.sunkang.zookeeper.rpc.api.IHello;
import com.sunkang.zookeeper.rpc.api.ZkConfig;
import com.sunkang.zookeeper.rpc.server.registryCenter.IregistryCenter;
import com.sunkang.zookeeper.rpc.server.registryCenter.registryCenterImpl;

import java.io.IOException;

/**
 * @Project: 3.DistributedProject
 * @description:  服务端启动的入口
 * @author: sunkang
 * @create: 2018-06-23 11:59
 * @ModificationHistory who      when       What
 **/
public class ServerDemo {

    public static void main(String[] args) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程1已经启动");
                //创建一个额服务
                IHello hello = new HelloImpl();
                //创建注册中心
                IregistryCenter registryCenter = new registryCenterImpl(ZkConfig.CONNECT_STRING_RUI);
                //服务发布
                RpcServer rpcServer = new RpcServer(registryCenter,"127.0.0.1:8081");
                //服务绑定
                rpcServer.bind(hello);
                //服务的发布
                rpcServer.publish();
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程2已经启动");
                IHello hello2 = new HelloImpl2();
                IregistryCenter registryCenter2 = new registryCenterImpl(ZkConfig.CONNECT_STRING_RUI);
                RpcServer rpcServer2 = new RpcServer(registryCenter2,"127.0.0.1:8082");
                rpcServer2.bind(hello2);
                rpcServer2.publish();
            }
        }).start();


        System.in.read();

    }
}

package com.sunkang.zookeeper.rpc.server;


import com.sunkang.zookeeper.rpc.server.annotation.RpcService;
import com.sunkang.zookeeper.rpc.server.registryCenter.IregistryCenter;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Project: 3.DistributedProject
 * @description:  服务发布
 * @author: sunkang
 * @create: 2018-06-23 11:59
 * @ModificationHistory who      when       What
 **/
public class RpcServer {
    private static  final ExecutorService cashedThreadPool = Executors.newCachedThreadPool();

    private IregistryCenter iregistryCenter;

    //registryMap来保存服务名称与真实服务实现
    private Map<String ,Object> registryMap =new HashMap<>();

    private String address ;

    public RpcServer(IregistryCenter iregistryCenter, String address) {
        this.iregistryCenter = iregistryCenter;
        this.address  =address;
    }

    //实现服务名与实例进绑定
    public void  bind(Object obj){
        RpcService annotation =  obj.getClass().getAnnotation(RpcService.class);
        String serviceName = annotation.className().getName();
        String version  = annotation.verion();
        if(version != null && !version.equals("")){
            serviceName = serviceName+"-"+version;
        }
        if(registryMap.get(serviceName) == null){
            registryMap.put(serviceName,obj);
        }
    }
    //服务的发布
    public void  publish(){
        try {
           String[] addr =  address.split(":");
           //进行注册
            for(String serviceName :registryMap.keySet()){
                iregistryCenter.Registry(serviceName,address);
            }
            //启动serverSocket进行监听
            ServerSocket serverSocket  = new ServerSocket(Integer.valueOf(addr[1]));
            while(true){
                 Socket socket =  serverSocket.accept();
                cashedThreadPool.execute(new ProcessHandler(socket,registryMap));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

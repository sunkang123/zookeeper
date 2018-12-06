package com.sunkang.zookeeper.rpc.server.registryCenter;

import com.sunkang.zookeeper.rpc.api.ZkConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @Project: 3.DistributedProject
 * @description: 服务注册中心提供注册服务
 * 提供注册节点的持久化节点的创建以及服务接口名以及服务地址的临时节点的创建
 * @author: sunkang
 * @create: 2018-06-24 14:16
 * @ModificationHistory who      when       What
 **/
public class registryCenterImpl implements IregistryCenter {

    private CuratorFramework curatorFramework;

    public registryCenterImpl(String uri) {
        //使用curator进行连接
        this.curatorFramework = CuratorFrameworkFactory.builder().connectString(uri)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();

    }

    @Override
    public void Registry(String serviceName, String address) {
        //注册相应的服务
        try {
            //第一步创建是根节点的持久化节点
            if(curatorFramework.checkExists().forPath(ZkConfig.ROOT_REGISTRY) == null){
                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ZkConfig.ROOT_REGISTRY);
            }
            //服务的临时节点
            serviceName = ZkConfig.ROOT_REGISTRY+"/"+serviceName+"/"+address;
            //注册节点号
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(serviceName);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

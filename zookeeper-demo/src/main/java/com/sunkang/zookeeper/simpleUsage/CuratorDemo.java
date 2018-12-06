package com.sunkang.zookeeper.simpleUsage;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @Project: 3.DistributedProject
 * @description: 使用curator来操作节点
 * @author: sunkang
 * @create: 2018-06-23 14:25
 * @ModificationHistory who      when       What
 **/
public class CuratorDemo {
    public static void main(String[] args) throws Exception {
        CuratorFramework curator =CuratorFrameworkFactory.builder()
                .connectString("192.168.44.129:2181")
                .connectionTimeoutMs(4000)//连接超时时间设置4秒中
                .sessionTimeoutMs(4000)//session超时设置4秒中
                .retryPolicy(new ExponentialBackoffRetry(1000,3))//设置连接的重试机制
                .namespace("curator")//设置命名空间，表明接下来的节点操作都在/curator的下进行操作
                .build();

        //启动
        curator.start();

        //1.创建节点  creatingParentsIfNeeded如果子节点的父级节点不存在，会联级创建
        curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/sunkang/test","sunkang".getBytes());
        System.out.println("创建节点成功");

        //2.获取节点的状态
        Stat state =new Stat();
        System.out.println("/sunkang/test的值： "+ new String(curator.getData().storingStatIn(state).forPath("/sunkang/test")));

        //3.设置改变节点
        curator.setData().withVersion(state.getAversion()).forPath("/sunkang/test","xx".getBytes());

        //4.获取子节点
        List<String> childrens = curator.getChildren().forPath("/sunkang");
        System.out.println("childrens : "+childrens);

        //5.检查是否存在
        Stat stat =  curator.checkExists().forPath("/sunkang");
        System.out.println("state: "+ stat);

        //6.使用watcher
        curator.getChildren().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.getPath()+"->"+event.getState()+"->"+event.getType());
            }
        }).forPath("/sunkang");
        //7.删除节点,deletingChildrenIfNeeded表示级联删除
        curator.delete().deletingChildrenIfNeeded().withVersion(stat.getVersion()).forPath("/sunkang");

        System.in.read();
    }
}

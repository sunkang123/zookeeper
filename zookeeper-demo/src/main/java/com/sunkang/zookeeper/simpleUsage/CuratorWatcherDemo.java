package com.sunkang.zookeeper.simpleUsage;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Project: 3.DistributedProject
 * @description: curator实现监听
 * @author: sunkang
 * @create: 2018-06-23 14:45
 * @ModificationHistory who      when       What
 **/
public class CuratorWatcherDemo {

    public static void main(String[] args) throws Exception {
        CuratorFramework curator =CuratorFrameworkFactory.builder()
                .connectString("192.168.44.129:2181")
                .connectionTimeoutMs(4000)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace("curator")
                .build();

        curator.start();
        //当前节点的监听
//        addListenerWhitNodeCash(curator,"/sunkang");
        //监听子节点的监听
//        addListenerWhitPathChildCash(curator,"/sunkang");
        //综合性事件
        addListenerWithTreeCache(curator,"/sunkang");
        System.in.read();

    }

    /**
     * 即节点的监听又监听子节点的监听
     * @param curator
     * @param s
     * @throws Exception
     */
    private static void addListenerWithTreeCache(CuratorFramework curator, String s) throws Exception {
       final TreeCache treeCache = new TreeCache(curator,s);
       TreeCacheListener listener = new TreeCacheListener() {
           @Override
           public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
               System.out.println("zonghe "+treeCacheEvent.getData()+";"+treeCacheEvent.getType());
           }
       };
       treeCache.getListenable().addListener(listener);
       treeCache.start();
    }

    /**
     *对给具体的节点的子节点的增加监听，子节点的删除，创建和数据节点的内容发生变化，会触发监听事件
     * @param curator
     * @param s
     * @throws Exception
     */
    private static void addListenerWhitPathChildCash(final CuratorFramework curator, String s) throws Exception {
        final PathChildrenCache pathChildrenCache =new PathChildrenCache(curator,s,true);

        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED," + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED," + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED," + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();

    }

    /**
     * 给具体的节点的增加监听,创建，删除，数据值改变
     * @param curator
     * @param s
     * @throws Exception
     */
    private static void addListenerWhitNodeCash(CuratorFramework curator, String s) throws Exception {
        final NodeCache nodeCache = new NodeCache(curator,s);
        NodeCacheListener nodeCacheListener = new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("Node data update, new data: " + new String(nodeCache.getCurrentData().getData()));
            }
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }
}

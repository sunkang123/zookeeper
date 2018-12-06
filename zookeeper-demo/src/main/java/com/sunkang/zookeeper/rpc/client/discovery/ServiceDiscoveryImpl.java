package com.sunkang.zookeeper.rpc.client.discovery;


import com.sunkang.zookeeper.rpc.api.ZkConfig;
import com.sunkang.zookeeper.rpc.client.loadBlance.RandomLoadBlance;
import com.sunkang.zookeeper.rpc.client.loadBlance.iLoadBlance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

/**
 * @Project: 3.DistributedProject
 * @description:  服务发现    主要监听对应的接口名的服务地址，获取服务地址的子节点信息，进行负载寻址地址
 * @author: sunkang
 * @create: 2018-06-24 13:32
 * @ModificationHistory who      when       What
 **/
public class ServiceDiscoveryImpl implements iServiceDiscovery {
    private List<String> reps;
    private CuratorFramework curatorFramework;

    public ServiceDiscoveryImpl(String uri) {
        this.curatorFramework = CuratorFrameworkFactory.builder().connectString(uri)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();
    }

    @Override
    public String discovery(String serviceName) {
        //获取监控的地址
        String path = ZkConfig.ROOT_REGISTRY + "/" + serviceName;
        try {
            reps = curatorFramework.getChildren().forPath(path);
            //重新检测检点的变化
            doRegistryListener(path);
            //进行负载均衡
            iLoadBlance loadBlance = new RandomLoadBlance();
            return loadBlance.getHostName(reps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void doRegistryListener(final String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework,path,true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                //对接口名的地址进行监听，发现改变，即触发该事件，在次查询取的最新的子节点的信息
              reps =  curatorFramework.getChildren().forPath(path);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}

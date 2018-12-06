package com.sunkang.zookeeper.distributedLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Project: 3.DistributedProject
 * @description:  curator的分布式锁的实现
 * @author: sunkang
 * @create: 2018-06-24 10:09
 * @ModificationHistory who      when       What
 **/
public class CuratorLocks {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework  = CuratorFrameworkFactory.builder().connectString("192.168.44.129:2181")
                .sessionTimeoutMs(4000).retryPolicy(new ExponentialBackoffRetry(4000,3))
                .build();
        curatorFramework.start();
        //InterProcessMutex这个锁为可重入锁
        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework,"/locks");
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    boolean flag = false;
                    try {
                        //尝试获取锁，最多等待5秒
                        flag = interProcessMutex.acquire(5, TimeUnit.SECONDS);
                        Thread currentThread = Thread.currentThread();
                        if(flag){
                            System.out.println("线程"+currentThread.getId()+"获取锁成功");
                        }else{
                            System.out.println("线程"+currentThread.getId()+"获取锁失败");
                        }
                        //模拟业务逻辑，延时4秒
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally{
                        if(flag){
                            try {
                                //释放锁
                                interProcessMutex.release();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }
}

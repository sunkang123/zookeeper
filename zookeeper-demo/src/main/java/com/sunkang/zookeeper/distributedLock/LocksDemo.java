package com.sunkang.zookeeper.distributedLock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Project: 3.DistributedProject
 * @description:
 * @author: sunkang
 * @create: 2018-06-24 08:44
 * @ModificationHistory who      when       What
 **/
public class LocksDemo {
    public static void main(String[] args) throws IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DistributedLocks lock = null;
                    try {
                        countDownLatch.await();
                        lock = new DistributedLocks();
                        lock.lock(); //获得锁
                        //todo  做一些事情
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();
        }
    }
}

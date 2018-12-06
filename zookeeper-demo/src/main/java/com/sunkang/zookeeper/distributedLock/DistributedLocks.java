package com.sunkang.zookeeper.distributedLock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Project: 3.DistributedProject
 * @description:  手动实现分布式锁
 * @author: sunkang
 * @create: 2018-06-24 08:19
 * @ModificationHistory who      when       What
 **/
public class DistributedLocks implements Lock,Watcher {
    private ZooKeeper zooKeeper = null;
    private String ROOT_LOCK ="/locks";
    private String CURRENT_LOCK ;
    private String WAIT_LOCK;
    private CountDownLatch  countDownLatch;

    //连接才开始操作的
    private CountDownLatch sysConectDownLatch;

    public DistributedLocks() {
        try {
            //建立zookeeper的连接
            sysConectDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper("192.168.44.129:2181",40000,this);
            sysConectDownLatch.await();
            //是否存在根节点，如果不存在，则去创建持久化节点
          Stat stat = zooKeeper.exists(ROOT_LOCK,false);
          if(stat == null){
              zooKeeper.create(ROOT_LOCK,"0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
          }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lock() {
        if(tryLock()){
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+"->获得锁成功");
            return ;
        }
        //添加监控
        waitForLocks(WAIT_LOCK);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    //尝试获取锁
    @Override
    public boolean tryLock() {
        try {
            //创建当前的节点
            CURRENT_LOCK = zooKeeper.create(ROOT_LOCK+"/","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            //获取当前的所有的子节点
            System.out.println(Thread.currentThread().getName()+"->"+ CURRENT_LOCK+"，尝试竞争锁");
            List<String> childrens =  zooKeeper.getChildren(ROOT_LOCK,false);
            SortedSet<String> treeSet =new  TreeSet<String>();
            for(String children : childrens){
                treeSet.add(ROOT_LOCK+"/"+children);
            }
            //获取最小的节点
            String minNode = treeSet.first();
            if(CURRENT_LOCK.equals(minNode)){
                return true;
            }
            //获取当前节点的上一个节点
           SortedSet<String> lessCurrentSets=  treeSet.headSet(CURRENT_LOCK);
            if(lessCurrentSets != null){
                WAIT_LOCK = lessCurrentSets.last();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    //对上一节点记性监控
    private boolean waitForLocks(String pre){
        try {
            //进行监控
            Stat stat=  zooKeeper.exists(pre,true);
            if(stat!=null){
                System.out.println("当前线程"+Thread.currentThread().getName()+"等待"+WAIT_LOCK+"释放");
                 countDownLatch = new CountDownLatch(1);
                countDownLatch.await();
                System.out.println("当前线程"+Thread.currentThread().getName()+"获得锁");
                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+ "->释放锁"+ CURRENT_LOCK);
        try {
            zooKeeper.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK=null;
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if( watchedEvent.getState().equals(Event.KeeperState.SyncConnected)){
            sysConectDownLatch.countDown();//说明已经建立了连接
        }
        //当节点的信息发送删除的时候就会触发该监控
        if(this.countDownLatch != null){
            countDownLatch.countDown();
        }
    }
}

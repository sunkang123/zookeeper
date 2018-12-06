package com.sunkang.zookeeper.simpleUsage;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Project: 3.DistributedProject
 * @description: 用原生zookeeper客户端的api
 * @author: sunkang
 * @create: 2018-06-23 13:04
 * @ModificationHistory who      when       What
 **/
public class OrginZookeeperConnectionDemo {

    public static void main(String[] args) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Watcher watcher =  new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if( watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    //连接成功会有SyncConnected事件产生
                    System.out.println("默认事件"+watchedEvent.getPath()+"->"+watchedEvent.getState()+"->"+watchedEvent.getType());
                    //如果收到了服务端的响应事件，连接成功，接下来才可以对zookeeper的数据节点进行操作
                    countDownLatch.countDown();
                }
            }};

        final ZooKeeper zooKeeper = new ZooKeeper("192.168.44.129:2181", 4000,watcher );

        countDownLatch.await();

        zooKeeper.exists("/zk-test-create", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getPath()+"->"+watchedEvent.getState()+"->"+watchedEvent.getType());
                //再次绑定
                try {
                    //这里会触发默认的全局事件
                    zooKeeper.exists(watchedEvent.getPath(),true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        //1.创建临时节点
        zooKeeper.create("/zk-test-create","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        Thread.sleep(1000);
        System.out.println("添加节点成功");


        Stat state = new Stat();
        //2.得到当前节点的值
        byte[]  bytes =  zooKeeper.getData("/zk-test-create",null,state);
        System.out.println("createNode的当前的值为："+ new String(bytes));

        //3.修改当前节点的值
        zooKeeper.setData("/zk-test-create","2".getBytes(),state.getVersion());
        //得到当前节点的值
        byte[]  byte1s =  zooKeeper.getData("/zk-test-create",null,state);
        System.out.println("createNode的修改后值为 : "+ new String(byte1s));


        //4.查看子节点
        List<String> childrenList =  zooKeeper.getChildren("/",false);
        System.out.println("childrenList: "+childrenList);

        //5.删除节点的值
        zooKeeper.delete("/zk-test-create",state.getVersion());


        //6.设置权限认证
        zooKeeper.addAuthInfo("digest","foo:true".getBytes());
        zooKeeper.create("/zk-book-auth_test","init".getBytes(),ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.EPHEMERAL);

        //7.新建客户端需要认证获取对应的节点
       byte[] authBytes =  zooKeeper.getData("/zk-book-auth_test",false,null);
        System.out.println("/zk-book-auth_test的value:"+ new String(authBytes) );

        zooKeeper.close();
//        System.in.read();
    }
}

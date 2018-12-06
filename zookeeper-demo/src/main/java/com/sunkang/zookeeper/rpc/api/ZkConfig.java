package com.sunkang.zookeeper.rpc.api;

/**
 * @Project: 3.DistributedProject
 * @description: 注册中心的基本配置
 * @author: sunkang
 * @create: 2018-06-24 13:37
 * @ModificationHistory who      when       What
 **/
public class ZkConfig {
    //zookpeer注册中心的地址
    public final static String CONNECT_STRING_RUI= "192.168.44.129:2181,192.168.44.130:2181,192.168.44.131:2181";

    //注册的根节点的名称
    public final static  String ROOT_REGISTRY="/registries";
}

package com.sunkang.zookeeper.rpc.server.registryCenter;

/**
 * @Project: 3.DistributedProject
 * @description:
 * @author: sunkang
 * @create: 2018-06-24 14:16
 * @ModificationHistory who      when       What
 **/
public interface IregistryCenter {


    void Registry(String serviceName, String address);
}

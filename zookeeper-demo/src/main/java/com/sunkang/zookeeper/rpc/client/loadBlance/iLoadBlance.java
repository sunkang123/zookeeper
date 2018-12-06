package com.sunkang.zookeeper.rpc.client.loadBlance;

import java.util.List;

/**
 * @Project: 3.DistributedProject
 * @description:
 * @author: sunkang
 * @create: 2018-06-24 13:22
 * @ModificationHistory who      when       What
 **/
public interface iLoadBlance {

    String getHostName(List<String> reps);
}

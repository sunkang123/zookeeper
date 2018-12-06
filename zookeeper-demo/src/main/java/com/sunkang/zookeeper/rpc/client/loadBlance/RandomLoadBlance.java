package com.sunkang.zookeeper.rpc.client.loadBlance;

import java.util.List;
import java.util.Random;

/**
 * @Project: 3.DistributedProject
 * @description:
 * @author: sunkang
 * @create: 2018-06-24 13:28
 * @ModificationHistory who      when       What
 **/
public class RandomLoadBlance extends  AbstractloadBlance {
    @Override
    public String doSelect(List<String> reps) {
        Random random = new Random();
        return reps.get(random.nextInt(reps.size()));
    }
}

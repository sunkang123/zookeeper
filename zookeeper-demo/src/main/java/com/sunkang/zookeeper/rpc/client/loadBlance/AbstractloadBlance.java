package com.sunkang.zookeeper.rpc.client.loadBlance;

import java.util.List;

/**
 * @Project: 3.DistributedProject
 * @description:
 * @author: sunkang
 * @create: 2018-06-24 13:25
 * @ModificationHistory who      when       What
 **/
public abstract class AbstractloadBlance  implements iLoadBlance {
    @Override
    public String getHostName(List<String> reps) {
        if(reps == null){
            return null;
        }
        if(reps.size() == 1){
            return reps.get(0);
        }

        String rep = doSelect(reps);
        return rep;
    }

    public abstract String doSelect(List<String> reps);
}

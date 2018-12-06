package com.sunkang.zookeeper.rpc.api;

import java.io.Serializable;

/**
 * @Project: 3.DistributedProject
 * @description:  模拟请求的实体
 * @author: sunkang
 * @create: 2018-06-23 11:33
 * @ModificationHistory who      when       What
 **/
public class RpcRequest implements Serializable {
    //类名
    private String className;
    //方法名
    private String methodName;
    //参数值
    private Object[] paramters;
    //版本号
    private String version;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParamters() {
        return paramters;
    }

    public void setParamters(Object[] paramters) {
        this.paramters = paramters;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

package com.sunkang.zookeeper.rpc.server;

import com.sunkang.zookeeper.rpc.api.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

/**
 * @Project: 3.DistributedProject
 * @description: 处理客户端发来的请求
 * @author: sunkang
 * @create: 2018-06-23 12:37
 * @ModificationHistory who      when       What
 **/
public class ProcessHandler  implements  Runnable {
    private Socket socket;
    private Map<String ,Object> registryMap ;
    public ProcessHandler(Socket socket,Map<String ,Object> registryMap) {
        this.socket = socket;
        this.registryMap = registryMap;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            //从socket获取相应的读流
            ois = new ObjectInputStream(socket.getInputStream());
            //获取流的对象
            RpcRequest request = (RpcRequest) ois.readObject();
            //进行实际对象的调用
            Object obj = invoke(request);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private Object invoke(RpcRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object[] types = request.getParamters();
        Class<?>[] methodParamters = new Class<?>[types.length];
        for (int i = 0; i < types.length; i++) {
            methodParamters[i] = types[i].getClass();
        }
       String version =  request.getVersion();
       Object service =  registryMap.get(request.getClassName()+"-"+version);
       //通过反射进行进行调用，获取对应的方法
        Method method = service.getClass().getMethod(request.getMethodName(), methodParamters);
        //调用
        return method.invoke(service, types);
    }

}

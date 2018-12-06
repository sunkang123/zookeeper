package com.sunkang.zookeeper.rpc.client;



import com.sunkang.zookeeper.rpc.api.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Project: 3.DistributedProject
 * @description:  传输层的发送消息，然后接受消息的处理
 * @author: sunkang
 * @create: 2018-06-23 11:45
 * @ModificationHistory who      when       What
 **/
public class TcpTransport {
    private String host;
    private int port;

    public TcpTransport(String host, int port) {
        this.host=host;
        this.port=port;
    }
    private Socket newSocket(){
        try {
            Socket socket = new Socket(host,port);
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object send(RpcRequest request) {
        //创建一个socket连接
        Socket socket = newSocket();
        ObjectOutputStream oos = null ;
        ObjectInputStream ois =null;
        try {
             oos = new ObjectOutputStream(socket.getOutputStream());
             //发起请求
             oos.writeObject(request);
             ois = new ObjectInputStream(socket.getInputStream());
             //读到返回值
            Object obj =  ois.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
             if(socket!=null){
                 try {
                     socket.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             if(oos != null){
                 try {
                     oos.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             if(ois !=null){
                 try {
                     ois.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }
        return null;
    }
}

package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 基于java自带的Rpc远程调用接口Remote 自定义客户端接口
 * 底层通信基于Socket通信
 */
public interface RmiServer extends Remote {

    /**
     * 需要抛出远程调用过程中出现的异常信息
     * @param params
     * @return
     * @throws RemoteException
     */
    String handler(String params) throws RemoteException;

    Integer rmiSercverPort=8080;

    String rmiRemoteUrl="rmi://localhost:"+rmiSercverPort+"/handlerParams";
}

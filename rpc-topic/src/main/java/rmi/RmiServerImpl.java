package rmi;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.util.Date;

/**
 * 自定义的远程客户端接口实现类 需要实现UnicastRemoteObject
 */
public class RmiServerImpl extends UnicastRemoteObject implements  RmiServer {

    public RmiServerImpl() throws RemoteException {
    }

    @Override
    public String handler(String params) throws RemoteException {
        return "RmiServer handler time :"+
                DateFormat.getTimeInstance(2).format( new Date(System.currentTimeMillis()))
                +">>params :"+params;
    }


}

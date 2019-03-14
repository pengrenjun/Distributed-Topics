package rmi;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * rmi server 端启动
 */
public class RunRmiServer {

    public static void main(String[] args) {

        try {
            RmiServer rmiServer=new RmiServerImpl();

            //server 端注册端口
            Registry registry = LocateRegistry.createRegistry(RmiServer.rmiSercverPort);


            Naming.bind(RmiServer.rmiRemoteUrl,rmiServer);

            System.out.println("rmiServer start!");
        } catch (RemoteException | AlreadyBoundException | MalformedURLException e) {
            e.printStackTrace();
        }


    }
}

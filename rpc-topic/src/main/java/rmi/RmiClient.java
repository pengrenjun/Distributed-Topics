package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * rmi 客户端启动访问远程服务端获取信息
 */
public class RmiClient {

    public static void main(String[] args) {

        try {
            RmiServer rmiServer=(RmiServer) Naming.lookup(RmiServer.rmiRemoteUrl);

            String handlerResault=rmiServer.handler("id：123");

            System.out.println(handlerResault);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

}

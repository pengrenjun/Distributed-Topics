package zookeeper.javaApi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Description 与zookeeper集群服务服建立会话连接
 * @Date 2019/4/29 0029 下午 3:05
 * @Created by Pengrenjun
 */
public class CreateSessionDemo {

    private static String connectAddresses="10.0.99.197:2181,10.0.99.171:2181,10.0.99.177:2181";

    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {

        ZooKeeper zooKeeper=new ZooKeeper(connectAddresses, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
              //如果当前连接成功,通过计数器去控制
              if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                  countDownLatch.countDown();
                  System.out.println(watchedEvent.getState());
              }

            }
        });
        countDownLatch.await();
        System.out.println(zooKeeper.getState());
    }
}

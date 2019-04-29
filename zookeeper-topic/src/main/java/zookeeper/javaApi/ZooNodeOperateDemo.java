package zookeeper.javaApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Description zookeeper节点操作实例
 * @Date 2019/4/29 0029 下午 3:05
 * @Created by Pengrenjun
 */
public class ZooNodeOperateDemo {

    private static String connectAddresses="10.0.99.197:2181,10.0.99.171:2181,10.0.99.177:2181";

    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        ZooKeeper zooKeeper=new ZooKeeper(connectAddresses, 5000, new Watcher() {
            //建立监听事件
            @Override
            public void process(WatchedEvent watchedEvent) {
              //如果当前连接成功,通过计数器去控制
              if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                  countDownLatch.countDown();
                  System.out.println(watchedEvent.getState()+"------->"+watchedEvent.getType());
              }

            }
        });
        countDownLatch.await();
        System.out.println(zooKeeper.getState());


        //创建节点
        try {
            //CreateMode.PERSISTENT 创建的节点可以在远程连接中查到
            //String nodeA = zooKeeper.create("/nodeA", "javaCreat".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            String node = zooKeeper.create("/nodeB", "javaCreat".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("节点创建成功"+node);
        } catch (KeeperException e) {
            e.printStackTrace();
        }

        //修改节点
        Stat stat = zooKeeper.setData("/nodeB", "javaChange".getBytes(), -1);
        System.out.println(stat);
    }
}

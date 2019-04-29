package zookeeper.michael.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 权限控制示例代码 设置数据节点的操作的权限
 * 腾讯课堂搜索 咕泡学院
 * 加群获取视频：608583947
 * 风骚的Michael 老师
 */
public class AuthControlDemo implements Watcher{
    private final static String CONNECTSTRING="10.0.99.197:2181,10.0.99.171:2181,10.0.99.177:2181";
    private static CountDownLatch countDownLatch=new CountDownLatch(1);
    private static CountDownLatch countDownLatch2=new CountDownLatch(1);

    private static ZooKeeper zookeeper;
    private static Stat stat=new Stat();
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zookeeper=new ZooKeeper(CONNECTSTRING, 5000, new AuthControlDemo());
        countDownLatch.await();

        // acl (create /delete /admin /read/write)
        //权限模式： ip 指定哪些Ip可以访问 /Digest（username:password） 账号密码形式的控制 /world 开放的权限 数据节点的访问权限对所有用户开放
        // /super 超级用户 可以对所有节点进行操作

        ACL acl=new ACL(ZooDefs.Perms.CREATE, new Id("digest","root:Rl#97ssc"));
        ACL acl2=new ACL(ZooDefs.Perms.CREATE, new Id("ip","10.0.99.197"));

        List<ACL> acls=new ArrayList<>();
        acls.add(acl);
        acls.add(acl2);
        zookeeper.create("/auth3","123".getBytes(),acls,CreateMode.PERSISTENT);

        zookeeper.addAuthInfo("digest","root:Rl#97ssc".getBytes());
        zookeeper.create("/auth3","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        zookeeper.create("/auth3/auth3-1","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.EPHEMERAL);

    }

    @Test
    public void deleteNode() throws IOException, KeeperException, InterruptedException {
        zookeeper=new ZooKeeper(CONNECTSTRING, 5000, new AuthControlDemo());
        zookeeper.addAuthInfo("digest","root:Rl#97ssc".getBytes());
        zookeeper.delete("/auth3",-1);
    }


    public void process(WatchedEvent watchedEvent) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
            if(Event.EventType.None==watchedEvent.getType()&&null==watchedEvent.getPath()){
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState()+"-->"+watchedEvent.getType());
            }
        }

    }
}

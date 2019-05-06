package zookeeper.michael.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryOneTime;

/**
 * 腾讯课堂搜索 咕泡学院
 * 加群获取视频：608583947
 * 风骚的Michael 老师
 */
public class CuratorClientUtils {


    public final static String CONNECTSTRING="10.0.99.197:2181,10.0.99.171:2181," +
            "10.0.99.177:2181";


    public static CuratorFramework getInstance(){
        CuratorFramework curatorFramework= CuratorFrameworkFactory.
                newClient(CONNECTSTRING,5000,5000,
                        new ExponentialBackoffRetry(1000,3));
        curatorFramework.start();
        return curatorFramework;
    }

    public static CuratorFramework getInstance(String threadInfo){
        CuratorFramework  curatorFramework= CuratorFrameworkFactory.
                newClient(CONNECTSTRING,5000,5000,
                       new RetryOneTime(1000));
        curatorFramework.start();
        System.out.println("zookeeper Client [" + threadInfo + "] Server connected...");
        return curatorFramework;
    }


}

package zookeeper.michael.zkclient;

import org.I0Itec.zkclient.ZkClient;

/**
 * 腾讯课堂搜索 咕泡学院
 * 加群获取视频：608583947
 * 风骚的Michael 老师
 */
public class SessionDemo {

    private final static String CONNECTSTRING="10.0.99.197:2181,10.0.99.171:2181,10.0.99.177:2181";

    public static void main(String[] args) {
        ZkClient zkClient=new ZkClient(CONNECTSTRING,4000);

        System.out.println(zkClient+" - > success");
    }
}

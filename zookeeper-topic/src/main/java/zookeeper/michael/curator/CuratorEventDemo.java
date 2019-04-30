package zookeeper.michael.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * 腾讯课堂搜索 咕泡学院
 * 加群获取视频：608583947
 * 风骚的Michael 老师
 */
public class CuratorEventDemo {

    /**
     * 三种watcher来做节点的监听
     * pathcache   监视一个路径下子节点的创建、删除、节点数据更新
     * NodeCache   监视一个节点的创建、更新、删除
     * TreeCache   pathcaceh+nodecache 的合体（监视路径下的创建、更新、删除事件），
     * 缓存路径下的所有子节点的数据
     */

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework=CuratorClientUtils.getInstance();

        /**
         * 监听节点变化NodeCache
         */
        //NodeCacheTest(curatorFramework);


        /**
         * PatchChildrenCache
         */

        PatchChildrenCacheTest(curatorFramework);


    }

    private static void PatchChildrenCacheTest(CuratorFramework curatorFramework) throws Exception {
        PathChildrenCache cache=new PathChildrenCache(curatorFramework,"/event",true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        // Normal 异步初始化cache / BUILD_INITIAL_CACHE  同步初始化客户端的cache，及创建cache后，就从服务器端拉入对应的数据
        // /POST_INITIALIZED_EVENT 异步初始化，初始化完成触发事件

        cache.getListenable().addListener((curatorFramework1,pathChildrenCacheEvent)->{
//                    CHILD_ADDED,
//                    CHILD_UPDATED,
//                    CHILD_REMOVED,
//                    CONNECTION_SUSPENDED,
//                    CONNECTION_RECONNECTED,
//                    CONNECTION_LOST,
//                    INITIALIZED;
            switch (pathChildrenCacheEvent.getType()){
                case CHILD_ADDED:
                    System.out.println("增加子节点");
                    break;
                case CHILD_REMOVED:
                    System.out.println("删除子节点");
                    break;
                case CHILD_UPDATED:
                    System.out.println("更新子节点");
                    break;
                default:break;
            }
        });


        curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath("/event","event".getBytes());
        TimeUnit.SECONDS.sleep(1);
        System.out.println("1");
        curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath("/event/event1","1".getBytes());
        TimeUnit.SECONDS.sleep(1);
        System.out.println("2");

        curatorFramework.setData().forPath("/event/event1","222".getBytes());
        TimeUnit.SECONDS.sleep(1);
        System.out.println("3");

        curatorFramework.delete().forPath("/event/event1");
        System.out.println("4");

        System.in.read();
    }

    private static void NodeCacheTest(CuratorFramework curatorFramework) throws Exception {
        NodeCache cache=new NodeCache(curatorFramework,"/curator",false);
        cache.start(true);

        cache.getListenable().addListener(()-> System.out.println("节点数据发生变化,变化后的结果" +
                "："+new String(cache.getCurrentData().getData())));

        curatorFramework.setData().forPath("/curator","菲菲".getBytes());

        //等待当前进程
        System.in.read();
    }
}

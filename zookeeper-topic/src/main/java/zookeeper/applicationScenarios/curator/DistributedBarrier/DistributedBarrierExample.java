package zookeeper.applicationScenarios.curator.DistributedBarrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.utils.CloseableUtils;
import zookeeper.michael.curator.CuratorClientUtils;

/**
 * @Description: Curator中提供DistributedBarrier就是用来实现分布式Barrier的
 * DistributedBarrier类实现了屏障的功能，但是我们并不知道要什么时候移除屏障
 * @Author：pengrj
 * @Date : 2019/5/2 0002 20:45
 * @version:1.0
 */
public class DistributedBarrierExample {
    private static final String PATH = "/curatorBarrier";

    /** 客户端数量 */
    private static final int CLIENT_COUNT = 5;

    private static DistributedBarrier barrier;

    public static void main(String[] args) throws Exception {

        CuratorFramework client =CuratorClientUtils.getInstance();

        for(int i=0;i<CLIENT_COUNT;i++){
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //获取DistributedBarrier
                        barrier = new DistributedBarrier(client, PATH);
                        //首先你需要设置屏障，它将阻塞运行到此的当前线程:
                        barrier.setBarrier();
                        System.out.println("线程" +index+" 等待");
                        //然后需要阻塞的线程调用，‘方法等待放行条件’，如果连接丢失,此方法将抛出异常
                        barrier.waitOnBarrier();

                        System.out.println("线程" +index+" 已执行");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        //主线程等地10s之后 所有等待的线程开始执行
        Thread.sleep(10*1000);

        if(barrier != null){
            //当条件满足时（DistributedBarrier是不知道条件的），移除屏障，所有等待的线程将继续执行
            barrier.removeBarrier();
            System.out.println("所有线程都已到达,准备启动");
        }

        Thread.sleep(10*1000);
        CloseableUtils.closeQuietly(client);
    }

}

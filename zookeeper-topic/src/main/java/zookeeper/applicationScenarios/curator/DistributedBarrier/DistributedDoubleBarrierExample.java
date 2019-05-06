package zookeeper.applicationScenarios.curator.DistributedBarrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import zookeeper.michael.curator.CuratorClientUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: Curator还提供了另一种线程自发触发Barrier释放的模式：双重屏障，
 * 在协作开始之前同步，当足够数量的进程加入到屏障后，开始协作，
 * 当所有进程完毕后离开屏障
 * @Author：pengrj
 * @Date : 2019/5/2 0002 21:03
 * @version:1.0
 */
public class DistributedDoubleBarrierExample {
    private static final String PATH = "/curatorBarrier/doubleBarrier";

    //public DistributedDoubleBarrier(CuratorFramework client, String barrierPath, int memberQty);
    //memberQty是成员数量，当enter()方法被调用时，成员被阻塞，直到所有的成员都调用了enter()。
    // 当leave()方法被调用时，它也阻塞调用线程， 直到所有的成员都调用了leave()。
    //就像赛跑比赛， 发令枪响， 所有的参赛者开始跑，等所有的参赛者跑过终点线，比赛才结束。
    //DistributedDoubleBarrier 会监控连接状态，当连接断掉时enter()和leave方法会抛出异常。


    /** 客户端数量 */
    private static final int CLIENT_COUNT = 5;

    public static void main(String[] args) throws Exception {

        CuratorFramework client = CuratorClientUtils.getInstance();
        AtomicInteger atomicInteger=new AtomicInteger();
        for(int i=0;i<CLIENT_COUNT;i++){
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //获取DistributedDoubleBarrier
                        DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, PATH,CLIENT_COUNT);
                        System.out.println("线程" +index+" 等待");
                        //调用enter阻塞,直到所有线程都到达之后执行,执行完毕之后，调用leave阻塞,直到所有线程都调用leave
                        barrier.enter();

                        System.out.println("线程" +index+" 已执行");
                        Thread.sleep(index*1000);
                        atomicInteger.getAndIncrement();
                        barrier.leave();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        while(true){
            Thread.sleep(1000);
            System.out.println(atomicInteger.get());
        }

    }
}

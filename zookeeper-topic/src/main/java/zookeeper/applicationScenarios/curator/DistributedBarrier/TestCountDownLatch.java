package zookeeper.applicationScenarios.curator.DistributedBarrier;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: CyclicBarrier:是一个同步的辅助类，允许一组线程相互之间等待，
 * 达到一个共同点，再继续执行
 * @Author：pengrj
 * @Date : 2019/5/2 0002 20:37
 * @version:1.0
 */
public class TestCountDownLatch {
    //使用jdk的CyclicBarrier模拟赛跑
    /** 参赛人数 */
    public static Integer RUNNER_COUNT = 3;

    public static CountDownLatch countDownLatch=new CountDownLatch(RUNNER_COUNT);
    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for(int i=1; i<=RUNNER_COUNT; i++){

            final int index = i;
            executor.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    countDownLatch.countDown();
                    System.out.println("参赛者"  + index+ "准备好了.");
                    try {
                        countDownLatch.await();
                    } catch (Exception e) {}

                    System.out.println("参赛者"  + index+" 开跑！");
                }
            }));
        }
        executor.shutdown();
    }

}

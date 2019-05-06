package zookeeper.applicationScenarios.curator.DistributedAtomic;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryNTimes;
import zookeeper.michael.curator.CuratorClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description: DistributedAtomicInteger和SharedCount计数范围是一样的,都是int类型
 * @Author：pengrj
 * @Date : 2019/5/2 0002 21:53
 * @version:1.0
 */
public class DistributedAtomicLongExample {
    //DistributedAtomicInteger和DistributedAtomicLong
    // 和上面的计数器的实现有显著的不同，它首先尝试使用乐观锁的方式设置计数器，
    // 如果不成功(比如期间计数器已经被其它client更新了)，
    // 它使用InterProcessMutex方式来更新计数值

    //此计数器有一系列的操作：
    //get(): 获取当前值
    //increment()： 加一
    //decrement(): 减一
    //add()： 增加特定的值
    //subtract(): 减去特定的值
    //trySet(): 尝试设置计数值
    //forceSet(): 强制设置计数值

    //你必须检查返回结果的succeeded()， 它代表此操作是否成功。
    // 如果操作成功， preValue()代表操作前的值， postValue()代表操作后的值。

    private static final int CLIENT_COUNT = 5;
    private static final String PATH = "/curatorCounter/atomic";
    public static void main(String[] args) throws Exception {
        try{
            CuratorFramework client =CuratorClientUtils.getInstance();
            List<DistributedAtomicLong> examples = new ArrayList<>();
            ExecutorService service = Executors.newFixedThreadPool(CLIENT_COUNT);
            for (int i = 0; i < CLIENT_COUNT; ++i) {
                final DistributedAtomicLong count = new DistributedAtomicLong
                        (client, PATH, new RetryNTimes(10, 10));

                examples.add(count);

                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {
                            AtomicValue<Long> value = count.increment();
                            System.out.println("操作是否成功: " + value.succeeded());
                            if (value.succeeded()){
                                System.out.println("操作成功: 操作前的值为： " + value.preValue() + " 操作后的值为： " + value.postValue());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                service.submit(task);
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

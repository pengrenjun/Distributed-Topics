package zookeeper.applicationScenarios.curator.DistributedAtomic;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import zookeeper.michael.curator.CuratorClientUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description: SharedCount代表计数器
 * @Author：pengrj
 * @Date : 2019/5/2 0002 21:24
 * @version:1.0
 */
public class SharedCounterExample implements SharedCountListener {
    //SharedCount代表计数器，可以为它增加一个SharedCountListener，
    // 当计数器改变时此Listener可以监听到改变的事件，
    // 而SharedCountReader可以读取到最新的值，
    // 包括字面值和带版本信息的值VersionedValue。
    // SharedCount必须调用start()方法开启，使用完之后必须调用close关闭它。


    //客户端数量
    private static final int CLIENT_COUNT = 5;
    private static final String PATH = "/curatorCounter/SharedCounter";

    public static void main(String[] args) throws Exception {
    /*在这个例子中，我们使用baseCount来监听计数值(addListener方法)。
    任意的SharedCount， 只要使用相同的path，都可以得到这个计数值。
    然后我们使用5个线程为计数值增加一个10以内的随机数。

    注意：可能测试的时候，有时候莫名其妙第一条打印了State changed: CONNECTED之后
    立马打印了一条 Counter’s value is changed to XX 不要大惊小怪 这是因为 你上一次运行之后 zk上的节点没有清除,
    所以会先把上次的结果打印出来,如果是为了测试,建议每次运行完main()程序之后,删除zk上的节点信息。*/

        final Random rand = new Random();
        SharedCounterExample example = new SharedCounterExample();
        CuratorFramework client = CuratorClientUtils.getInstance();
        try {
            //使用baseCount来监听计数值(addListener方法)
            SharedCount baseCount = new SharedCount(client, PATH, 0);
            baseCount.addListener(example);
            baseCount.start();

            List<SharedCount> examples = Lists.newArrayList();
            ExecutorService service = Executors.newFixedThreadPool(CLIENT_COUNT);

            for (int i = 0; i < CLIENT_COUNT; i++) {

                final SharedCount count = new SharedCount(client, PATH, 0);
                examples.add(count);
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        count.start();
                        Thread.sleep(rand.nextInt(10000));
                        System.out.println("获取到的当前值："+count.getCount());
                        int add = count.getCount() + 1;
                        System.out.println("要更改的值为: " + add);
                        boolean b = count.trySetCount(count.getVersionedValue(), add);
                        System.out.println("更改结果为: " + b);
                        return null;
                    }
                };
                service.submit(task);
            }

            service.shutdown();
            service.awaitTermination(50, TimeUnit.MINUTES);


            for (int i = 0; i < CLIENT_COUNT; i++) {
                examples.get(i).close();
            }
            baseCount.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            client.delete().deletingChildrenIfNeeded().forPath("/curatorCounter/SharedCounter");
        }
    }

    @Override
    public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
        System.out.println("State changed: " + arg1.toString());
    }

    @Override
    public void countHasChanged(SharedCountReader sharedCount, int newCount) throws Exception {
        System.out.println("Counter's value is changed to " + newCount);
        System.out.println("---------------------");
    }

}

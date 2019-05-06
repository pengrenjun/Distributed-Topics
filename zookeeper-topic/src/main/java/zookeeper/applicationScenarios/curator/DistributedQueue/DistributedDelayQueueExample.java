package zookeeper.applicationScenarios.curator.DistributedQueue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.DistributedDelayQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.util.Date;

/**
 * @Description: DistributedDelayQueue是延时队列。
 * 元素有个delay值， 消费者隔一段时间才能收到元素。
 * @Author：pengrj
 * @Date : 2019/5/2 0002 20:24
 * @version:1.0
 */
public class DistributedDelayQueueExample {

    private static final String PATH = "/DistributedQueue/Delayqueue";
    public static void main(String[] args) throws Exception {
        TestingServer server = new TestingServer();
        CuratorFramework client = null;
        DistributedDelayQueue<String> queue = null;
        try {
            client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.getCuratorListenable().addListener(new CuratorListener() {
                @Override
                public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                    System.out.println("CuratorEvent: " + event.getType().name());
                }
            });
            client.start();
            QueueConsumer<String> consumer = createQueueConsumer();
            QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, createQueueSerializer(), PATH);
            queue = builder.buildDelayQueue();
            queue.start();

            for (int i = 0; i < 10; i++) {
                //注意delayUntilEpoch不是离现在的一个时间间隔，而是未来的一个时间戳，如 :
                //System.currentTimeMillis() + 10秒。
                //如果delayUntilEpoch的时间已经到达，消息会立刻被消费者接收。
                queue.put("test-" + i, System.currentTimeMillis() + 10000);
            }
            System.out.println(new Date().getTime() + ": already put all items");


            Thread.sleep(20000);

        } catch (Exception ex) {
        } finally {
            CloseableUtils.closeQuietly(queue);
            CloseableUtils.closeQuietly(client);
            CloseableUtils.closeQuietly(server);
        }
    }
    private static QueueSerializer<String> createQueueSerializer() {
        return new QueueSerializer<String>() {
            @Override
            public byte[] serialize(String item) {
                return item.getBytes();
            }
            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        };
    }
    private static QueueConsumer<String> createQueueConsumer() {
        return new QueueConsumer<String>() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                System.out.println("connection new state: " + newState.name());
            }
            @Override
            public void consumeMessage(String message) throws Exception {
                System.out.println(new Date().getTime() + ": consume one message: " + message);
            }
        };
    }

}

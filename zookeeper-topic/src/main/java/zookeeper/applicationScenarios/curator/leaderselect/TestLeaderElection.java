package zookeeper.applicationScenarios.curator.leaderselect;

import org.apache.curator.framework.CuratorFramework;
import zookeeper.michael.curator.CuratorClientUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 多个客户端测试 公平竞争
 * @Author：pengrj
 * @Date : 2019/5/2 0002 9:33
 * @version:1.0
 */
public class TestLeaderElection {
    private static final String PATH = "/curator/leaderSeq";
    /** 3个客户端 */
    private static final Integer CLIENT_COUNT = 3;

    public static void main(String[] args) throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(CLIENT_COUNT);

        for (int i = 0; i < CLIENT_COUNT; i++) {
            final int index = i;
            service.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        new TestLeaderElection().schedule(index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

         System.in.read();
         service.shutdownNow();
    }

    private void schedule(final int thread) throws Exception {
        CuratorFramework client = CuratorClientUtils.getInstance("clint_"+Integer.toString(thread));
        CustomLeaderSelectorListenerAdapter leaderSelectorListener =
                new CustomLeaderSelectorListenerAdapter(client, PATH, "Client #" + thread);
        leaderSelectorListener.start();

        Thread.sleep(7000);
        if(thread==0){
            client.close();
        }

    }


}

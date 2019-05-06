package zookeeper.applicationScenarios.curator.leaderselect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import zookeeper.michael.curator.CuratorClientUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: Curator中选举  Leader Latch
 * @Author：pengrj
 * @Date : 2019/5/1 0001 21:49
 * @version:1.0
 */
public class TestLeaderLatch {
    // LeaderLatch方式就是以一种抢占方式来决定选主，是一种非公平的领导选举，
    // 谁抢到就是谁，会随机从候选者中选择一台作为leader

    //竞争leader的客户端数量
    private static  final  Integer CLIENT_COUNT=7;

    //zookeeper临时节点的路径
    private static  final  String PATH="/curator/leader";

    public static void main(String[] args) throws IOException {
        //模拟的同步线程池
        ExecutorService executorService=Executors.newFixedThreadPool(CLIENT_COUNT);

        //客户端启动竞争leader
        for(int i=1;i<=CLIENT_COUNT;i++){

            Integer index=i;

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //客户端竞争leader
                    try {
                       new TestLeaderLatch().fightforleader("CLIRNT_"+index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        System.in.read();
        executorService.shutdown();

    }

    public  void fightforleader(String threadInfo) throws Exception {

        CuratorFramework curatorFramework = CuratorClientUtils.getInstance(threadInfo);

        LeaderLatch leaderLatch=new LeaderLatch(curatorFramework,PATH,threadInfo);

        //在start之前添加事件回调监听(leader当选和释放的监听)
        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {

                System.out.println(threadInfo+"当选为leader!");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(threadInfo+"释放leadership!");
                //释放leadership
                //CloseMode.NOTIFY_LEADER 节点状态改变时,通知LeaderLatchListener
                try {
                    leaderLatch.close(LeaderLatch.CloseMode.NOTIFY_LEADER);

                    curatorFramework.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void notLeader() {

                System.out.println(threadInfo+"作为follower!");

            }
        });

        //通过curatort提供的LeaderLatch 进行learder竞争开启
        leaderLatch.start();
    }

}

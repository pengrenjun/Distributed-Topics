package zookeeper.applicationScenarios.curator.DistributedQueue;

import zookeeper.michael.curator.CuratorClientUtils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Description:
 * @Author：pengrj
 * @Date : 2019/5/2 0002 15:44
 * @version:1.0
 */
public class TestingServer implements Closeable {

    public String getConnectString() {
        return CuratorClientUtils.CONNECTSTRING;
    }

    @Override
    public void close() throws IOException {

    }
}

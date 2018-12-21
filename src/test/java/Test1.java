import com.distributed.Node;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: Hitoka
 * @since: 2018-12-19
 **/
public class Test1 {

    @Test
    public void startInstroducer() throws IOException {
        new Node(9001).run();
    }


    @Test
    public void startNode() throws IOException {
        String introIp = "127.0.0.1";
        new Node(9002, InetAddress.getByName(introIp), 9001).run();
    }



}

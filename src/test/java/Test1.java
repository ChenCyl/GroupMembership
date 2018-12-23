import com.distributed.Main;
import com.distributed.Node;
import com.distributed.entity.NodeID;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

/**
 * @author: Hitoka
 * @since: 2018-12-24
 **/
public class Test1 {

    public static void main(String[] args) throws UnknownHostException {
        Boolean isIntroducer = false;
        if (isIntroducer) {
            Node node = new Node(9001);
            List<NodeID> membershipList = node.run(null);
            while (rejoin()) {
                // rejoin 过后的端口号不变
                membershipList = node.run(membershipList);
            }
        } else {
            Node node = new Node(9005, InetAddress.getByName("127.0.0.1"), 9001);
            node.run(null);
            // 是否 rejoin
            while (rejoin()) {
                // rejoin 过后的端口号不变
                node.run(null);
            }

        }
    }

    private static Boolean rejoin() {
        Logger logger = Logger.getLogger(Main.class);
        // Node 不是线程，所以之后 return 了之后才会开始监听键盘，和 Node 里监听键盘不冲突
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            if (scanner.next().equals("r")) {
                System.out.println("You hava rejoined in the group.");
                System.out.println("You can input: [q] to leave the group. [m] to show the membership list.");
                scanner.close();
                logger.info("[REJOIN]");
                return true;
            }
            else {
                System.out.println("Invalid input.");
            }
        }
        return false;
    }
}

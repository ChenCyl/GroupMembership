package com.distributed;

import com.distributed.entity.NodeID;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

/**
 * @author: Chen Yulei
 * @since: 2018-12-20
 **/
public class Main {

    private static int port = 0;
    private static int introPort = 0;
    private static String introAddress = "";
    private static boolean isIntroducer = false;

    // 正式操作
    public static void main1(String[] args) throws IOException {
        handleCommand(args);
        // 普通 node run
        if (!isIntroducer) {
            new Node(port, InetAddress.getByName(introAddress), introPort).run(null);
            // 是否 rejoin
            while (rejoin()) {
                // rejoin 过后的端口号不变
                new Node(port, InetAddress.getByName(introAddress), introPort).run(null);
            }
        }
        // introducer run
        else {
            List<NodeID> membershipList = new Node(port).run(null);
            while (rejoin()) {
                // rejoin 过后的端口号不变
                membershipList = new Node(port).run(membershipList);
            }
        }
    }
    // 单机操作
    public static void main(String[] args) throws UnknownHostException {
        Boolean isIntroducer = false;
        if (isIntroducer) {
            List<NodeID> membershipList = new Node(9001).run(null);
            while (rejoin()) {
                // rejoin 过后的端口号不变
                membershipList = new Node(9001).run(membershipList);
            }
        } else {
            new Node(9003, InetAddress.getByName("127.0.0.1"), 9001).run(null);
            // 是否 rejoin
            while (rejoin()) {
                // rejoin 过后的端口号不变
                new Node(9005, InetAddress.getByName("127.0.0.1"), 9001).run(null);
            }

        }
    }

    /**
     * return true is to rejoin
     * @return
     */
    private static Boolean rejoin() {
        Logger logger = Logger.getLogger(Main.class);
        // Node 不是线程，所以之后 return 了之后才会开始监听键盘，和 Node 里监听键盘不冲突
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            if (scanner.next().equals("r")) {
                System.out.println("You hava rejoined in the group.");
                System.out.println("You can input: [q] to leave the group. [m] to show the membership list.");
//                scanner.close();
                logger.info("[REJOIN]");
                Node.EXIT = false;
                return true;
            }
            else {
                System.out.println("Invalid input.");
            }
        }
        return false;
    }

    // 以下函数用来处理命令行 不是自己写的
    // 作者：https://github.com/mazumdarparijat
    /**
     * Formats commandline inputs and flags
     */
    private static void handleCommand(String [] args) {
        Options op=createOptions();
        CommandLineParser parser=new DefaultParser();
        CommandLine line=null;
        try {
            line=parser.parse(op,args);
        } catch (ParseException e) {
            printHelp(op);
            e.printStackTrace();
        }
        if (!line.hasOption("i")) {
            Main.port = Integer.parseInt(line.getOptionValue("p"));
            isIntroducer=true;
        }
        else {
            isIntroducer=false;
            Main.port = Integer.parseInt(line.getOptionValue("p"));
            Main.introAddress=line.getOptionValues("i")[0];
            Main.introPort=Integer.parseInt(line.getOptionValues("i")[1]);
        }
    }

    /** Creates the required options to look for in command line arguments
     * @return Options object
     */
    private static Options createOptions() {
        Option port = Option.builder("p").argName("serverPort").hasArg().desc("Port to run faliure detector server")
                .required().build();
        Option i = Option.builder("i").desc("Describes the address and port of introducer").numberOfArgs(2).build();
        Options op=new Options();
        op.addOption(port);
        op.addOption(i);
        return op;
    }

    /** print helper for usage
     * @param op options
     */
    private static void printHelp(Options op) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("failureDetector", op);
    }


}

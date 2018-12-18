package com.distributed;

/**
 * @author: Chen Yulei
 * @since: 2018-12-18
 **/
public class IntroducerMain {

    public static void main(String[] args) {
        Boolean isIntroducer = true;
        Integer port = 9001;
        new Thread(new Node(port, isIntroducer)).start();
    }
}

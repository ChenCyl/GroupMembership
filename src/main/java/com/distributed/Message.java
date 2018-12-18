package com.distributed;

/**
 * @author: Chen Yulei
 * @since: 2018-12-18
 **/
public class Message {

    private enum MessageType {
        PING ('P'),
        PING_REQUEST ('Q'),
        ACK ('A'),
        ACK_REQUEST ('B'),
        MISSING_NOTICE ('M'),
        END ('E');

        private char messagePrefix;
        MessageType(char p) {
            messagePrefix = p;
        }

    }


}

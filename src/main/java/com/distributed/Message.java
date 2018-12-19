package com.distributed;

import java.io.Serializable;

/**
 * @author:  Chen Yulei
 * @since:  2018-12-18
 **/

// Serializable: An object can be sent not only String
public class Message implements Serializable {

    // "PING" "REQ" "ACK" "MOVE"
    private String type;
    // 发送端
    private NodeID SourceID;
    // 接收端
    private NodeID SinkID;

    public Message(String type, NodeID sourceID, NodeID sinkID) {
        this.type = type;
        SourceID = sourceID;
        SinkID = sinkID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NodeID getSourceID() {
        return SourceID;
    }

    public void setSourceID(NodeID sourceID) {
        SourceID = sourceID;
    }

    public NodeID getSinkID() {
        return SinkID;
    }

    public void setSinkID(NodeID sinkID) {
        SinkID = sinkID;
    }
}

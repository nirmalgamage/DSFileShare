package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.NodeOfTheNeighbour;
import com.semicolon.ds.core.TableOfRoutingData;
import com.semicolon.ds.core.TimeoutHandler;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class LeaveHandler implements AbstractRequestHandler {

    private TableOfRoutingData tableOfRoutingData;
    private BlockingQueue<ChannelMessage> channelOut;
    private static LeaveHandler leaveHandler;

    public synchronized static LeaveHandler getInstance() {
        if (leaveHandler == null){
            leaveHandler = new LeaveHandler();
        }
        return leaveHandler;
    }

    public void sendLeave () {
        String payload = String.format(Constants.LEAVE_FORMAT,
                this.tableOfRoutingData.getAddress(),
                this.tableOfRoutingData.getPort());
        String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5,payload);
        ArrayList<NodeOfTheNeighbour> nodeOfTheNeighbours = tableOfRoutingData.getNeighbours();
        for (NodeOfTheNeighbour n: nodeOfTheNeighbours) {
            ChannelMessage message = new ChannelMessage(n.getNeighbourAddress(), n.getPort(),rawMessage);
            sendRequest(message);
        }

    }

    @Override
    public void init(TableOfRoutingData tableOfRoutingData,
                     BlockingQueue<ChannelMessage> channelOut,
                     TimeoutHandler timeoutHandler) {
        this.tableOfRoutingData = tableOfRoutingData;
        this.channelOut = channelOut;
    }

    @Override
    public void sendRequest(ChannelMessage message) {
        try {
            channelOut.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

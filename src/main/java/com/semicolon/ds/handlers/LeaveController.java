package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.Neighbour;
import com.semicolon.ds.core.RoutingTable;
import com.semicolon.ds.core.TimeoutManager;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class LeaveController implements IRequestController {

    private RoutingTable rTable;
    private BlockingQueue<ChannelMessage> channelOutput;
    private static LeaveController leaveController;

    public synchronized static LeaveController getInstance() {
        if (leaveController == null){
            leaveController = new LeaveController();
        }
        return leaveController;
    }

    public void sendLeaveMessage() {
        String msgPayload = String.format(Constants.LEAVE_FORMAT,
                this.rTable.getAddress(),
                this.rTable.getPort());
        String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5,msgPayload);
        ArrayList<Neighbour> neighbours = rTable.getNeighbours();
        for (Neighbour neighbour: neighbours) {
            ChannelMessage channelMessage = new ChannelMessage(neighbour.getAddress(), neighbour.getPort(),rMessage);
            sendRequestMessage(channelMessage);
        }

    }

    @Override
    public void init(RoutingTable rTable,
                     BlockingQueue<ChannelMessage> channelOutput,
                     TimeoutManager timeoutManager) {
        this.rTable = rTable;
        this.channelOutput = channelOutput;
    }

    @Override
    public void sendRequestMessage(ChannelMessage cMessage) {
        try {
            channelOutput.put(cMessage);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

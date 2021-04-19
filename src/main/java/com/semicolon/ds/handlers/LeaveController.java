package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.NodeOfTheNeighbour;
import com.semicolon.ds.core.TableOfRoutingData;
import com.semicolon.ds.core.TimeoutHandler;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class LeaveController implements IRequestController {

    private TableOfRoutingData rTable;
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
        ArrayList<NodeOfTheNeighbour> neighbours = rTable.getNeighbours();
        for (NodeOfTheNeighbour neighbour: neighbours) {
            ChannelMessage channelMessage = new ChannelMessage(neighbour.getNeighbourAddress(), neighbour.getPort(),rMessage);
            sendRequestMessage(channelMessage);
        }

    }

    @Override
    public void init(TableOfRoutingData rTable,
                     BlockingQueue<ChannelMessage> channelOutput,
                     TimeoutHandler timeoutManager) {
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

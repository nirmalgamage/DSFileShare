package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.TableOfRoutingData;
import com.semicolon.ds.core.TimeoutHandler;

import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class PongController implements IRequestController, IResponseController {

    private final Logger LOGGER = Logger.getLogger(PongController.class.getName());

    private BlockingQueue<ChannelMessage> channelOutput;

    private TableOfRoutingData rTable;

    private static PongController pongController;
    private TimeoutHandler timeoutManager;

    private PongController(){

    }

    public synchronized static PongController getInstance(){
        if (pongController == null){
            pongController = new PongController();
        }

        return pongController;
    }

    @Override
    public void sendRequestMessage(ChannelMessage cMessage) {

    }

    @Override
    public void manageResponse(ChannelMessage cMessage) {
        LOGGER.fine("PONG Received : " + cMessage.getMessage()
                + " from: " + cMessage.getIpAddress()
                + " port: " + cMessage.getPort());

        StringTokenizer tokenizer = new StringTokenizer(cMessage.getMessage(), " ");

        String size = tokenizer.nextToken();
        String keyword = tokenizer.nextToken();
        String address = tokenizer.nextToken().trim();
        int port = Integer.parseInt(tokenizer.nextToken().trim());
        if(keyword.equals("BPONG")) {
            if(rTable.getNeighboursCount() < Constants.MIN_NEIGHBOURS) {
                this.rTable.addNodeAsANeighbour(address, port, cMessage.getPort());

            }
        } else {
            this.timeoutManager.newResponseRegistration(String.format(Constants.PING_MESSAGE_ID_FORMAT,address,port));
            this.rTable.addNodeAsANeighbour(address, port, cMessage.getPort());


        }

    }

    @Override
    public void init(
            TableOfRoutingData rTable,
            BlockingQueue<ChannelMessage> channelOutput,
            TimeoutHandler timeoutManager) {
        this.rTable = rTable;
        this.channelOutput = channelOutput;
        this.timeoutManager = timeoutManager;
    }
}

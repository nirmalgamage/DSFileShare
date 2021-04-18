package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.TableOfRoutingData;
import com.semicolon.ds.core.TimeoutHandler;

import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class PongHandler implements AbstractRequestHandler, AbstractResponseHandler{

    private final Logger LOG = Logger.getLogger(PongHandler.class.getName());

    private BlockingQueue<ChannelMessage> channelOut;

    private TableOfRoutingData tableOfRoutingData;

    private static PongHandler pongHandler;
    private TimeoutHandler timeoutHandler;

    private PongHandler(){

    }

    public synchronized static PongHandler getInstance(){
        if (pongHandler == null){
            pongHandler = new PongHandler();
        }

        return pongHandler;
    }

    @Override
    public void sendRequest(ChannelMessage message) {

    }

    @Override
    public void handleResponse(ChannelMessage message) {
        LOG.fine("Received PONG : " + message.getMessage()
                + " from: " + message.getAddress()
                + " port: " + message.getPort());

        StringTokenizer stringToken = new StringTokenizer(message.getMessage(), " ");

        String length = stringToken.nextToken();
        String keyword = stringToken.nextToken();
        String address = stringToken.nextToken().trim();
        int port = Integer.parseInt(stringToken.nextToken().trim());
        if(keyword.equals("BPONG")) {
            if(tableOfRoutingData.getNeighboursCount() < Constants.MIN_NEIGHBOURS) {
                this.tableOfRoutingData.addNodeAsANeighbour(address, port, message.getPort());
//                this.routingTable.print();
            }
        } else {
            this.timeoutHandler.newResponseRegistration(String.format(Constants.PING_MESSAGE_ID_FORMAT,address,port));
            this.tableOfRoutingData.addNodeAsANeighbour(address, port, message.getPort());

//            this.routingTable.print();
        }

    }

    @Override
    public void init(
            TableOfRoutingData tableOfRoutingData,
            BlockingQueue<ChannelMessage> channelOut,
            TimeoutHandler timeoutHandler) {
        this.tableOfRoutingData = tableOfRoutingData;
        this.channelOut = channelOut;
        this.timeoutHandler = timeoutHandler;
    }
}

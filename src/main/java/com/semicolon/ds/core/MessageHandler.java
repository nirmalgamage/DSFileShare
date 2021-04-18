package com.semicolon.ds.core;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.comms.UDPClient;
import com.semicolon.ds.comms.UDPServer;
import com.semicolon.ds.handlers.*;

import java.net.DatagramSocket;
import java.net.SocketException;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MessageHandler extends Thread {

    private final Logger LOG = Logger.getLogger(MessageHandler.class.getName());

    private volatile boolean continueHandlingResponse = true;

    private final UDPServer udpServer;
    private final UDPClient udpClient;

    private BlockingQueue<ChannelMessage> blockingQueueChannelIn;
    private BlockingQueue<ChannelMessage> blockingQueueChannelOut;

    private TableOfRoutingData tableOfRoutingData;
    private PingHandler handlerOfPing;
    private LeaveHandler handlerOfLeave;
    private SearchQueryHandler handlerOfSearchQuery;
    private FileHandler handlerOfFile;

    private TimeoutHandler handlerOfTimeOut = new TimeoutHandler();

    public MessageHandler(String routingAddress, int routingPort) throws SocketException {
        blockingQueueChannelIn = new LinkedBlockingQueue<ChannelMessage>();
        DatagramSocket socket = new DatagramSocket(routingPort);
        this.udpServer = new UDPServer(blockingQueueChannelIn, socket);

        blockingQueueChannelOut = new LinkedBlockingQueue<ChannelMessage>();
        this.udpClient = new UDPClient(blockingQueueChannelOut, new DatagramSocket());

        this.tableOfRoutingData = new TableOfRoutingData(routingAddress, routingPort);

        this.handlerOfPing = PingHandler.getInstance();
        this.handlerOfLeave = LeaveHandler.getInstance();

        this.handlerOfFile = FileHandler.newFileHandler("");

        this.handlerOfPing.init(this.tableOfRoutingData, this.blockingQueueChannelOut, this.handlerOfTimeOut);
        this.handlerOfLeave.init(this.tableOfRoutingData, this.blockingQueueChannelOut, this.handlerOfTimeOut);

        this.handlerOfSearchQuery = SearchQueryHandler.getInstance();
        this.handlerOfSearchQuery.init(tableOfRoutingData, blockingQueueChannelOut, handlerOfTimeOut);

        LOG.fine("starting server");
        handlerOfTimeOut.newRequestRegistration(Constants.R_PING_MESSAGE_ID, Constants.PING_INTERVAL, new TimeoutCallback() {
            @Override
            public void onTimeout(String messageId) {
                sendingTheRoutingOfPing();
            }

            @Override
            public void onResponse(String messageId) {
            }

        });
    }

    @Override
    public void run(){
        this.udpServer.start();
        this.udpClient.start();
        this.startHandlingResponse();
    }

    public void startHandlingResponse() {
        while (continueHandlingResponse) {
            try {
                ChannelMessage channelMessage = blockingQueueChannelIn.poll(100, TimeUnit.MILLISECONDS);
                if (channelMessage != null) {
                    LOG.info("Received Message: " + channelMessage.getMessage()
                            + " from: " + channelMessage.getAddress()
                            + " port: " + channelMessage.getPort());

                    AbstractResponseHandler abstractResponseHandler
                            = ResponseHandlerFactory.getResponseHandler(
                            channelMessage.getMessage().split(" ")[1],
                            this
                    );

                    if (abstractResponseHandler != null){
                        abstractResponseHandler.handleResponse(channelMessage);
                    }

                }
                handlerOfTimeOut.timeOutChecking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopHandlingResponse() {
        this.continueHandlingResponse = false;
        udpServer.stopProcessing();
    }

    public void sendingThePing(String address, int port) {
        this.handlerOfPing.sendPing(address, port);
    }

    public void searching(String keyword){
        this.handlerOfSearchQuery.doSearch(keyword);
    }

    public BlockingQueue<ChannelMessage> getBlockingQueueChannelIn() {
        return blockingQueueChannelIn;
    }

    public BlockingQueue<ChannelMessage> getBlockingQueueChannelOut() {
        return blockingQueueChannelOut;
    }

    public TimeoutHandler getHandlerOfTimeOut() {
        return handlerOfTimeOut;
    }

    public TableOfRoutingData getTableOfRoutingData() {
        return tableOfRoutingData;
    }


    private void sendingTheRoutingOfPing() {
        ArrayList<String> neighboursInRoutingTable = tableOfRoutingData.toList();
        for (String n: neighboursInRoutingTable) {
            String address = n.split(":")[0];
            int port = Integer.valueOf(n.split(":")[1]);
            sendingThePing(address, port);

        }
    }

    public void sendingTheLeave() {
        this.handlerOfLeave.sendLeave();
    }

    public String getFilesFromFileName() {
        return this.handlerOfFile.nameOfTheFiles();
    }
}

package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.RoutingTable;
import com.semicolon.ds.core.TimeoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class PingController implements IRequestController, IResponseController {

    private final Logger LOGGER = Logger.getLogger(PingController.class.getName());

    private static PingController pingController;

    private boolean started;
    private BlockingQueue<ChannelMessage> channelOutput;
    private RoutingTable rTable;
    private TimeoutManager timeoutManager;
    private Map<String, Integer> failCount = new HashMap<String, Integer>();
    private CallBackWhenTimeout callBackWhenTimeout = new PingToutCallBack();

    private PingController() {
        this.started = true;
    }

    public synchronized static PingController getInstance() {
        if (pingController == null){
            pingController = new PingController();
        }
        return pingController;
    }

    @Override
    public void sendRequestMessage(ChannelMessage cMessage) {
        try {
            channelOutput.put(cMessage);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void manageResponse(ChannelMessage cMessage) {

        LOGGER.fine("PING Received : " + cMessage.getMessage()
                + " from: " + cMessage.getAddress()
                + " port: " + cMessage.getPort());

        StringTokenizer stringTokenizer = new StringTokenizer(cMessage.getMessage(), " ");

        String size = stringTokenizer.nextToken();
        String keyword = stringTokenizer.nextToken();
        String address = stringTokenizer.nextToken().trim();
        int port = Integer.parseInt(stringTokenizer.nextToken().trim());
        switch (keyword) {

            case "BPING":
                int hopsCount = Integer.parseInt(stringTokenizer.nextToken().trim());

                if (rTable.isANeighbour(address, port)) {
                    if (hopsCount > 0) {
                        passBPingMessage(address, port, hopsCount - 1);
                    }
                } else {


                    int result = this.rTable.getCount();
                    if (result < Constants.MAX_NEIGHBOURS) {

                        String msgPayload = String.format(Constants.BPONG_FORMAT,
                                this.rTable.getAddress(), this.rTable.getPort());

                        String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5, msgPayload);
                        ChannelMessage outputMessage = new ChannelMessage(address,
                                port, rMessage);
                        this.sendRequestMessage(outputMessage);
                    } else {

                        if (hopsCount > 0) {
                            passBPingMessage(address, port, hopsCount - 1);
                        }
                    }
                }

                break;
            case "LEAVE":
                System.out.println("Receiving the Leaving msg");
                this.rTable.removeNeighbour(address, port);
                if (rTable.getCount() <= Constants.MIN_NEIGHBOURS) {
                    sendBPingMessage(address, port);
                }
                this.rTable.print();

                break;
            default:
                int result = this.rTable.addNeighbour(address, port, cMessage.getPort());

                if (result != 0) {
                    String msgPayload = String.format(Constants.PONG_FORMAT,
                            this.rTable.getAddress(), this.rTable.getPort());

                    String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5, msgPayload);
                    ChannelMessage outputMessage = new ChannelMessage(address,
                            port, rMessage);
                    this.sendRequestMessage(outputMessage);
                }
                break;
        }


    }

    public void sendPingMessage(String address, int port) {
        String msgPayload = String.format(Constants.PING_FORMAT,
                this.rTable.getAddress(),
                this.rTable.getPort());
        String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5,msgPayload);
        ChannelMessage channelMessage = new ChannelMessage(address, port,rMessage);
        this.failCount.putIfAbsent(
                String.format(Constants.PING_MESSAGE_ID_FORMAT, address, port),
                0);
        this.timeoutManager.registerRequest(
                String.format(Constants.PING_MESSAGE_ID_FORMAT, address, port),
                Constants.PING_TIMEOUT,
                this.callBackWhenTimeout
                );
        this.sendRequestMessage(channelMessage);

    }

    private void sendBPingMessage(String address, int port) {
        ArrayList<String> targetList = rTable.getOtherNeighbours(address,port);
        String msgPayload = String.format(Constants.BPING_FORMAT,
                this.rTable.getAddress(),
                this.rTable.getPort(),
                Constants.BPING_HOP_LIMIT);
        String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5,msgPayload);
        for (String target: targetList) {
            ChannelMessage message = new ChannelMessage(
                    target.split(":")[0],
                    Integer.parseInt(target.split(":")[1]), rMessage);
            sendRequestMessage(message);
        }
    }

    private void passBPingMessage(
            String baseAddress,
            int basePort,
            int currHop) {
        ArrayList<String> targetsList = rTable.getOtherNeighbours(baseAddress,basePort);
        String msgPayload = String.format(Constants.BPING_FORMAT,
                baseAddress,
                basePort,
                currHop);
        String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5,msgPayload);
        for (String target: targetsList) {
            ChannelMessage channelMessage = new ChannelMessage(
                    target.split(":")[0],
                    Integer.parseInt(target.split(":")[1]), rMessage);
            sendRequestMessage(channelMessage);
        }
    }


    @Override
    public void init(
            RoutingTable rTable,
            BlockingQueue<ChannelMessage> channelOutput,
            TimeoutManager timeoutManager) {
            this.rTable = rTable;
            this.channelOutput = channelOutput;
            this.timeoutManager = timeoutManager;

    }

    private class PingToutCallBack implements CallBackWhenTimeout {

        @Override
        public void whenTimeout(String messageId) {
            failCount.put(messageId, failCount.get(messageId) + 1);
            if(failCount.get(messageId) >= Constants.PING_RETRY) {
                LOGGER.fine("Neighbour is missed :( =>" + messageId);
                rTable.removeNeighbour(
                        messageId.split(":")[1],
                        Integer.valueOf(messageId.split(":")[2]));
            }

            if (rTable.getCount() < Constants.MIN_NEIGHBOURS) {
                sendBPingMessage(
                        messageId.split(":")[1],
                        Integer.valueOf(messageId.split(":")[2]));
            }
        }

        @Override
        public void onResponse(String messageId) {
            failCount.put(messageId, 0);
        }
    }


}

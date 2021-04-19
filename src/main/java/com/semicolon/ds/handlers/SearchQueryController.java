package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.FileHandler;
import com.semicolon.ds.core.NodeOfTheNeighbour;
import com.semicolon.ds.core.TableOfRoutingData;
import com.semicolon.ds.core.TimeoutHandler;
import com.semicolon.ds.utils.StringManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class SearchQueryController implements IResponseController, IRequestController {

    private final Logger LOGGER = Logger.getLogger(SearchQueryController.class.getName());

    private TableOfRoutingData rTable;

    private BlockingQueue<ChannelMessage> channelOutput;

    private TimeoutHandler timeoutManager;

    private static SearchQueryController searchQueryHandler;

    private FileHandler fileManager;

    private SearchQueryController(){
        fileManager = FileHandler.newFileHandler("");
    }

    public synchronized static SearchQueryController getInstance(){
        if (searchQueryHandler == null){
            searchQueryHandler = new SearchQueryController();
        }
        return searchQueryHandler;
    }

    public void executeSearchOperation(String keyword) {

        String msgPayload = String.format(Constants.QUERY_FORMAT,
                this.rTable.getAddress(),
                this.rTable.getPort(),
                StringManipulator.encodeString(keyword),
                Constants.HOP_COUNT);

        String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5, msgPayload);

        ChannelMessage startMessage = new ChannelMessage(
                this.rTable.getAddress(),
                this.rTable.getPort(),
                rMessage);

        this.manageResponse(startMessage);
    }

    @Override
    public void sendRequestMessage(ChannelMessage cMessage) {
        try {
            channelOutput.put(cMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(TableOfRoutingData rTable, BlockingQueue<ChannelMessage> channelOutput,
                     TimeoutHandler timeoutManager) {
        this.rTable = rTable;
        this.channelOutput = channelOutput;
        this.timeoutManager = timeoutManager;
    }

    @Override
    public void manageResponse(ChannelMessage cMessage) {
        LOGGER.fine("SER Received: " + cMessage.getMessage()
                + " from: " + cMessage.getIpAddress()
                + " port: " + cMessage.getPort());

        StringTokenizer tokenizer = new StringTokenizer(cMessage.getMessage(), " ");

        String size = tokenizer.nextToken();
        String keyword = tokenizer.nextToken();
        String address = tokenizer.nextToken().trim();
        int port = Integer.parseInt(tokenizer.nextToken().trim());

        String fileName = StringManipulator.decodeString(tokenizer.nextToken().trim());
        int hopsCount = Integer.parseInt(tokenizer.nextToken().trim());


        Set<String> resultSet = fileManager.searchingAFile(fileName);
        int numOfFileNames = resultSet.size();

        if (numOfFileNames != 0) {

            StringBuilder fileNamesBuilder = new StringBuilder("");

            Iterator<String> iterator = resultSet.iterator();

            while(iterator.hasNext()){
                fileNamesBuilder.append(StringManipulator.encodeString(iterator.next()) + " ");
            }

            String msgPayload = String.format(Constants.QUERY_HIT_FORMAT,
                    numOfFileNames,
                    rTable.getAddress(),
                    rTable.getPort(),
                    Constants.HOP_COUNT- hopsCount,
                    fileNamesBuilder.toString());

            String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5, msgPayload);

            ChannelMessage qhMessage = new ChannelMessage(address,
                    port,
                    rMessage);

            this.sendRequestMessage(qhMessage);
        }



        if (hopsCount > 0){
            ArrayList<NodeOfTheNeighbour> neighboursList = this.rTable.getNeighbours();

            for(NodeOfTheNeighbour neighbour: neighboursList){


                if (neighbour.getNeighbourAddress().equals(cMessage.getIpAddress())
                        && neighbour.getNeighbourClientPort() == cMessage.getPort()) {
                    continue;
                }

                String msgPayload = String.format(Constants.QUERY_FORMAT,
                        address,
                        port,
                        StringManipulator.encodeString(fileName),
                        hopsCount - 1);

                String rMessage = String.format(Constants.MSG_FORMAT, msgPayload.length() + 5, msgPayload);

                ChannelMessage qMessage = new ChannelMessage(neighbour.getNeighbourAddress(),
                        neighbour.getPort(),
                        rMessage);

                this.sendRequestMessage(qMessage);
            }
        }
    }
}

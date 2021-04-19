package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.RoutingTable;
import com.semicolon.ds.core.SearchResult;
import com.semicolon.ds.core.TimeoutManager;
import com.semicolon.ds.utils.StringManipulator;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class QueryHitController implements IResponseController {

    private static final Logger LOGGER = Logger.getLogger(QueryHitController.class.getName());

    private RoutingTable rTable;

    private BlockingQueue<ChannelMessage> channelOutput;

    private TimeoutManager timeoutManager;

    private static QueryHitController queryHitController;

    private Map<String, SearchResult> searchResults;

    private long searchStartedTime;

    private QueryHitController(){

    }

    public static synchronized QueryHitController getInstance(){
        if (queryHitController == null){
            queryHitController = new QueryHitController();
        }

        return queryHitController;
    }

    @Override
    public synchronized void manageResponse(ChannelMessage cMessage) {
        LOGGER.fine("SEROK Received : " + cMessage.getMessage()
                + " from: " + cMessage.getIpAddress()
                + " port: " + cMessage.getPort());

        StringTokenizer tokenizer = new StringTokenizer(cMessage.getMessage(), " ");

        String size = tokenizer.nextToken();
        String keyword = tokenizer.nextToken();
        int numOfFiles = Integer.parseInt(tokenizer.nextToken());
        String address = tokenizer.nextToken().trim();
        int port = Integer.parseInt(tokenizer.nextToken().trim());

        String addressKey = String.format(Constants.ADDRESS_KEY_FORMAT, address, port);

        int hopsCount = Integer.parseInt(tokenizer.nextToken());

        while(numOfFiles > 0){

            String fileName = StringManipulator.decodeString(tokenizer.nextToken());

            if (this.searchResults != null){
                if(!this.searchResults.containsKey(addressKey + fileName)){
                    this.searchResults.put(addressKey + fileName,
                            new SearchResult(fileName, address, port, hopsCount,
                                    (System.currentTimeMillis() - searchStartedTime)));

                }
            }

            numOfFiles--;
        }
    }

    @Override
    public void init(RoutingTable rTable, BlockingQueue<ChannelMessage> channelOutput, TimeoutManager timeoutManager) {
        this.rTable = rTable;
        this.channelOutput = channelOutput;
        this.timeoutManager = timeoutManager;
    }

    public void setSearchResults(Map<String, SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    public void setSearchStartedTime(long currentTimeinMillis){
        this.searchStartedTime = currentTimeinMillis;
    }

}

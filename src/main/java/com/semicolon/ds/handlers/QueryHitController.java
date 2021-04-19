package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.TableOfRoutingData;
import com.semicolon.ds.core.ResultsForSearchingQuery;
import com.semicolon.ds.core.TimeoutHandler;
import com.semicolon.ds.utils.StringManipulator;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class QueryHitController implements IResponseController {

    private static final Logger LOGGER = Logger.getLogger(QueryHitController.class.getName());

    private TableOfRoutingData rTable;

    private BlockingQueue<ChannelMessage> channelOutput;

    private TimeoutHandler timeoutManager;

    private static QueryHitController queryHitController;

    private Map<String, ResultsForSearchingQuery> searchResults;

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
                            new ResultsForSearchingQuery(fileName, address, port, hopsCount,
                                    (System.currentTimeMillis() - searchStartedTime)));

                }
            }

            numOfFiles--;
        }
    }

    @Override
    public void init(TableOfRoutingData rTable, BlockingQueue<ChannelMessage> channelOutput, TimeoutHandler timeoutManager) {
        this.rTable = rTable;
        this.channelOutput = channelOutput;
        this.timeoutManager = timeoutManager;
    }

    public void setSearchResults(Map<String, ResultsForSearchingQuery> searchResults) {
        this.searchResults = searchResults;
    }

    public void setSearchStartedTime(long currentTimeinMillis){
        this.searchStartedTime = currentTimeinMillis;
    }

}

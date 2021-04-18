package com.semicolon.ds.handlers;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.FileHandler;
import com.semicolon.ds.core.NodeOfTheNeighbour;
import com.semicolon.ds.core.TableOfRoutingData;
import com.semicolon.ds.core.TimeoutHandler;
import com.semicolon.ds.utils.StringEncoderDecoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class SearchQueryHandler implements AbstractResponseHandler, AbstractRequestHandler {

    private final Logger LOG = Logger.getLogger(SearchQueryHandler.class.getName());

    private TableOfRoutingData tableOfRoutingData;

    private BlockingQueue<ChannelMessage> channelOut;

    private TimeoutHandler timeoutHandler;

    private static SearchQueryHandler searchQueryHandler;

    private FileHandler fileHandler;

    private SearchQueryHandler(){
        fileHandler = FileHandler.newFileHandler("");
    }

    public synchronized static SearchQueryHandler getInstance(){
        if (searchQueryHandler == null){
            searchQueryHandler = new SearchQueryHandler();
        }
        return searchQueryHandler;
    }

    public void doSearch(String keyword) {

        String payload = String.format(Constants.QUERY_FORMAT,
                this.tableOfRoutingData.getAddress(),
                this.tableOfRoutingData.getPort(),
                StringEncoderDecoder.encode(keyword),
                Constants.HOP_COUNT);

        String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

        ChannelMessage initialMessage = new ChannelMessage(
                this.tableOfRoutingData.getAddress(),
                this.tableOfRoutingData.getPort(),
                rawMessage);

        this.handleResponse(initialMessage);
    }

    @Override
    public void sendRequest(ChannelMessage message) {
        try {
            channelOut.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(TableOfRoutingData tableOfRoutingData, BlockingQueue<ChannelMessage> channelOut,
                     TimeoutHandler timeoutHandler) {
        this.tableOfRoutingData = tableOfRoutingData;
        this.channelOut = channelOut;
        this.timeoutHandler = timeoutHandler;
    }

    @Override
    public void handleResponse(ChannelMessage message) {
        LOG.fine("Received SER : " + message.getMessage()
                + " from: " + message.getAddress()
                + " port: " + message.getPort());

        StringTokenizer stringToken = new StringTokenizer(message.getMessage(), " ");

        String length = stringToken.nextToken();
        String keyword = stringToken.nextToken();
        String address = stringToken.nextToken().trim();
        int port = Integer.parseInt(stringToken.nextToken().trim());

        String fileName = StringEncoderDecoder.decode(stringToken.nextToken().trim());
        int hops = Integer.parseInt(stringToken.nextToken().trim());

        //search for the file in the current node
        Set<String> resultSet = fileHandler.searchingAFile(fileName);
        int fileNamesCount = resultSet.size();

        if (fileNamesCount != 0) {

            StringBuilder fileNamesString = new StringBuilder("");

            Iterator<String> itr = resultSet.iterator();

            while(itr.hasNext()){
                fileNamesString.append(StringEncoderDecoder.encode(itr.next()) + " ");
            }

            String payload = String.format(Constants.QUERY_HIT_FORMAT,
                    fileNamesCount,
                    tableOfRoutingData.getAddress(),
                    tableOfRoutingData.getPort(),
                    Constants.HOP_COUNT- hops,
                    fileNamesString.toString());

            String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

            ChannelMessage queryHitMessage = new ChannelMessage(address,
                    port,
                    rawMessage);

            this.sendRequest(queryHitMessage);
        }

        //if the hop count is greater than zero send the message to all neighbours again

        if (hops > 0){
            ArrayList<NodeOfTheNeighbour> nodeOfTheNeighbours = this.tableOfRoutingData.getNeighbours();

            for(NodeOfTheNeighbour nodeOfTheNeighbour : nodeOfTheNeighbours){

                //skip sending search query to the same node again
                if (nodeOfTheNeighbour.getNeighbourAddress().equals(message.getAddress())
                        && nodeOfTheNeighbour.getNeighbourClientPort() == message.getPort()) {
                    continue;
                }

                String payload = String.format(Constants.QUERY_FORMAT,
                        address,
                        port,
                        StringEncoderDecoder.encode(fileName),
                        hops - 1);

                String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

                ChannelMessage queryMessage = new ChannelMessage(nodeOfTheNeighbour.getNeighbourAddress(),
                        nodeOfTheNeighbour.getPort(),
                        rawMessage);

                this.sendRequest(queryMessage);
            }
        }
    }
}

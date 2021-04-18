package com.semicolon.ds.handlers;

import com.semicolon.ds.core.MessageHandler;

import java.util.logging.Logger;

public class ResponseHandlerFactory {

    private static final Logger LOG = Logger.getLogger(ResponseHandlerFactory.class.getName());

    public static AbstractResponseHandler getResponseHandler(String keyword,
                                                             MessageHandler messageHandler){
        switch (keyword){
            case "PING":
                AbstractResponseHandler pingHandler = PingHandler.getInstance();
                pingHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return pingHandler;

            case "BPING":
                AbstractResponseHandler bPingHandler = PingHandler.getInstance();
                bPingHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return bPingHandler;

            case "PONG":
                AbstractResponseHandler pongHandler = PongHandler.getInstance();
                pongHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return pongHandler;

            case "BPONG":
                AbstractResponseHandler bpongHandler = PongHandler.getInstance();
                bpongHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return bpongHandler;

            case "SER":
                AbstractResponseHandler searchQueryHandler = SearchQueryHandler.getInstance();
                searchQueryHandler.init(messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut());
                return searchQueryHandler;

            case "SEROK":
                AbstractResponseHandler queryHitHandler = QueryHitHandler.getInstance();
                queryHitHandler.init(messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut());
                return queryHitHandler;

            case "LEAVE":
                AbstractResponseHandler leaveHandler = PingHandler.getInstance();
                leaveHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return leaveHandler;
            default:
                LOG.severe("Unknown keyword received in Response Handler : " + keyword);
                return null;
        }
    }
}

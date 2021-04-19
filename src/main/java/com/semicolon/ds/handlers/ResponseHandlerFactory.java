package com.semicolon.ds.handlers;

import com.semicolon.ds.core.MessageHandler;

import java.util.logging.Logger;

public class ResponseHandlerFactory {

    private static final Logger LOGGER = Logger.getLogger(ResponseHandlerFactory.class.getName());

    public static IResponseController getResponseHandler(String word,
                                                         MessageHandler messageHandler){
        switch (word){
            case "PING":
                IResponseController pingController = PingController.getInstance();
                pingController.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return pingController;

            case "BPING":
                IResponseController bPingHandler = PingController.getInstance();
                bPingHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return bPingHandler;

            case "PONG":
                IResponseController pongHandler = PongController.getInstance();
                pongHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return pongHandler;

            case "BPONG":
                IResponseController bpongHandler = PongController.getInstance();
                bpongHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return bpongHandler;

            case "SER":
                IResponseController searchQueryHandler = SearchQueryController.getInstance();
                searchQueryHandler.init(messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut());
                return searchQueryHandler;

            case "SEROK":
                IResponseController queryHitHandler = QueryHitController.getInstance();
                queryHitHandler.init(messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut());
                return queryHitHandler;

            case "LEAVE":
                IResponseController leaveHandler = PingController.getInstance();
                leaveHandler.init(
                        messageHandler.getTableOfRoutingData(),
                        messageHandler.getBlockingQueueChannelOut(),
                        messageHandler.getHandlerOfTimeOut()
                );
                return leaveHandler;
            default:
                LOGGER.severe("undefined word from response controller : " + word);
                return null;
        }
    }
}

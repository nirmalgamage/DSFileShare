package com.semicolon.ds.handlers;

import com.semicolon.ds.core.MessageBroker;

import java.util.logging.Logger;

public class ResponseHandlerFactory {

    private static final Logger LOGGER = Logger.getLogger(ResponseHandlerFactory.class.getName());

    public static IResponseController getResponseHandler(String word,
                                                         MessageBroker mBroker){
        switch (word){
            case "PING":
                IResponseController pingController = PingController.getInstance();
                pingController.init(
                        mBroker.getRoutingTable(),
                        mBroker.getChannelOut(),
                        mBroker.getTimeoutManager()
                );
                return pingController;

            case "BPING":
                IResponseController bPingHandler = PingController.getInstance();
                bPingHandler.init(
                        mBroker.getRoutingTable(),
                        mBroker.getChannelOut(),
                        mBroker.getTimeoutManager()
                );
                return bPingHandler;

            case "PONG":
                IResponseController pongHandler = PongController.getInstance();
                pongHandler.init(
                        mBroker.getRoutingTable(),
                        mBroker.getChannelOut(),
                        mBroker.getTimeoutManager()
                );
                return pongHandler;

            case "BPONG":
                IResponseController bpongHandler = PongController.getInstance();
                bpongHandler.init(
                        mBroker.getRoutingTable(),
                        mBroker.getChannelOut(),
                        mBroker.getTimeoutManager()
                );
                return bpongHandler;

            case "SER":
                IResponseController searchQueryHandler = SearchQueryController.getInstance();
                searchQueryHandler.init(mBroker.getRoutingTable(),
                        mBroker.getChannelOut(),
                        mBroker.getTimeoutManager());
                return searchQueryHandler;

            case "SEROK":
                IResponseController queryHitHandler = QueryHitController.getInstance();
                queryHitHandler.init(mBroker.getRoutingTable(),
                        mBroker.getChannelOut(),
                        mBroker.getTimeoutManager());
                return queryHitHandler;

            case "LEAVE":
                IResponseController leaveHandler = PingController.getInstance();
                leaveHandler.init(
                        mBroker.getRoutingTable(),
                        mBroker.getChannelOut(),
                        mBroker.getTimeoutManager()
                );
                return leaveHandler;
            default:
                LOGGER.severe("undefined word from response controller : " + word);
                return null;
        }
    }
}

package com.semicolon.ds.handlers;

import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.TableOfRoutingData;
import com.semicolon.ds.core.TimeoutHandler;

import java.util.concurrent.BlockingQueue;

interface AbstractMessageHandler {
    void init (
            TableOfRoutingData tableOfRoutingData,
            BlockingQueue<ChannelMessage> channelOut,
            TimeoutHandler timeoutHandler);

}

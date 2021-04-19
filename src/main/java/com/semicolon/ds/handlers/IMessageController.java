package com.semicolon.ds.handlers;

import com.semicolon.ds.comms.ChannelMessage;
import com.semicolon.ds.core.RoutingTable;
import com.semicolon.ds.core.TimeoutManager;

import java.util.concurrent.BlockingQueue;

interface IMessageController {
    void init (
            RoutingTable rTable,
            BlockingQueue<ChannelMessage> channelOutput,
            TimeoutManager timeoutManager);

}

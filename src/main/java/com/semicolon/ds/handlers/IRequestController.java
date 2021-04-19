package com.semicolon.ds.handlers;

import com.semicolon.ds.comms.ChannelMessage;

public interface IRequestController extends IMessageController {

    void sendRequestMessage(ChannelMessage cMessage);
}

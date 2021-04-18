package com.semicolon.ds.handlers;

import com.semicolon.ds.comms.ChannelMessage;

public interface IResponseController extends IMessageController {

    void manageResponse(ChannelMessage cMessage);
}

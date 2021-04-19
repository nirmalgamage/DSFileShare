package com.semicolon.ds.core;

import com.semicolon.ds.Constants;
import com.semicolon.ds.handlers.CallBackWhenTimeout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TimeoutHandler {
    private final Logger LOG = Logger.getLogger(TimeoutHandler.class.getName());
    private Map<String, CallbackMapWithTimeup> requestsCallback = new HashMap<String, CallbackMapWithTimeup>();

    public void newRequestRegistration(String messaageId, long timeout, CallBackWhenTimeout callback) {
        requestsCallback.put(messaageId, new CallbackMapWithTimeup(timeout, callback));
    }

    public void newResponseRegistration(String messageId) {
        LOG.fine("RegisteringResponse : " + messageId);
        requestsCallback.remove(messageId);
    }

    public void timeOutChecking() {
        ArrayList<String> messagesToRemove = new ArrayList<>();
        for (String messageId: requestsCallback.keySet()) {
            if(requestsCallback.get(messageId).timeoutChecking(messageId)) {
                if(messageId.equals(Constants.R_PING_MESSAGE_ID)) {
                    requestsCallback.get(messageId).callbackTimeoutTime = requestsCallback.get(messageId).callbackTimeoutTime
                            + requestsCallback.get(messageId).callbackTimeout;
                }else {
                    messagesToRemove.add(messageId);
                }

            }
        }
        for (String messageId: messagesToRemove) {
            requestsCallback.remove(messageId);
        }
    }

    private class CallbackMapWithTimeup {
        private long callbackTimeoutTime;
        private CallBackWhenTimeout callback;
        private long callbackTimeout;

        private CallbackMapWithTimeup(long callbackTimeout, CallBackWhenTimeout callback) {
            this.callbackTimeout = callbackTimeout;
            this.callback = callback;
            this.callbackTimeoutTime = System.currentTimeMillis() + callbackTimeout;
        }
        private boolean timeoutChecking(String messageID) {
            if (System.currentTimeMillis() >= callbackTimeoutTime) {
                callback.whenTimeout(messageID);
                return true;
            }
            return false;
        }
    }
}

package com.semicolon.ds.comms;

public class ChannelMessage {
    private final String ipAddress;
    private final int port;
    private final String message;

    public ChannelMessage(String ipAddress, int port, String message) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.message = message;
    }

    public int getPort() {
        return port;
    }

    public String getMessage() {
        return message;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}

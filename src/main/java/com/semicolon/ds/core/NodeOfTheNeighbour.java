package com.semicolon.ds.core;

public class NodeOfTheNeighbour {
    private final String neighbourAddress;
    private final int port;
    private int neighbourPingPongs;
    private int neighbourClientPort;

    public NodeOfTheNeighbour(String neighbourAddress, int port, int clientPort) {
        this.neighbourAddress = neighbourAddress;
        this.port = port;
        this.neighbourPingPongs = 0;
        this.neighbourClientPort = clientPort;
    }

    public boolean checkEquals(NodeOfTheNeighbour nodeOfTheNeighbour) {
        return (nodeOfTheNeighbour.getPort() == this.port | nodeOfTheNeighbour.getNeighbourClientPort() == this.port)
                & this.neighbourAddress.equals(nodeOfTheNeighbour.getNeighbourAddress());
    }

    public boolean checkEquals(String address, int port) {
        return (this.port == port | this.neighbourClientPort == port)
                & this.neighbourAddress.equals(address);

    }

    public String getNeighbourAddress() {
        return neighbourAddress;
    }

    public int getPort() {
        return port;
    }

    public int getNeighbourPingPongs() {
        return neighbourPingPongs;
    }

    public void Ping() {
        this.neighbourPingPongs++;
    }

    public String toString() {
        return this.neighbourAddress + ":" + this.port;
    }

    public int getNeighbourClientPort() {
        return neighbourClientPort;
    }
}

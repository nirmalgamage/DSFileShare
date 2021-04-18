package com.semicolon.ds.core;


import com.semicolon.ds.Constants;

import java.util.ArrayList;
import java.util.logging.Logger;


public class TableOfRoutingData {

    private final Logger LOG = Logger.getLogger(TableOfRoutingData.class.getName());
    private ArrayList<NodeOfTheNeighbour> nodeOfTheNeighbours;
    private final String address;
    private final int port;

    public TableOfRoutingData(String address, int port) {
        this.address = address;
        this.port = port;
        this.nodeOfTheNeighbours = new ArrayList<>();
    }

    public synchronized int addNodeAsANeighbour(String address, int port, int clientPort) {
        for (NodeOfTheNeighbour n: nodeOfTheNeighbours) {
            if (n.checkEquals(address, port)){
                n.Ping();
                return nodeOfTheNeighbours.size();
            }
        }
        if (nodeOfTheNeighbours.size() >= Constants.MAX_NEIGHBOURS) {
            return 0;
        }
        nodeOfTheNeighbours.add(new NodeOfTheNeighbour(address, port, clientPort));

        LOG.fine("Adding neighbour : " + address + ":" + port);
        return nodeOfTheNeighbours.size();
    }

    public synchronized int removeNodeFromNeighbour(String address, int port) {
        NodeOfTheNeighbour nodeOfTheNeighbour = null;
        for (NodeOfTheNeighbour n: nodeOfTheNeighbours) {
            if (n.checkEquals(address, port)) {
                nodeOfTheNeighbour = n;
            }
        }
        if (nodeOfTheNeighbour != null) {
            nodeOfTheNeighbours.remove(nodeOfTheNeighbour);
            return nodeOfTheNeighbours.size();
        }
        return 0;
    }
    public synchronized int getNeighboursCount() {
        return nodeOfTheNeighbours.size();
    }

    public synchronized void showData() {
        System.out.println("Total neighbours: " + nodeOfTheNeighbours.size());
        System.out.println("Address: " + address + ":" + port);
        System.out.println("++++++++++++++++++++++++++");
        for (NodeOfTheNeighbour n : nodeOfTheNeighbours) {
            System.out.println(
                    "Address: " + n.getNeighbourAddress()
                    + " Port: " + n.getPort()
                    + " Pings: " + n.getNeighbourPingPongs()
            );
        }
    }

    public synchronized String toString() {
        String table = "Total neighbours: " + nodeOfTheNeighbours.size() + "\n";
        table += "Address: " + address + ":" + port + "\n";
        table += "++++++++++++++++++++++++++" + "\n";
        for (NodeOfTheNeighbour n : nodeOfTheNeighbours) {
            table +=
                    "Address: " + n.getNeighbourAddress()
                            + " Port: " + n.getPort()
                            + " Pings: " + n.getNeighbourPingPongs() + "\n"
            ;
        }
        return table;
    }

    public synchronized ArrayList<String> toList() {
        ArrayList<String> list = new ArrayList<>();
        for (NodeOfTheNeighbour n: nodeOfTheNeighbours) {
            list.add(n.toString());
        }
        return list;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public ArrayList<NodeOfTheNeighbour> getNeighbours() {
        return nodeOfTheNeighbours;
    }

    public boolean checkANodeIsANeighbour(String address, int port) {
        for (NodeOfTheNeighbour n: nodeOfTheNeighbours) {
            if (n.checkEquals(address, port)) {
                return  true;
            }
        }
        return false;
    }

    public ArrayList<String> otherNeighbourNodes(String address, int port) {
        ArrayList<String> neighbourNodelist = new ArrayList<>();
        for (NodeOfTheNeighbour n: nodeOfTheNeighbours) {
            if(!n.checkEquals(address, port)) {
                neighbourNodelist.add(n.toString());
            }
        }
        return neighbourNodelist;
    }
}

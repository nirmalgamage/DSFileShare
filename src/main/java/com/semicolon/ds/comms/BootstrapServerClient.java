package com.semicolon.ds.comms;

import com.semicolon.ds.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.net.*;
import java.util.logging.Logger;


public class BootstrapServerClient {

    private final Logger logger = Logger.getLogger(BootstrapServerClient.class.getName());

    private final String bsIPAddress;
    private final int bsPort;

    private final DatagramSocket datagramSocket;

    // TODO: Get BS ip and port from cmd args
    public BootstrapServerClient(String bsIPAddress, int bsPort) throws IOException{

        datagramSocket = new DatagramSocket();

        this.bsIPAddress = bsIPAddress;
        this.bsPort = bsPort;
    }

    public BootstrapServerClient() throws IOException{
        datagramSocket = new DatagramSocket();

        this.bsIPAddress = Constants.BS_IP_ADDRESS;
        this.bsPort = Constants.BS_PORT;
    }

    public List<InetSocketAddress> register(String userName, String ipAddress, int port) throws IOException {

        String request = String.format(Constants.REG_FORMAT, ipAddress, port, userName);

        request = String.format(Constants.MSG_FORMAT, request.length() + 5, request);

        return  processBootstrapServerRegisterResponse(sendAndReceiveUDPMessages(request));

    }

    public boolean unRegister(String userName, String ipAddress, int port) throws IOException{

        String request = String.format(Constants.UNREG_FORMAT, ipAddress, port, userName);

        request = String.format(Constants.MSG_FORMAT, request.length() + 5, request);

        return  processBootstrapServerUnregisterResponse(sendAndReceiveUDPMessages(request));

    }

    private List<InetSocketAddress> processBootstrapServerRegisterResponse(String response){

        StringTokenizer stringToken = new StringTokenizer(response, " ");

        stringToken.nextToken();

        String status = stringToken.nextToken();

        if (!Constants.REGOK.equals(status)) {
            throw new IllegalStateException(Constants.REGOK + " not received");
        }

        int nodesCount = Integer.parseInt(stringToken.nextToken());

        List<InetSocketAddress> gNodes = null;

        switch (nodesCount) {
            case 0:
                logger.fine("Successful - No other nodes in the network");
                gNodes = new ArrayList<>();
                break;

            case 1:
                logger.fine("No of nodes found : 1");

                gNodes = new ArrayList<>();

                while (stringToken.hasMoreTokens()) {
                    gNodes.add(new InetSocketAddress(stringToken.nextToken(),
                            Integer.parseInt(stringToken.nextToken())));
                }
                break;

            case 2:
                logger.fine("No of nodes found : 2");

                gNodes = new ArrayList<>();

                while (stringToken.hasMoreTokens()) {
                    gNodes.add(new InetSocketAddress(stringToken.nextToken(),
                            Integer.parseInt(stringToken.nextToken())));
                }
                break;

            case 9999:
                logger.severe("Failed. There are errors in your command");
                break;
            case 9998:
                logger.severe("Failed, already registered to you, unRegister first");
                break;
            case 9997:
                logger.severe("Failed, registered to another user, try a different IP and port");
                break;
            case 9996:
                logger.severe("Failed, can’t register. BS full.");
                break;
            default:
                throw new IllegalStateException("Invalid status code");
        }

        return gNodes;
    }

    private boolean processBootstrapServerUnregisterResponse(String response){

        StringTokenizer stringTokenizer = new StringTokenizer(response, " ");

        stringTokenizer.nextToken();
        String status = stringTokenizer.nextToken();

        if (!Constants.UNROK.equals(status)) {
            throw new IllegalStateException(Constants.UNROK + " not received");
        }

        int code = Integer.parseInt(stringTokenizer.nextToken());

        switch (code) {
            case 0:
                logger.fine("Successfully unregistered");
                return true;

            case 9999:
                logger.severe("Error while un-registering. " +
                        "IP and port may not be in the registry or command is incorrect");
            default:
                return false;
        }
    }

    private String sendAndReceiveUDPMessages(String request) throws IOException {
        DatagramPacket sendingPacket = new DatagramPacket(request.getBytes(),
                request.length(), InetAddress.getByName(bsIPAddress), bsPort);

        datagramSocket.setSoTimeout(Constants.TIMEOUT_REG);

        datagramSocket.send(sendingPacket);

        byte[] buffer = new byte[65536];

        DatagramPacket received = new DatagramPacket(buffer, buffer.length);

        datagramSocket.receive(received);

        return new String(received.getData(), 0, received.getLength());
    }
}

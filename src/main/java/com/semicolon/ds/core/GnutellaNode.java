package com.semicolon.ds.core;

import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.BootstrapServerClient;
import com.semicolon.ds.comms.ftp.FTPClient;
import com.semicolon.ds.comms.ftp.FTPServer;

import javafx.scene.control.TextArea;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GnutellaNode {

    private final Logger LOG = Logger.getLogger(GnutellaNode.class.getName());

    private BootstrapServerClient bootstrapServerClient;

    private String gnutellaUserName;
    private String gnutellaIpAddress;
    private int gnutellaPort;
    private MessageHandler gnutellaMessageHandler;
    private SearchHandler gnutellaSearchManager;
    private FTPServer gnutellaFTPServer;

    public GnutellaNode(String gnutellaUserName) throws Exception {

        try (final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            this.gnutellaIpAddress = socket.getLocalAddress().getHostAddress();

        } catch (Exception e){
            throw new RuntimeException("Could not find host address");
        }

        this.gnutellaUserName = gnutellaUserName;
        this.gnutellaPort = freePort();
        FileHandler fileHandler = FileHandler.newFileHandler(gnutellaUserName);
        this.gnutellaFTPServer = new FTPServer(this.gnutellaPort + Constants.FTP_PORT_OFFSET, gnutellaUserName);
        Thread t = new Thread(gnutellaFTPServer);
        t.start();

        this.bootstrapServerClient = new BootstrapServerClient();
        this.gnutellaMessageHandler = new MessageHandler(gnutellaIpAddress, gnutellaPort);

        this.gnutellaSearchManager = new SearchHandler(this.gnutellaMessageHandler);

        gnutellaMessageHandler.start();

        LOG.fine("GnutellaNode initiated on IP :" + gnutellaIpAddress + " and Port :" + gnutellaPort);

    }

    public void init() {
        List<InetSocketAddress> targets = this.registerNewNode();
        if(targets != null) {
            for (InetSocketAddress target: targets) {
                gnutellaMessageHandler.sendingThePing(target.getAddress().toString().substring(1), target.getPort());
            }
        }


        Thread thread = new Thread("New Thread") {
            public void run() {
                int count = 0;
                while (true)
                    try {
                        this.sleep(1000);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }

        };
        thread.start();
    }

    private List<InetSocketAddress> registerNewNode() {
        List<InetSocketAddress> targets = null;

        try{
            targets = this.bootstrapServerClient.register(this.gnutellaUserName, this.gnutellaIpAddress, this.gnutellaPort);

        } catch (IOException e) {
            LOG.severe("Registering GnutellaNode failed");
            e.printStackTrace();
        }
        return targets;

    }

    public void unRegisterExistingNode() {
        try{
            this.bootstrapServerClient.unRegister(this.gnutellaUserName, this.gnutellaIpAddress, this.gnutellaPort);
            this.gnutellaMessageHandler.sendingTheLeave();

        } catch (IOException e) {
            LOG.severe("Un-Registering GnutellaNode failed");
            e.printStackTrace();
        }
    }

    public int searchKeyword(String keyword){
        return this.gnutellaSearchManager.seaeching(keyword);
    }

    public Map<String,ResultsForSearchingQuery> searchKeywordInUI(String keyword) {
        return this.gnutellaSearchManager.searchUI(keyword);
    }

    public void downloadFile(int fileOption) {
        try {
            ResultsForSearchingQuery fileDetail = this.gnutellaSearchManager.detailsOfTheFile(fileOption);
            System.out.println("The file you requested is " + fileDetail.getFileNameOfSearchedFile());
            FTPClient ftpClient = new FTPClient(fileDetail.getAddressOfSearchedFile(), fileDetail.getTcpPortOfSearchedFile(),
                    fileDetail.getFileNameOfSearchedFile());

            System.out.println("Waiting for file download...");
            Thread.sleep(Constants.FILE_DOWNLOAD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(int fileOption, TextArea textArea) {
        try {
            ResultsForSearchingQuery fileDetail = this.gnutellaSearchManager.detailsOfTheFile(fileOption);
            System.out.println("The file you requested is " + fileDetail.getFileNameOfSearchedFile());
            FTPClient ftpClient = new FTPClient(fileDetail.getAddressOfSearchedFile(), fileDetail.getTcpPortOfSearchedFile(),
                    fileDetail.getFileNameOfSearchedFile());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGnutellaUserName() {
        return gnutellaUserName;
    }

    public String getGnutellaIpAddress() {
        return gnutellaIpAddress;
    }

    public int getGnutellaPort(){
        return gnutellaPort;
    }

    private int freePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            serverSocket.setReuseAddress(true);
            int port = serverSocket.getLocalPort();
            try {
                serverSocket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
            LOG.severe("Getting free port failed");
            throw new RuntimeException("Getting free port failed");
        }
    }

    public void printRoutingTable(){
        this.gnutellaMessageHandler.getTableOfRoutingData().showData();
    }

    public String getRoutingTable() {
       return this.gnutellaMessageHandler.getTableOfRoutingData().toString();
    }

    public String getFileNames() {
        return this.gnutellaMessageHandler.getFilesFromFileName();
    }

    public void getFile(String address, int tcpPort, String fileName) {
        try {
            FTPClient ftpClient = new FTPClient(address, tcpPort, fileName);
            System.out.println("Waiting for file download...");
            Thread.sleep(Constants.FILE_DOWNLOAD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

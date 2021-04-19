package com.semicolon.ds.core;

import com.semicolon.ds.Constants;

public class ResultsForSearchingQuery {

    private String fileNameOfSearchedFile;
    private String addressOfSearchedFile;
    private int port;
    private int tcpPortOfSearchedFile;
    private int hopsOfSearchedFile;
    private long timeElapsedOfSearchedFile;

    public ResultsForSearchingQuery(String fileNameOfSearchedFile, String addressOfSearchedFile, int port, int hopsOfSearchedFile, long timeElapsedOfSearchedFile) {
        this.fileNameOfSearchedFile = fileNameOfSearchedFile;
        this.addressOfSearchedFile = addressOfSearchedFile;
        this.port = port;
        this.tcpPortOfSearchedFile = port + Constants.FTP_PORT_OFFSET;
        this.hopsOfSearchedFile = hopsOfSearchedFile;
        this.timeElapsedOfSearchedFile = timeElapsedOfSearchedFile;
    }

    public String getFileNameOfSearchedFile() {
        return fileNameOfSearchedFile;
    }

    public String getAddressOfSearchedFile() {
        return addressOfSearchedFile;
    }

    public int getPort() {
        return port;
    }

    public int getTcpPortOfSearchedFile() {
        return tcpPortOfSearchedFile;
    }

    public int getHopsOfSearchedFile() {
        return hopsOfSearchedFile;
    }

    public long getTimeElapsedOfSearchedFile() {
        return timeElapsedOfSearchedFile;
    }
}

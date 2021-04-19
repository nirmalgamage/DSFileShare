package com.semicolon.ds.comms.ftp;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class FTPClient {

    public FTPClient(String IpAddress, int port, String fileName) throws Exception {
        Socket serverSock = new Socket(IpAddress, port);

        System.out.println("Connecting...");
        Thread t = new Thread(new DataReceivingOperation(serverSock, fileName));
        t.start();
    }
}

class DataReceivingOperation implements Runnable {

    private final Socket serverSocket;
    private final String fileName;

    private final Logger logger = Logger.getLogger(DataReceivingOperation.class.getName());

    public DataReceivingOperation(Socket server, String fileName) {
        this.serverSocket = server;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    serverSocket.getInputStream()));
            DataOutputStream dOut = new DataOutputStream(serverSocket.getOutputStream());
            dOut.writeUTF(fileName);
            dOut.flush();
            receiveFile();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile() {
        try {
            int bytesRead;

            DataInputStream serverData = new DataInputStream(serverSocket.getInputStream());

            String fileName = serverData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = serverData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = serverData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            serverData.close();
            logger.info("File " + fileName + " successfully downloaded.");
        } catch (IOException ex) {
            logger.severe("server error. Connection closed.");
        }
    }
}

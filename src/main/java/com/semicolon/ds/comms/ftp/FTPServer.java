package com.semicolon.ds.comms.ftp;

import com.semicolon.ds.core.FileManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class FTPServer implements Runnable{

    private final ServerSocket serverSocket;
    private Socket clientSocket;

    public FTPServer(int port, String userName) throws Exception {
        serverSocket = new ServerSocket(port);
        Logger logger = Logger.getLogger(FTPServer.class.getName());
        logger.info("FTPServer Username: "+ userName);
    }

    public int getPort(){
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        while (true) {

            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread t = new Thread(new DataSendingOperation(clientSocket));
            t.start();
        }
    }
}

class DataSendingOperation implements Runnable {

    private final Socket clientSocket;

    private final Logger logger = Logger.getLogger(DataSendingOperation.class.getName());

    public DataSendingOperation(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
            String fileName = dIn.readUTF();

            sendFile(FileManager.getInstance("").getFile(fileName));
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendFile(File file) {
        try {
            byte[] mybytearray = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = clientSocket.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(file.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            fis.close();
            logger.fine("File " + file.getName() + " sent to client.");
        } catch (Exception e) {
            logger.severe("File does not exist!");
            e.printStackTrace();
        }
    }
}

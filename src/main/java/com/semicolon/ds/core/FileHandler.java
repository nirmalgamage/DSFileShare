package com.semicolon.ds.core;

import com.semicolon.ds.Constants;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class FileHandler {

    private static FileHandler fileHandler;

    private Map<String, String> dataFiles;

    private String fileHandlerName;

    private String seperatorForFiles = System.getProperty("file.separator");
    private String fileRootDirectory;


    private final Logger LOG = Logger.getLogger(FileHandler.class.getName());

    private FileHandler(String fileHandlerName) {
        dataFiles = new HashMap<>();

        this.fileHandlerName = fileHandlerName;
        this.fileRootDirectory =   "." + seperatorForFiles + this.fileHandlerName;

        ArrayList<String> allDataFiles = getFileNameFromResource();

        Random r = new Random();

        for (int i = 0; i < 5; i++){
            dataFiles.put(allDataFiles.get(r.nextInt(allDataFiles.size())), "");
        }

        showFileName();
    }

    public static synchronized FileHandler newFileHandler(String fileHandlerName) {
        if (fileHandler == null) {
            fileHandler = new FileHandler(fileHandlerName);

        }
        return fileHandler;
    }

    public boolean addNewFile(String nameOfTheFile, String pathOfTheFile) {
        this.dataFiles.put(nameOfTheFile, pathOfTheFile);
        return true;
    }

    public Set<String> searchingAFile(String searchQuery) {
        String[] splitSearchQuery = searchQuery.split(" ");

        Set<String> matchingKeys = new HashSet<String>();

        for (String q: splitSearchQuery){
            for (String key: this.dataFiles.keySet()){
                String[] splitNamesOfFile = key.split(" ");
                for (String f : splitNamesOfFile){
                    if (f.toLowerCase().equals(q.toLowerCase())){
                        matchingKeys.add(key);
                    }
                }
            }
        }

        return matchingKeys;
    }

    private ArrayList<String> getFileNameFromResource(){

        ArrayList<String> nameOfTheFiles = new ArrayList<>();

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                (classLoader.getResourceAsStream(Constants.FILE_NAMES)));

        try {

            for (String dataInOneLine; (dataInOneLine = bufferedReader.readLine()) != null;) {
                nameOfTheFiles.add(dataInOneLine);
            }

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return nameOfTheFiles;
    }

    private void showFileName() {
        System.out.println("Number of files: " + dataFiles.size());
        System.out.println("-------------------------");
        for (String dataFileKey: dataFiles.keySet()) {
            System.out.println(dataFileKey);
            generateFile(dataFileKey);
        }
    }

    public String nameOfTheFiles() {
        String fileNames = "Total files: " + dataFiles.size() + "\n";
        fileNames += "++++++++++++++++++++++++++\n";
        for (String s: dataFiles.keySet()) {
            fileNames += s + "\n";
        }
        return fileNames;
    }

    public void generateFile(String nameOfTheFile) {
        try {
            String pathOfTheFile = this.fileRootDirectory + seperatorForFiles + nameOfTheFile;
            File newFile = new File(pathOfTheFile);
            newFile.getParentFile().mkdir();
            if (newFile.createNewFile()) {
                LOG.fine(pathOfTheFile + " File Created");
            } else LOG.fine("File " + pathOfTheFile + " already exists");
            RandomAccessFile f = new RandomAccessFile(newFile, "rw");
            f.setLength(1024 * 1024 * 8);
        } catch (IOException e) {
            LOG.severe("File creating failed");
            e.printStackTrace();
        }
    }

    public File getFileFromFileName(String nameOfTheFile) {
        File file = new File(fileRootDirectory + seperatorForFiles + nameOfTheFile);
        return file;
    }
}

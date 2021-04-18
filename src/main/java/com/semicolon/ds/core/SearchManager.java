package com.semicolon.ds.core;

import com.semicolon.ds.Constants;
import com.semicolon.ds.utils.ConsoleRegister;
import com.semicolon.ds.handlers.QueryHitController;

import java.util.*;

class SearchManager {

    private MessageBroker messageBroker;

    private Map<Integer, SearchResult> fileDownloadOptions;

    SearchManager(MessageBroker messageBroker){
        this.messageBroker = messageBroker;
    }

    int doSearch(String keyword){

        Map<String, SearchResult> searchResults
                = new HashMap<String, SearchResult>();

        QueryHitController queryHitHandler = QueryHitController.getInstance();
        queryHitHandler.setSearchResults(searchResults);
        queryHitHandler.setSearchStartedTime(System.currentTimeMillis());

        this.messageBroker.doSearch(keyword);

        System.out.println("Please be patient till the file results are returned ...");

        try{
            Thread.sleep(Constants.SEARCH_TIMEOUT);

        } catch (InterruptedException e){
            e.printStackTrace();
        }

        printSearchResults(searchResults);
        this.clearSearchResults();
        return fileDownloadOptions.size();
    }

    List<String> doUISearch(String keyword){

        Map<String, SearchResult> searchResults
                = new HashMap<String, SearchResult>();

        QueryHitController queryHitHandler = QueryHitController.getInstance();
        queryHitHandler.setSearchResults(searchResults);
        queryHitHandler.setSearchStartedTime(System.currentTimeMillis());

        this.messageBroker.doSearch(keyword);

        System.out.println("Please be patient till the file results are returned ...");

        try{
            Thread.sleep(Constants.SEARCH_TIMEOUT);

        } catch (InterruptedException e){
            e.printStackTrace();
        }

        List<String> results = new ArrayList<String>();

        int fileIndex = 1;

        this.fileDownloadOptions = new HashMap<Integer, SearchResult>();

        for (String s: searchResults.keySet()){
            SearchResult searchResult = searchResults.get(s);
            String temp = "" + searchResult.getFileName() + "\t" +
                    searchResult.getAddress() + ":" + searchResult.getPort() + "\t" +
                    searchResult.getHops() + "\t" + searchResult.getTimeElapsed() + "ms";
            this.fileDownloadOptions.put(fileIndex, searchResult);
            results.add(temp);
            fileIndex++;
        }

        this.clearSearchResults();

        return results;
    }

    private void clearSearchResults(){
        QueryHitController queryHitHandler = QueryHitController.getInstance();

        queryHitHandler.setSearchResults(null);
    }

    private void printSearchResults(Map<String, SearchResult> searchResults){

        System.out.println("\nFile search results : ");

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Option No");
        headers.add("FileName");
        headers.add("Source");
        headers.add("QueryHit time (ms)");
        headers.add("Hop count");

        ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();

        int fileIndex = 1;

        this.fileDownloadOptions = new HashMap<Integer, SearchResult>();

        for (String s : searchResults.keySet()){
            SearchResult searchResult = searchResults.get(s);
            this.fileDownloadOptions.put(fileIndex, searchResult);

            ArrayList<String> row1 = new ArrayList<String>();
            row1.add("" + fileIndex);
            row1.add(searchResult.getFileName());
            row1.add(searchResult.getAddress() + ":" + searchResult.getPort());
            row1.add("" + searchResult.getTimeElapsed());
            row1.add("" + searchResult.getHops());

            content.add(row1);

            fileIndex++;
        }

        if (fileDownloadOptions.size() == 0){
            System.out.println("Sorry. No files are found!!!");

            return;
        }

        ConsoleRegister ct = new ConsoleRegister(headers,content);
        ct.generateTableLog();

    }

    public SearchResult getFileDetails(int fileIndex){
        return this.fileDownloadOptions.get(fileIndex);
    }
}

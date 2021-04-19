package com.semicolon.ds.core;

import com.semicolon.ds.Constants;
import com.semicolon.ds.utils.ConsoleRegister;
import com.semicolon.ds.handlers.QueryHitController;

import java.util.*;

class SearchHandler {

    private MessageHandler messageHandler;

    private Map<Integer, ResultsForSearchingQuery> downloadingOptionsForFiles;

    SearchHandler(MessageHandler messageHandler){
        this.messageHandler = messageHandler;
    }

    int seaeching(String keyword){

        Map<String, ResultsForSearchingQuery> searchingResults
                = new HashMap<String, ResultsForSearchingQuery>();

        QueryHitController queryHitHandler = QueryHitController.getInstance();
        queryHitHandler.setSearchResults(searchingResults);
        queryHitHandler.setSearchStartedTime(System.currentTimeMillis());

        this.messageHandler.searching(keyword);

        System.out.println("Please be patient till the file results are returned ...");

        try{
            Thread.sleep(Constants.SEARCH_TIMEOUT);

        } catch (InterruptedException e){
            e.printStackTrace();
        }

        viewSearchResults(searchingResults);
        this.resetTheSearchResult();
        return downloadingOptionsForFiles.size();
    }

    Map<String, ResultsForSearchingQuery> searchUI(String keyword){

        Map<String, ResultsForSearchingQuery> searchResults
                = new HashMap<String, ResultsForSearchingQuery>();

        QueryHitController queryHitHandler = QueryHitController.getInstance();
        queryHitHandler.setSearchResults(searchResults);
        queryHitHandler.setSearchStartedTime(System.currentTimeMillis());

        this.messageHandler.searching(keyword);

        System.out.println("Please be patient till the file results are returned ...");

        try{
            Thread.sleep(Constants.SEARCH_TIMEOUT);

        } catch (InterruptedException e){
            e.printStackTrace();
        }

        List<String> results = new ArrayList<String>();

        int fileIndex = 1;

        this.downloadingOptionsForFiles = new HashMap<Integer, ResultsForSearchingQuery>();

        for (String s: searchResults.keySet()){
            ResultsForSearchingQuery resultsForSearchingQuery = searchResults.get(s);
            String temp = "" + resultsForSearchingQuery.getFileNameOfSearchedFile() + "\t" +
                    resultsForSearchingQuery.getAddressOfSearchedFile() + ":" + resultsForSearchingQuery.getPort() + "\t" +
                    resultsForSearchingQuery.getHopsOfSearchedFile() + "\t" + resultsForSearchingQuery.getTimeElapsedOfSearchedFile() + "ms";
            this.downloadingOptionsForFiles.put(fileIndex, resultsForSearchingQuery);
            results.add(temp);
            fileIndex++;
        }

        this.resetTheSearchResult();

        return searchResults;
    }

    private void resetTheSearchResult(){
        QueryHitController queryHitHandler = QueryHitController.getInstance();

        queryHitHandler.setSearchResults(null);
    }

    private void viewSearchResults(Map<String, ResultsForSearchingQuery> searchResults){

        System.out.println("\nFile search results : ");

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Option No");
        headers.add("FileName");
        headers.add("Source");
        headers.add("QueryHit time (ms)");
        headers.add("Hop count");

        ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();

        int fileIndex = 1;

        this.downloadingOptionsForFiles = new HashMap<Integer, ResultsForSearchingQuery>();

        for (String s : searchResults.keySet()){
            ResultsForSearchingQuery resultsForSearchingQuery = searchResults.get(s);
            this.downloadingOptionsForFiles.put(fileIndex, resultsForSearchingQuery);

            ArrayList<String> row1 = new ArrayList<String>();
            row1.add("" + fileIndex);
            row1.add(resultsForSearchingQuery.getFileNameOfSearchedFile());
            row1.add(resultsForSearchingQuery.getAddressOfSearchedFile() + ":" + resultsForSearchingQuery.getPort());
            row1.add("" + resultsForSearchingQuery.getTimeElapsedOfSearchedFile());
            row1.add("" + resultsForSearchingQuery.getHopsOfSearchedFile());

            content.add(row1);

            fileIndex++;
        }

        if (downloadingOptionsForFiles.size() == 0){
            System.out.println("Sorry. No files are found!!!");

            return;
        }

        ConsoleRegister ct = new ConsoleRegister(headers,content);
        ct.generateTableLog();

    }

    public ResultsForSearchingQuery detailsOfTheFile(int fileIndex){
        return this.downloadingOptionsForFiles.get(fileIndex);
    }
}

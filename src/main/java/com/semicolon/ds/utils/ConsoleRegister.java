package com.semicolon.ds.utils;

import java.util.ArrayList;
import java.lang.StringBuilder;

public class ConsoleRegister {



    private final int padding = 4;
    private final char splitCharacter = '-';

    private ArrayList<String> headersList;
    private ArrayList<ArrayList<String>> cTable;
    private ArrayList<Integer> maximumLength;
    private int numRows, numColumns;




    public ConsoleRegister(ArrayList<String> inputHeaders, ArrayList<ArrayList<String>> details){
        this.headersList = inputHeaders;
        this.maximumLength =  new ArrayList<Integer>();

        for(int itr = 0; itr < headersList.size(); itr++){
            maximumLength.add(headersList.get(itr).length());
        }
        this.cTable = details;
        getAllMaximumLength();
    }



    public void changeField(int row, int col, String input){

        cTable.get(row).set(col,input);

        getColMaximumLength(col);
    }



    public void generateTableLog(){

        StringBuilder sBuilder = new StringBuilder();
        StringBuilder sbRowSeparator = new StringBuilder();
        String lPadder = "";
        int rLength = 0;
        String rowSeperator = "";


        for(int i = 0; i < padding; i++){
            lPadder += " ";
        }

        for(int i = 0; i < maximumLength.size(); i++){
            sbRowSeparator.append("|");
            for(int j = 0; j < maximumLength.get(i)+(padding *2); j++){
                sbRowSeparator.append(splitCharacter);
            }
        }
        sbRowSeparator.append("|");
        rowSeperator = sbRowSeparator.toString();

        sBuilder.append(rowSeperator);
        sBuilder.append("\n");

        sBuilder.append("|");
        for(int i = 0; i < headersList.size(); i++){
            sBuilder.append(lPadder);
            sBuilder.append(headersList.get(i));

            for(int k = 0; k < (maximumLength.get(i)- headersList.get(i).length()); k++){
                sBuilder.append(" ");
            }
            sBuilder.append(lPadder);
            sBuilder.append("|");
        }
        sBuilder.append("\n");
        sBuilder.append(rowSeperator);
        sBuilder.append("\n");

        for(int i = 0; i < cTable.size(); i++){
            ArrayList<String> tempRow = cTable.get(i);

            sBuilder.append("|");
            for(int j = 0; j < tempRow.size(); j++){
                sBuilder.append(lPadder);
                sBuilder.append(tempRow.get(j));

                for(int k = 0; k < (maximumLength.get(j)-tempRow.get(j).length()); k++){
                    sBuilder.append(" ");
                }
                sBuilder.append(lPadder);
                sBuilder.append("|");
            }
            sBuilder.append("\n");
            sBuilder.append(rowSeperator);
            sBuilder.append("\n");
        }
        System.out.println(sBuilder.toString());
    }


    private void getAllMaximumLength(){
        for(int i = 0; i < cTable.size(); i++){
            ArrayList<String> arrayList = cTable.get(i);
            for(int j = 0; j < arrayList.size(); j++){

                if(arrayList.get(j).length() > maximumLength.get(j)){
                    maximumLength.set(j, arrayList.get(j).length());
                }
            }
        }
    }


    private void getColMaximumLength(int col){
        for(int i = 0; i < cTable.size(); i++){
            if(cTable.get(i).get(col).length() > maximumLength.get(col)){
                maximumLength.set(col, cTable.get(i).get(col).length());
            }
        }
    }
}

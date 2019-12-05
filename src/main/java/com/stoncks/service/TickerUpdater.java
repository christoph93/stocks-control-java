package com.stoncks.service;


import com.stoncks.StoncksApplication;
import com.stoncks.document.TickerDocument;
import com.stoncks.document.TransactionDocument;
import com.stoncks.io.PriceReader;
import com.stoncks.io.TickerFromUrl;
import com.stoncks.repository.TickerRepository;
import com.stoncks.repository.TransactionRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TickerUpdater implements Runnable {

    private TransactionRepository transactionRepository;
    private TickerRepository tickerRepository;
    private int reqLimitPerMin;
    private boolean onlyMissingSymbols;

    public TickerUpdater(TransactionRepository transactionRepository, TickerRepository tickerRepository, int reqLimitPerMin, boolean onlyMissingSymbols){
        this.tickerRepository = tickerRepository;
        this.transactionRepository = transactionRepository;
        this.reqLimitPerMin = reqLimitPerMin;
        this.onlyMissingSymbols = onlyMissingSymbols;
    }

    private String[] getMissingSymbolsFromTransactions(){
        String[] allSymbols = getAllSymbolsFromTransactions();
        List<TickerDocument> existingTickerDocuments = tickerRepository.findAll();
        HashSet<String> existingSymbols = new HashSet<>();
        HashSet<String> missingSymbols = new HashSet<>();

        for(TickerDocument t : existingTickerDocuments){
            existingSymbols.add(t.getSymbol());
        }

        for(String s : allSymbols){
            if(!existingSymbols.contains(s)){
                missingSymbols.add(s);
            }
        }
        return Arrays.copyOf(missingSymbols.toArray(), missingSymbols.toArray().length, String[].class);
    }

    private String[] getAllSymbolsFromTransactions(){
        List<TransactionDocument> transactionDocuments = transactionRepository.findAll();
        HashSet<String> symbolsFromTrans = new HashSet<>();

        for (TransactionDocument t : transactionDocuments) {
            symbolsFromTrans.add(t.getSymbol() + ".SAO");
        }

        return Arrays.copyOf(symbolsFromTrans.toArray(), symbolsFromTrans.toArray().length, String[].class);
    }

    private void updateFromTransactions(boolean onlyMissingSymbols) throws InterruptedException {

        long firstReq = 0;
        int reqCount = 0;
        long intervalInSec;

        String[] symbolsToUpdate = (onlyMissingSymbols ? getMissingSymbolsFromTransactions() : getAllSymbolsFromTransactions());

        System.out.println(
                (onlyMissingSymbols ? "Updating missing symbols:" : "Updating all symbols")
        );

        System.out.println(Arrays.toString(symbolsToUpdate));

        TickerFromUrl tfu = new TickerFromUrl("TIME_SERIES_DAILY", StoncksApplication.API_KEY);

        for(String currentSymbol : symbolsToUpdate){

            if(reqCount == 0) firstReq = System.currentTimeMillis();

            intervalInSec = (System.currentTimeMillis() - firstReq)/1000;
            System.out.println("Current interval from firstReq: " + intervalInSec);

            while(reqCount == this.reqLimitPerMin && intervalInSec < 60) {
                intervalInSec = (System.currentTimeMillis() - firstReq)/1000;
                System.out.println("Reached 5 reqs. Waiting for: " + (60-intervalInSec));
                Thread.sleep(2000);
            }

            if(reqCount == 5){
                reqCount = 0;
                firstReq = System.currentTimeMillis();
            }

            //delete all ticker for that symbol, in case there us more than one
            List<TickerDocument> tickersToDelete = tickerRepository.deleteBySymbol(currentSymbol);
            if(tickersToDelete != null) {
                for (TickerDocument t : tickersToDelete) {
                    System.out.println("Deleting ticker " + t.getSymbol());
                }
            }

            TickerDocument temp = tfu.getTicker(currentSymbol,"full");

            PriceReader pr = new PriceReader(temp, "Time Series (Daily)");

            System.out.println("Saving Ticker " + temp.getSymbol());
            tickerRepository.save(pr.setClosingPrices());
            Thread.sleep(1000);
            reqCount++;
            }

    }


    @Override
    public void run() {
        System.out.println("Starting TickerUpdater thread");
        try {
            updateFromTransactions(this.onlyMissingSymbols);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Ending TickerUpdater thread");
    }
}

package edu.yu.cs.intro.finalExam2021;

import java.util.*;

public class Bank {
    private Map<Long, Account> bankAccounts;
    private Map<String,Double> stocksSymbolToPrice;
    public Bank(){
        this.bankAccounts = new HashMap<>();
        this.stocksSymbolToPrice = new HashMap<>();
    }
    protected boolean addNewStockToMarket(String tickerSymbol, double sharePrice){
        boolean alreadyThere = this.stocksSymbolToPrice.containsKey(tickerSymbol);
        if(alreadyThere){
            this.stocksSymbolToPrice.replace(tickerSymbol,sharePrice);
        }else{
            this.stocksSymbolToPrice.put(tickerSymbol,sharePrice);
        }
        return alreadyThere;
    }
    public double getStockPrice(String symbol){
        if(this.stocksSymbolToPrice.containsKey(symbol)){
            return this.stocksSymbolToPrice.get(symbol);
        }else{
            return 0;
        }
    }
    public Set<String> getAllStockTickerSymbols(){
        return this.stocksSymbolToPrice.keySet();
    }
    public int getNumberOfOutstandingShares(String tickerSymbol){
        int totalShares = 0;
        if(tickerSymbol == null || !this.stocksSymbolToPrice.containsKey(tickerSymbol)){
            return 0;
        }else{
            Set<Long> ssSet = this.bankAccounts.keySet();
            for(Long a: ssSet){
                Account b = this.bankAccounts.get(a);
                totalShares += b.getNumberOfSharesOwned(tickerSymbol);
            }
        }
        return totalShares;
    }
    public double getMarketCapitalization(String tickerSymbol){
        int totalShares = this.getNumberOfOutstandingShares(tickerSymbol);
        double sharePrice = this.stocksSymbolToPrice.get(tickerSymbol);
        return (totalShares * sharePrice);
    }
    public double getTotalCashInBank(){
        double totalCash = 0;
        Set<Long> ssSet = this.bankAccounts.keySet();
        for(Long a: ssSet){
            totalCash += this.bankAccounts.get(a).getAvailableCash();
        }
        return totalCash;
    }
    public void createNewAccount(String firstName, String lastName, long socialSecurityNumber, String userName, String password){
        if(this.bankAccounts.containsKey(socialSecurityNumber)){
            throw new IllegalArgumentException ("Account with given SS# already exists.");
        }else{
            Account account = new Account(firstName,lastName,socialSecurityNumber,userName,password,this);
            this.bankAccounts.put(socialSecurityNumber,account);
        }
    }
    protected Account getAccount(long socialSecurityNumber, String userName, String password)throws AuthenticationException{
        if(this.bankAccounts.containsKey(socialSecurityNumber) && this.bankAccounts.get(socialSecurityNumber).getUserName().equals(userName) && this.bankAccounts.get(socialSecurityNumber).getPassword().equals(password)){
            return this.bankAccounts.get(socialSecurityNumber);
        }else{
            throw new AuthenticationException ();
        }
    }
    public double depositCash(long socialSecurityNumber, String userName, String password, double amount) throws AuthenticationException{
        if(!this.bankAccounts.containsKey(socialSecurityNumber) || !this.bankAccounts.get(socialSecurityNumber).getUserName().equals(userName) || !this.bankAccounts.get(socialSecurityNumber).getPassword().equals(password)){
            throw new AuthenticationException ();
        }else{
            this.bankAccounts.get(socialSecurityNumber).depositCash(amount);
        }
        return this.bankAccounts.get(socialSecurityNumber).getAvailableCash();
    }
    public void withdrawCash(long socialSecurityNumber, String userName, String password, double amount) throws AuthenticationException, InsufficientAssetsException {
        if(!this.bankAccounts.containsKey(socialSecurityNumber) || !this.bankAccounts.get(socialSecurityNumber).getUserName().equals(userName) || !this.bankAccounts.get(socialSecurityNumber).getPassword().equals(password)){
            throw new AuthenticationException ();
        }else if(this.bankAccounts.get(socialSecurityNumber).getAvailableCash() < amount){
            throw new InsufficientAssetsException ();
        }else{
            this.bankAccounts.get(socialSecurityNumber).depositCash(-amount);
        }
    }
    public double checkTotalStockWorth(long socialSecurityNumber, String userName, String password) throws AuthenticationException {
        double totalStockWorth = 0;
        if(!this.bankAccounts.containsKey(socialSecurityNumber) || !this.bankAccounts.get(socialSecurityNumber).getUserName().equals(userName) || !this.bankAccounts.get(socialSecurityNumber).getPassword().equals(password)){
            throw new AuthenticationException ();
        }else{
            totalStockWorth = this.bankAccounts.get(socialSecurityNumber).getStockWorth();
        }
        return totalStockWorth;
    }
    public double checkCashBalance(long socialSecurityNumber, String userName, String password) throws AuthenticationException {
        double totalCashBalance = 0;
        if(!this.bankAccounts.containsKey(socialSecurityNumber) || !this.bankAccounts.get(socialSecurityNumber).getUserName().equals(userName) || !this.bankAccounts.get(socialSecurityNumber).getPassword().equals(password)){
            throw new AuthenticationException ();
        }else{
            totalCashBalance = this.bankAccounts.get(socialSecurityNumber).getAvailableCash();
        }
        return totalCashBalance;
    }
    public void purchaseStock(long socialSecurityNumber, String userName, String password, String tickerSymbol, int shares) throws AuthenticationException, InsufficientAssetsException {
        if(!this.bankAccounts.containsKey(socialSecurityNumber) || !this.bankAccounts.get(socialSecurityNumber).getUserName().equals(userName) || !this.bankAccounts.get(socialSecurityNumber).getPassword().equals(password)){
            throw new AuthenticationException ();
        }else if(!this.stocksSymbolToPrice.containsKey(tickerSymbol)){
            throw new IllegalArgumentException ("No such stock exists in this Bank.");
        }else if(this.bankAccounts.get(socialSecurityNumber).getAvailableCash() < (this.stocksSymbolToPrice.get(tickerSymbol) * shares)){
            throw new InsufficientAssetsException ();
        }else{
            this.bankAccounts.get(socialSecurityNumber).depositCash(-(this.stocksSymbolToPrice.get(tickerSymbol) * shares));
            this.bankAccounts.get(socialSecurityNumber).depositStockShares(tickerSymbol,shares);
        }
    }
    public void sellStock(long socialSecurityNumber, String userName, String password, String tickerSymbol, int shares) throws AuthenticationException, InsufficientAssetsException {
        if(!this.bankAccounts.containsKey(socialSecurityNumber) || !this.bankAccounts.get(socialSecurityNumber).getUserName().equals(userName) || !this.bankAccounts.get(socialSecurityNumber).getPassword().equals(password)){
            throw new AuthenticationException ();
        }else if(!this.stocksSymbolToPrice.containsKey(tickerSymbol)){
            throw new IllegalArgumentException ("No such stock exists in this Bank.");
        }else if(this.bankAccounts.get(socialSecurityNumber).getNumberOfSharesOwned(tickerSymbol) < shares){
            throw new InsufficientAssetsException ();
        }else{
            this.bankAccounts.get(socialSecurityNumber).depositCash(this.stocksSymbolToPrice.get(tickerSymbol) * shares);
            this.bankAccounts.get(socialSecurityNumber).removeStockShares(tickerSymbol,shares);
        }
    }
    public double getNetWorth(long socialSecurityNumber, String userName, String password) throws AuthenticationException {
        double netWorth;
        if(!this.bankAccounts.containsKey(socialSecurityNumber) || !this.bankAccounts.get(socialSecurityNumber).getUserName().equals(userName) || !this.bankAccounts.get(socialSecurityNumber).getPassword().equals(password)){
            netWorth = 0;
        }else{
            netWorth = this.bankAccounts.get(socialSecurityNumber).getStockWorth() + this.bankAccounts.get(socialSecurityNumber).getAvailableCash();
        }
        return netWorth;
    }
}
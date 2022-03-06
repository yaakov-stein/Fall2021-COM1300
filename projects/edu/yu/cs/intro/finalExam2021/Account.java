package edu.yu.cs.intro.finalExam2021;

import java.util.HashMap;
import java.util.Map;

public class Account {
    private final String firstName;
    private final String lastName;
    private final long socialSecurityNumber;
    private final String userName;
    private final String password;
    private double cash;
    private HashMap<String,Integer> stockToNumberOfSharesOwned;
    private Bank bank;

    protected Account(String firstName, String lastName, long socialSecurityNumber, String userName, String password, Bank bank) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
        this.userName = userName;
        this.password = password;
        this.cash = 0;
        this.stockToNumberOfSharesOwned = new HashMap<>();
        this.bank = bank;
    }
    /**
     * total cash in savings + total cash in brokerage + total value of shares in brokerage
     * return 0 if the patron doesn't have any accounts
     */
    protected double getNetWorth() {
        return this.cash + getStockWorth();
    }

    /**
     * deposit shares of a stock into this account
     * @param tickerSymbol
     * @param shares
     */
    protected void depositStockShares(String tickerSymbol, int shares){
        Integer existingShares = this.stockToNumberOfSharesOwned.get(tickerSymbol);
        existingShares = existingShares == null ? shares : existingShares + shares;
        this.stockToNumberOfSharesOwned.put(tickerSymbol,existingShares);
    }

    protected void removeStockShares(String tickerSymbol, int shares){
        Integer existingShares = this.stockToNumberOfSharesOwned.get(tickerSymbol);
        existingShares -= shares;
        this.stockToNumberOfSharesOwned.put(tickerSymbol,existingShares);
    }

    protected int getNumberOfSharesOwned(String stock){
        Integer number = this.stockToNumberOfSharesOwned.get(stock);
        return number == null ? 0 : number;
    }

    /**
     * @return how much the account's stock holdings are worth
     */
    protected double getStockWorth(){
        double worth = 0;
        for(Map.Entry<String,Integer> entry : this.stockToNumberOfSharesOwned.entrySet()){
            double price = this.bank.getStockPrice(entry.getKey());
            worth += (price * entry.getValue());
        }
        return worth;
    }

    protected void depositCash(double amount){
        this.cash += amount;
    }
    protected double getAvailableCash(){
        return this.cash;
    }

    protected String getFirstName() {
        return this.firstName;
    }

    protected String getLastName() {
        return this.lastName;
    }

    protected String getUserName() {
        return this.userName;
    }

    protected String getPassword() {
        return this.password;
    }

    protected long getSocialSecurityNumber() {
        return this.socialSecurityNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof Account)){
            return false;
        }
        Account account = (Account) o;
        return socialSecurityNumber == account.socialSecurityNumber;
    }

    @Override
    public int hashCode() {
        return (int) (socialSecurityNumber ^ (socialSecurityNumber >>> 32));
    }
}

package edu.yu.cs.intro.finalExam2021;

import java.util.Set;

public class BankDemo {
    public static void main(String[] args) throws Exception{
        Bank bank = new Bank();

        //create a new account and do some cash transactions
        long mosheSS = 123456;
        String mosheName = "moshe";
        String moshePwd = "hi";
        bank.createNewAccount("Moshe","Schwartz",mosheSS,mosheName,moshePwd);
        try{
            bank.createNewAccount("","",mosheSS,"","");
            assert false; //should never get to this line
        }catch (IllegalArgumentException e){
        }
        bank.depositCash(mosheSS,mosheName,moshePwd,100d);
        assert 100d == bank.checkCashBalance(mosheSS,mosheName,moshePwd);
        assert 100d == bank.getNetWorth(mosheSS,mosheName,moshePwd);

        bank.depositCash(mosheSS,mosheName,moshePwd,0.25d);
        assert 100.25 == bank.checkCashBalance(mosheSS,mosheName,moshePwd);
        assert 100.25 == bank.getNetWorth(mosheSS,mosheName,moshePwd);

        bank.withdrawCash(mosheSS,mosheName,moshePwd,50.75d);
        assert 49.5 == bank.checkCashBalance(mosheSS,mosheName,moshePwd);
        assert 49.5 == bank.getNetWorth(mosheSS,mosheName,moshePwd);

        bank.depositCash(mosheSS,mosheName,moshePwd,150.5d);
        //try to take out more cash than he has
        try{
            bank.withdrawCash(mosheSS,mosheName,moshePwd,10000);
            assert false; //should never get to this line
        }catch(InsufficientAssetsException e){
        }
        //balance should be unaffected by failed transaction
        assert 200 == bank.checkCashBalance(mosheSS,mosheName,moshePwd);
        assert 200 == bank.getNetWorth(mosheSS,mosheName,moshePwd);

        //create a second account and do some cash transactions
        long aharonSS = 789123;
        String aharonName = "aharon";
        String aharonPwd = "hi";
        bank.createNewAccount("Aharon","Schwartz",aharonSS,aharonName,aharonPwd);
        bank.depositCash(aharonSS,aharonName,aharonPwd,500);

        //add some stocks to the list of stocks available in the bank
        bank.addNewStockToMarket("IBM",5);
        assert 0 == bank.getMarketCapitalization("IBM");
        bank.addNewStockToMarket("GOOG",50);

        //users buy some stocks
        bank.purchaseStock(mosheSS,mosheName,moshePwd,"IBM",10);
        assert 50 == bank.getMarketCapitalization("IBM");
        assert 150 == bank.checkCashBalance(mosheSS,mosheName,moshePwd);
        assert 200 == bank.getNetWorth(mosheSS,mosheName,moshePwd);

        bank.purchaseStock(aharonSS,aharonName,aharonPwd,"GOOG",2);
        assert 100 == bank.getMarketCapitalization("GOOG");
        bank.purchaseStock(aharonSS,aharonName,aharonPwd,"IBM",3);
        assert 115 == bank.checkTotalStockWorth(aharonSS,aharonName,aharonPwd);
        assert 385 == bank.checkCashBalance(aharonSS,aharonName,aharonPwd);
        assert 500 == bank.getNetWorth(aharonSS,aharonName,aharonPwd);

        assert 13 == bank.getNumberOfOutstandingShares("IBM");
        assert 65 == bank.getMarketCapitalization("IBM");

        bank.sellStock(aharonSS,aharonName,aharonPwd,"GOOG",1);
        assert 50 == bank.getMarketCapitalization("GOOG");
        assert 65 == bank.checkTotalStockWorth(aharonSS,aharonName,aharonPwd);
        assert 435 == bank.checkCashBalance(aharonSS,aharonName,aharonPwd);
        assert 500 == bank.getNetWorth(aharonSS,aharonName,aharonPwd);

        //user tries to buy more stock than he has money for
        try{
            bank.purchaseStock(aharonSS,aharonName,aharonPwd,"GOOG",100);
            assert false; //should never get to this line
        }catch(InsufficientAssetsException e){
        }
        //balance should be unaffected by failed transaction
        assert 435 == bank.checkCashBalance(aharonSS,aharonName,aharonPwd);
        assert 500 == bank.getNetWorth(aharonSS,aharonName,aharonPwd);

        //user tries to sell more shares than he has
        try{
            bank.sellStock(aharonSS,aharonName,aharonPwd,"GOOG",100);
            assert false; //should never get to this line
        }catch(InsufficientAssetsException e){
        }
        //balance should be unaffected by failed transaction
        assert 435 == bank.checkCashBalance(aharonSS,aharonName,aharonPwd);
        assert 500 == bank.getNetWorth(aharonSS,aharonName,aharonPwd);

        //user tries to buy a stock that doesn't exist
        try{
            bank.purchaseStock(mosheSS,mosheName,moshePwd,"FAKE",1);
            assert false; //should never get to this line
        }catch(IllegalArgumentException e){
        }
        //balance should be unaffected by failed transaction
        assert 150 == bank.checkCashBalance(mosheSS,mosheName,moshePwd);
        assert 200 == bank.getNetWorth(mosheSS,mosheName,moshePwd);

        //try to check balance with wrong password
        try{
            bank.checkCashBalance(mosheSS,mosheName,"foo");
            assert false; //should never get to this line
        }catch(AuthenticationException e){
        }
                
        //last checks on bank
        assert 585 == bank.getTotalCashInBank();
        Set<String> symbols = bank.getAllStockTickerSymbols();
        assert 2 == symbols.size();
        assert symbols.contains("IBM");
        assert symbols.contains("GOOG");
        assert 5 == bank.getStockPrice("IBM");
        assert 50 == bank.getStockPrice("GOOG");
    }
}

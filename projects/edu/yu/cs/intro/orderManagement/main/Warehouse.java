package edu.yu.cs.intro.orderManagement;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Warehouse {
    private Map<Integer,ArrayList<Integer>> inventory;
    private Set<Product> productSet;
    protected Warehouse(){
        this.inventory = new HashMap<>();
        this.productSet = new HashSet<>();
    }
    protected Set<Product> getAllProductsInCatalog(){
        Set<Product> copyOfProducts = new HashSet<>();
        copyOfProducts.addAll(this.productSet);
        return copyOfProducts;
    }
    protected void addNewProductToWarehouse(Product product, int desiredStockLevel){
        int productNumber = product.getItemNumber();
        if(this.isInCatalog(productNumber)){
            throw new IllegalArgumentException ("Product is already in catalog");
        }
        ArrayList<Integer> stock = new ArrayList<>();
        stock.add(0,desiredStockLevel);//current stock
        stock.add(1,desiredStockLevel);//default stock
        stock.add(2,0);//0 == restockable, 1 == not restockable
        this.inventory.put(product.getItemNumber(),stock);
        this.productSet.add(product);
    }
    protected void restock(int productNumber, int minimum){
        if(!this.isRestockable(productNumber)){
            throw new IllegalArgumentException ("Item is not restockable");
        }else if (this.inventory.get(productNumber).get(0) >= minimum){
            return;
        }else{
            int newStock = minimum > this.inventory.get(productNumber).get(1) ? minimum : this.inventory.get(productNumber).get(1);
            this.inventory.get(productNumber).set(0,newStock);
        }
    }
    protected int setDefaultStockLevel(int productNumber, int quantity){
        if(!this.isRestockable(productNumber)){
            throw new IllegalArgumentException ("Product is not restockable");
        }
        int oldDefaultStockLevel = this.inventory.get(productNumber).get(1);
        this.inventory.get(productNumber).set(1,quantity);
        return oldDefaultStockLevel;
    }
    protected int getStockLevel(int productNumber){
        if(!this.isInCatalog(productNumber)){
            return 0;
        }
        return this.inventory.get(productNumber).get(0);
    }
    protected boolean isInCatalog(int itemNumber){
        return this.inventory.containsKey(itemNumber);
    }
    protected boolean isRestockable(int itemNumber){
        if(this.isInCatalog(itemNumber) && this.inventory.get(itemNumber).get(2) == 0){
            return true;
        }
        return false;
    }
    protected int doNotRestock(int productNumber){//May need to convert this to a set
        if(!this.inventory.containsKey(productNumber)){
            throw new IllegalArgumentException ("Cannot place on \"do no restock\" list. Item is not in Warehouse.");
        }
        this.inventory.get(productNumber).set(2,1);
        return this.inventory.get(productNumber).get(0);
    }
    protected boolean canFulfill(int productNumber, int quantity){
        if(!this.isInCatalog(productNumber) || quantity > this.getStockLevel(productNumber)){
            return false;
        }
        return true;
    }
    protected void fulfill(int productNumber, int quantity){
        if(!this.canFulfill(productNumber,quantity)){
            throw new IllegalArgumentException ("Cannot fulfill Order");
        }else{
            this.inventory.get(productNumber).set(0,this.inventory.get(productNumber).get(0)-quantity);
        }
    }
}
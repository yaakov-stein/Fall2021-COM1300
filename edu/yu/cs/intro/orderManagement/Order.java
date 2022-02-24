package edu.yu.cs.intro.orderManagement;
import java.util.Map;
import java.util.HashMap;

public class Order {
    private Map<Item,Integer> orderMap;
    private double productPriceTotal;
    private double servicePriceTotal;
    private boolean orderCompleted;
    public Order(){
        this.orderMap = new HashMap<>();
    }
    public Item[] getItems(){
        if(this.orderMap.isEmpty()){
            throw new IllegalArgumentException ("Cannot process Order. Your Order is empty.");
        }
        Item[] itemArray = this.orderMap.keySet().toArray(new Item[0]);
        return itemArray;
    }
    public int getQuantity(Item b){
        if(!this.orderMap.containsKey(b)){
            return 0;
        }
        return this.orderMap.get(b);
    }
    public void addToOrder(Item item, int quantity){
        if(this.orderMap.containsKey(item)){
            this.orderMap.replace(item,this.orderMap.get(item) + quantity);
        }else{
            this.orderMap.put(item,quantity);
        }
        double price = item.getPrice();
        if(item instanceof Product){
            productPriceTotal += (price * quantity);
        }else{
            servicePriceTotal += (price * quantity);
        }
    }
    public double getProductsTotalPrice(){
        double ppt = Math.round(productPriceTotal * 100);
        return ppt/100;
    }
    public double getServicesTotalPrice(){
        double spt = Math.round(servicePriceTotal * 100);
        return spt/100;
    }
    public boolean isCompleted() {
        return orderCompleted;
    }
    public void setCompleted(boolean completed) {
        this.orderCompleted = completed;
    }
}
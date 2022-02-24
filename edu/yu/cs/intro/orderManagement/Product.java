package edu.yu.cs.intro.orderManagement;

public class Product implements Item {
    private String name;
    private double price;
    private int productID;
    public Product(String name, double price, int productID){
        this.name = name;
        this.price = price;
        this.productID = productID;
    }
    @Override
    public int getItemNumber() {
        return this.productID;
    }
    @Override
    public String getDescription() {
        return this.name;
    }
    @Override
    public double getPrice() {
        return this.price;
    }
    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null){
            return false;
        }
        if(o instanceof Product){
            if(((Product)o).getItemNumber() == this.productID){
                return true;
            }
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(this.productID);
    }
}
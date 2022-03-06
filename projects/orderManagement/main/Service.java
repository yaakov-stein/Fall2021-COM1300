package edu.yu.cs.intro.orderManagement;

public class Service implements Item {
    private double pricePerHour;
    private int numberOfHours;
    private int serviceID;
    private String description;
    public Service(double pricePerHour, int numberOfHours, int serviceID, String description){
        this.pricePerHour = pricePerHour;
        this.numberOfHours = numberOfHours;
        this.serviceID = serviceID;
        this.description = description;
    }
    public int getNumberOfHours(){
        return this.numberOfHours;
    }
    @Override
    public int getItemNumber() {
        return this.serviceID;
    }
    @Override
    public String getDescription() {
        return this.description;
    }
    @Override
    public double getPrice() {
        double totalPrice = Math.round(this.pricePerHour * this.numberOfHours * 100);
        return totalPrice/100;
    }
    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null){
            return false;
        }
        if(o instanceof Service){
            if(((Service)o).getItemNumber() == this.serviceID){
                return true;
            }
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(this.serviceID);
    }
}
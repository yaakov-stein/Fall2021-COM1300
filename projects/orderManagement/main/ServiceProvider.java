package edu.yu.cs.intro.orderManagement;
import java.util.Set;
import java.util.HashSet;

public class ServiceProvider implements Comparable<ServiceProvider>{
    private String name;
    private int id;
    private Set<Service> services;
    private int timeOfLastServiceProvided;
    private int currentOrder = 0;
    private boolean busy = false;
    private boolean willBeBusy = false;
    public ServiceProvider(String name, int id, Set<Service> services){
        this.name = name;
        this.id = id;
        this.services = services;
    }
    public String getName(){
        return this.name;
    }
    public int getId(){
        return this.id;
    }
    protected boolean getBusyStatus(){
        return this.busy;
    }
    protected void setWillBeBusy(boolean willBeBusy){
        this.willBeBusy = willBeBusy;
    }
    protected boolean getWillBeBusy(){
        return this.willBeBusy;
    }
    protected void setNewCurrentOrder(){
        this.currentOrder += 1;
        if(this.currentOrder > this.timeOfLastServiceProvided + 3){
            this.busy = false;
        }
    }
    protected void assignToCustomer(){
        if(this.busy){
            throw new IllegalStateException ("Service Provider is already busy");
        }
        this.busy = true;
        this.timeOfLastServiceProvided = this.currentOrder;
    }
    protected void endCustomerEngagement(){
        if(!this.busy){
            throw new IllegalStateException ("Service Provider is already free");
        }
        this.busy = false;
    }
    protected boolean addService(Service s){
        return this.services.add(s);
    }

    protected boolean removeService(Service s){
        return this.services.remove(s);
    }
    public Set<Service> getServices(){
        Set<Service> copyOfServices = new HashSet<>();
        copyOfServices.addAll(this.services);
        return copyOfServices;
    }
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null){
            return false;
        }
        if(o instanceof ServiceProvider){
            if(((ServiceProvider)o).getId() == this.id){
                return true;
            }
        }
        return false;
    }
    @Override
    public int hashCode(){
        return Integer.hashCode(this.id);
    }
    @Override
    public int compareTo(ServiceProvider other){
        if(this.id > other.getId()){
            return 1;
        }else if(this.id == other.getId()){
            return 0;
        }else{
            return -1;
        }
    }
}
package edu.yu.cs.intro.orderManagement;
import java.util.*;

public class OrderManagementSystem {
    private int productStockLevel;
    private Warehouse centralWarehouse;
    private Set<Service> allServices;
    private Map<Service,ArrayList<ServiceProvider>> serviceMap;
    private Set<ServiceProvider> serviceProviders;
    private Set<Item> discontinuedItems;
    public OrderManagementSystem(Set<Product> products, int defaultProductStockLevel, Set<ServiceProvider> serviceProviders) {
        this(products,defaultProductStockLevel,serviceProviders,new Warehouse());
    }   
    public OrderManagementSystem(Set<Product> products, int defaultProductStockLevel, Set<ServiceProvider> serviceProviders, Warehouse warehouse) {
        this.productStockLevel = defaultProductStockLevel;
        this.centralWarehouse = warehouse;
        this.discontinuedItems = new HashSet<>();
        this.addNewProducts(products);
        this.serviceProviders = serviceProviders;
        this.serviceMap = new HashMap<>();
        for(ServiceProvider a : serviceProviders){
            this.addServiceProvider(a);
        }
        this.allServices = this.serviceMap.keySet();
    }
    public void placeOrder(Order order) {
        Item[] allItemsInOrder = order.getItems();
        List<Product> allProductsInOrder = new ArrayList<>();
        List<Service> allServicesInOrder = new ArrayList<>();
        for(Item a:allItemsInOrder){
            if(a instanceof Product){
                allProductsInOrder.add((Product)a);
            }else{
                allServicesInOrder.add((Service)a);
            }
        }
        if (this.validateProducts(allProductsInOrder,order) != 0){
            throw new IllegalArgumentException ("Cannot fulfill all of requested Products");
        }
        if(this.validateServices(allServicesInOrder,order) != 0){
            throw new IllegalStateException ("Cannot fulfill all of requested Services");
        }
        for(Product a:allProductsInOrder){
            int quantityOfProduct = order.getQuantity(a);
            if(!this.centralWarehouse.canFulfill(a.getItemNumber(),quantityOfProduct)){
                this.centralWarehouse.restock(a.getItemNumber(),quantityOfProduct);
            }
            this.centralWarehouse.fulfill(a.getItemNumber(),quantityOfProduct);
        }
        order.setCompleted(true);
        for(Service a:allServicesInOrder){
            ArrayList<ServiceProvider> arrayListX = this.serviceMap.get(a);
            int freeMenNeeded = order.getQuantity(a);
            int freeMenAttained = 0;
            for(int i = 0; i < arrayListX.size() && freeMenNeeded > freeMenAttained;i++){
                if(!arrayListX.get(i).getBusyStatus()){
                    arrayListX.get(i).assignToCustomer();
                }
            }
        }
        for(ServiceProvider a:serviceProviders){
            a.setNewCurrentOrder();
        }
    }
    protected int validateServices(Collection<Service> services, Order order) {
        ArrayList<Service> serviceList = new ArrayList<>(services);
        if(!this.allServices.containsAll(serviceList)){
            serviceList.removeAll(this.allServices);
            return serviceList.get(0).getItemNumber();
        }
        int faultyService = 0;
        for(Service a:services){
            ArrayList<ServiceProvider> arrayListX = this.serviceMap.get(a);
            int freeMenNeeded = order.getQuantity(a);
            int freeMenAttained = 0;
            for(int i = 0; i < arrayListX.size() && freeMenNeeded > freeMenAttained;i++){
                if(!arrayListX.get(i).getBusyStatus() && !arrayListX.get(i).getWillBeBusy()){
                    freeMenAttained++;
                    arrayListX.get(i).setWillBeBusy(true);
                }
            }
            if(freeMenNeeded > freeMenAttained){
                faultyService = a.getItemNumber();
                break;
            }
        }
        for(ServiceProvider b:this.serviceProviders){
            b.setWillBeBusy(false);
        }
        return faultyService;
    }
    protected int validateProducts(Collection<Product> products, Order order) {
        ArrayList<Product> productList = new ArrayList<>(products);
        if(!this.centralWarehouse.getAllProductsInCatalog().containsAll(productList)){
            productList.removeAll(this.centralWarehouse.getAllProductsInCatalog());
            return productList.get(0).getItemNumber();
        }
        for(Product a:products){
            int quantityOfProduct = order.getQuantity(a);
            if(!this.centralWarehouse.canFulfill(a.getItemNumber(),quantityOfProduct) && !this.centralWarehouse.isRestockable(a.getItemNumber())){
                return a.getItemNumber();
            }
        }
        return 0;
    }
    protected Set<Product> addNewProducts(Collection<Product> products){
        Set<Product> successfulAdds = new HashSet<>();//Is it necessary to make this a hashset?
        Set<Product> preexistingProducts = this.centralWarehouse.getAllProductsInCatalog();
        products.removeAll(this.discontinuedItems);
        for(Product a:products){
            if(preexistingProducts.isEmpty() || !preexistingProducts.contains(a)){
                this.centralWarehouse.addNewProductToWarehouse(a,this.productStockLevel);
                successfulAdds.add(a);
            } 
            if(preexistingProducts.contains(a) && this.centralWarehouse.getStockLevel(a.getItemNumber())<this.productStockLevel){
                this.centralWarehouse.restock(a.getItemNumber(),productStockLevel);
            }
        }
        return successfulAdds;
    }
    protected void addServiceProvider(ServiceProvider provider){
        Set<Service> currentServices = provider.getServices();
        for(Service a:currentServices){
            if(this.discontinuedItems.contains(a)){
                continue;
            }
            if(!this.serviceMap.containsKey(a)){
                ArrayList<ServiceProvider> serviceProvidersForServiceX = new ArrayList<>();
                serviceProvidersForServiceX.add(provider);
                this.serviceMap.put(a,serviceProvidersForServiceX);
            }else{
                ArrayList<ServiceProvider> arrayListX = this.serviceMap.get(a);
                arrayListX.add(provider);
                this.serviceMap.replace(a,arrayListX);
            }
        }
    }
    public Set<Product> getProductCatalog() {
        return this.centralWarehouse.getAllProductsInCatalog();
    }
    public Set<Service> getOfferedServices() {
        return this.allServices;
    }
    protected void discontinueItem(Item item){
        this.discontinuedItems.add(item);
        if(item instanceof Service){
            this.serviceMap.remove(item);
        }else{
            this.centralWarehouse.doNotRestock(item.getItemNumber());
        }
    }
    protected void setDefaultProductStockLevel(Product prod, int level) {
        this.centralWarehouse.setDefaultStockLevel(prod.getItemNumber(),level);
    }
}
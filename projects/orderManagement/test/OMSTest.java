package edu.yu.cs.intro.orderManagement;
import java.util.*;
public class OMSTest{
	private OrderManagementSystem oms1;
	private OrderManagementSystem oms2;
	private OrderManagementSystem oms3;
	private Warehouse wh1;
	private Set<ServiceProvider> spSet1;
	private Set<Service> sSet1;
	private Set<Product> pSet1;
	private Set<ServiceProvider> spSet2;
	private Set<Service> sSet2;
	private Set<Product> pSet2;
	private Set<ServiceProvider> spSet3;
	public static void main(String[] args) {
		OMSTest oMST = new OMSTest();
		oMST.runOMSTest();
	}
	public OMSTest(){
		this.pSet1 = new HashSet<>();
		this.pSet2 = new HashSet<>();
		this.createProductsSets();
		this.spSet1 = new HashSet<>();
		this.spSet2 = new HashSet<>();
		this.spSet3 = new HashSet<>();
		this.createServiceProviderSets();
		this.wh1 = new Warehouse();
		this.stockWarehouse();
		this.oms1 = new OrderManagementSystem(this.pSet1,10,this.spSet1,wh1);
		this.oms2 = new OrderManagementSystem(this.pSet2,20,this.spSet2);
		this.oms3 = new OrderManagementSystem(this.pSet2,20,this.spSet3);
	}
	protected void runOMSTest(){
		Set<Product> proposedOMSProductCatalog = getAProductCatalog1();
		assert this.oms1.getProductCatalog().containsAll(proposedOMSProductCatalog);
		assert proposedOMSProductCatalog.containsAll(this.oms1.getProductCatalog());
		assert this.oms2.getProductCatalog().containsAll(this.pSet2);
		assert this.pSet2.containsAll(this.oms2.getProductCatalog());
		this.oms1.discontinueItem(new Product("BOOK",9.99,2000));
		Order o1 = new Order();
		o1.addToOrder(new Product("BOK",9.9,2000),9);
		this.oms1.placeOrder(o1);
		assert o1.isCompleted();
		assert this.wh1.getStockLevel(2000) == 1;
		o1.setCompleted(false);
		o1.addToOrder(new Product("BO)OK",9.999,2000),2);
		boolean caught = false;
        try{
            this.oms1.placeOrder(o1);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        assert caught;
        this.oms2.placeOrder(o1);
        assert o1.isCompleted();
        Order o2 = new Order();
        this.fillOrder(o2);
        this.oms1.placeOrder(o2);
        assert o2.isCompleted();
        caught = false;
        try{
            this.oms2.placeOrder(o2);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        assert caught;
        Order o3 = new Order();
        o3.addToOrder(new Service(30.00,2,2000,"ELECTRICIAN"),1);
        caught = false;
        try{
            this.oms1.placeOrder(o3);
        }catch (IllegalStateException e){
            caught = true;
        }
        assert caught;
        Order o4 = new Order();
        assert this.wh1.getStockLevel(1000) == 2;
        assert this.wh1.getStockLevel(3000) == 0;
        assert this.wh1.getStockLevel(4000) == 5;
        assert this.wh1.getStockLevel(5000) == 9;
        o4.addToOrder(new Product("FAN",15.99,5000),1);
        this.oms1.placeOrder(o4);
        this.oms1.placeOrder(o4);
        caught = false;
        try{
            this.oms1.placeOrder(o2);
        }catch (IllegalStateException e){
            caught = true;
        }
        assert caught;
        this.oms1.placeOrder(o4);
        o2.setCompleted(false);
        this.oms1.placeOrder(o2);
        assert o2.isCompleted();
        Order o5 = new Order();
        o5.addToOrder(new Service(12.99,3,1000,"ART"),3);
        o5.addToOrder(new Service(15.75,1,2000,"BACKSCRATCH"),3);
        o5.addToOrder(new Service(3.29,1,3000,"CARDRIVER"),3);
        caught = false;
        try{
            this.oms3.placeOrder(o5);
        }catch (IllegalStateException e){
            caught = true;
        }
        assert caught;
		System.out.println("All Tests Passed!!!");
	}
	protected void fillOrder(Order order){
		order.addToOrder(new Product("ALLIGATOR",129.75,1000),8);
		order.addToOrder(new Product("CUP",3.29,3000),20);
		order.addToOrder(new Product("DOG",1230,4000),10);
		order.addToOrder(new Product("FAN",15.99,5000),1);
		order.addToOrder(new Service(12.99,3,1000,"ART"),2);
		order.addToOrder(new Service(30.00,2,2000,"ELECTRICIAN"),1);
	}
	protected Set<Product> getAProductCatalog1(){
		Set<Product> a1 = new HashSet<>();
		Product a = new Product("ALLIGATOR",129.75,1000);
		Product b = new Product("BOOK",9.99,2000);
		Product c = new Product("CUP",3.29,3000);
		Product d = new Product("DOG",1230,4000);
		Product e = new Product("E-BOOK",179.99,2000);
		Product f = new Product("FAN",15.99,5000);
		a1.add(a);
		a1.add(b);
		a1.add(c);
		a1.add(d);
		a1.add(e);
		a1.add(f);
		return a1;
	}
	protected void createProductsSets(){
		Product a = new Product("ALLIGATOR",129.75,1000);
		Product b = new Product("BOOK",9.99,2000);
		Product c = new Product("CUP",3.29,3000);
		Product d = new Product("DOG",1230,4000);
		Product e = new Product("E-BOOK",179.99,2000);
		Product f = new Product("FAN",15.99,5000);
		this.pSet1.add(a);
		this.pSet2.add(b);
		this.pSet1.add(c);
		this.pSet2.add(d);
		this.pSet1.add(e);
		this.pSet2.add(f);
		this.pSet1.add(f);
	}
	protected void createServiceProviderSets(){
		Service a = new Service(12.99,3,1000,"ART");
		Service b = new Service(15.75,1,2000,"BACKSCRATCH");
		Service c = new Service(3.29,1,3000,"CARDRIVER");
		Service d = new Service(23.01,10,4000,"DIGGER");
		Service e = new Service(30.00,2,2000,"ELECTRICIAN");
		Service f = new Service(12.25,25,5000,"FARMER");
		Set<Service> setA = new HashSet<>();
		setA.add(a);
		setA.add(b);
		setA.add(f);
		Set<Service> setB = new HashSet<>();
		setB.add(f);
		setB.add(d);
		setB.add(e);
		Set<Service> setC = new HashSet<>();
		setC.add(c);
		setC.add(a);
		setC.add(e);
		Set<Service> setD = new HashSet<>();
		setD.add(a);
		setD.add(b);
		setD.add(c);
		ServiceProvider spA = new ServiceProvider("ANDY",1000,setA);
		ServiceProvider spB = new ServiceProvider("BOB",2000,setB);
		ServiceProvider spC = new ServiceProvider("CARL",3000,setC);
		ServiceProvider spD = new ServiceProvider("IMPOSTER",1000,setC);
		ServiceProvider spE = new ServiceProvider("E",4000,setD);
		ServiceProvider spF = new ServiceProvider("F",5000,setD);
		ServiceProvider spG = new ServiceProvider("G",6000,setD);
		this.spSet1.add(spA);
		this.spSet1.add(spB);
		this.spSet1.add(spC);
		this.spSet1.add(spD);
		this.spSet2.add(spA);
		this.spSet2.add(spB);
		this.spSet2.add(spC);
		this.spSet3.add(spE);
		this.spSet3.add(spF);
		this.spSet3.add(spG);
	}
	protected void stockWarehouse(){
		Product a = new Product("ALLIGATOR",129.75,1000);
		Product b = new Product("BOOK",9.99,2000);
		Product c = new Product("CUP",3.29,3000);
		Product d = new Product("DOG",1230,4000);
		Product f = new Product("FAN",15.99,5000);
		this.wh1.addNewProductToWarehouse(a,5);
		this.wh1.addNewProductToWarehouse(b,9);//
		this.wh1.addNewProductToWarehouse(c,20);
		this.wh1.addNewProductToWarehouse(d,15);
	}
}
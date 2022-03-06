package edu.yu.cs.intro.orderManagement;
import java.util.*;
public class ServiceProviderTest{
	private List<ServiceProvider> sp;
	public static void main(String[] args) {
		ServiceProviderTest spt = new ServiceProviderTest();
		spt.runServiceProviderTest();
	}
	public ServiceProviderTest(){
		this.sp = new ArrayList<>();
	}
	protected List<ServiceProvider> getServiceProviderList(){
		return this.sp;
	}
	protected void runServiceProviderTest(){
		this.createServiceProviders();
		this.assignServiceProviders();
		this.checkEqualsAndHashCode();
		System.out.println("The name of spD is " + this.sp.get(3).getName() + " and his ID# is " + this.sp.get(3).getId() + ".");
		System.out.println("All Tests Passed!!!");
	}
	protected void checkEqualsAndHashCode(){
		assert !this.sp.get(0).equals(this.sp.get(1));
		assert !this.sp.get(1).equals(this.sp.get(2));
		assert !this.sp.get(0).equals(this.sp.get(2));
		assert this.sp.get(0).equals(this.sp.get(3));
		assert this.sp.get(0).equals(this.sp.get(0));
		assert !this.sp.get(0).equals(null);
		assert this.sp.get(0).hashCode() != this.sp.get(1).hashCode();
		assert this.sp.get(0).hashCode() != this.sp.get(2).hashCode();
		assert this.sp.get(1).hashCode() != this.sp.get(2).hashCode();
		assert this.sp.get(0).hashCode() == this.sp.get(3).hashCode();
		assert this.sp.get(0).hashCode() == this.sp.get(0).hashCode();
		assert this.sp.get(0).compareTo(this.sp.get(1)) != 0;
		assert this.sp.get(0).compareTo(this.sp.get(2)) != 0;
		assert this.sp.get(1).compareTo(this.sp.get(2)) != 0;
		assert this.sp.get(0).compareTo(this.sp.get(3)) == 0;
		assert this.sp.get(0).compareTo(this.sp.get(0)) == 0;
	}
	protected void assignServiceProviders(){
		this.sp.get(0).assignToCustomer();
		boolean caught = false;
        try{
            this.sp.get(0).assignToCustomer();
        }catch (IllegalStateException e){
            caught = true;
        }
        assert caught;
        this.sp.get(0).setNewCurrentOrder();
        this.sp.get(0).setNewCurrentOrder();
        this.sp.get(0).setNewCurrentOrder();
        caught = false;
        try{
            this.sp.get(0).assignToCustomer();
        }catch (IllegalStateException e){
            caught = true;
        }
        assert caught;
        this.sp.get(0).setNewCurrentOrder();
        this.sp.get(0).assignToCustomer();
        assert this.sp.get(0).getBusyStatus();
	}
	protected void createServiceProviders(){
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
		ServiceProvider spA = new ServiceProvider("ANDY",1000,setA);
		ServiceProvider spB = new ServiceProvider("BOB",2000,setB);
		ServiceProvider spC = new ServiceProvider("CARL",3000,setC);
		ServiceProvider spD = new ServiceProvider("IMPOSTER",1000,setC);
		this.sp.add(spA);
		this.sp.add(spB);
		this.sp.add(spC);
		this.sp.add(spD);
		boolean i = this.sp.get(0).addService(c);
		boolean j = this.sp.get(0).removeService(b);
		assert i && j;
		assert this.sp.get(0).getServices().contains(c);
		assert !this.sp.get(0).getServices().contains(b);
		Set<Service> copySetA = this.sp.get(0).getServices();
		assert copySetA.contains(a) && copySetA.contains(c) && copySetA.contains(f) && !copySetA.contains(b) && !copySetA.contains(d) && !copySetA.contains(e);
	}
}
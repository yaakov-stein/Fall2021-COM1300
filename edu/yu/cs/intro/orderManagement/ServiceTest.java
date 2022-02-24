package edu.yu.cs.intro.orderManagement;
import java.util.*;
public class ServiceTest{
	private List<Service> serviceList;
	public static void main(String[] args) {
		ServiceTest pt = new ServiceTest();
		pt.runServiceTest();
	}
	public ServiceTest(){
		this.serviceList = new ArrayList<>();
	}
	protected void runServiceTest(){
		this.createServices();
		for(Service a:serviceList){
			System.out.println("This Service is a " + a.getDescription() + ". " + "It costs " + a.getPrice() + ". " + "Its ID# is " + a.getItemNumber() + ".");
		}
		assert !this.serviceList.get(0).equals(this.serviceList.get(1));
		assert !this.serviceList.get(1).equals(this.serviceList.get(5));
		assert !this.serviceList.get(2).equals(this.serviceList.get(4));
		assert this.serviceList.get(1).equals(this.serviceList.get(4));
		assert this.serviceList.get(0).equals(this.serviceList.get(0));
		assert !this.serviceList.get(0).equals(null);
		assert this.serviceList.get(0).hashCode() != this.serviceList.get(1).hashCode();
		assert this.serviceList.get(1).hashCode() != this.serviceList.get(5).hashCode();
		assert this.serviceList.get(2).hashCode() != this.serviceList.get(4).hashCode();
		assert this.serviceList.get(1).hashCode() == this.serviceList.get(4).hashCode();
		assert this.serviceList.get(0).hashCode() == this.serviceList.get(0).hashCode();
		assert this.serviceList.get(0).getPrice() == 38.97;
		assert this.serviceList.get(2).getPrice() == 3.29;
		System.out.println(this.serviceList.get(3).getPrice());
		assert this.serviceList.get(0).getNumberOfHours() == 3;
		System.out.println("Tests Passed!!");
	}
	protected void createServices(){
		Service a = new Service(12.99,3,1000,"ART");
		Service b = new Service(15.75,1,2000,"BACKSCRATCH");
		Service c = new Service(3.29,1,3000,"CARDRIVER");
		Service d = new Service(23.01,10,4000,"DIGGER");
		Service e = new Service(30.00,2,2000,"ELECTRICIAN");
		Service f = new Service(12.25,25,5000,"FARMER");
		this.serviceList.add(a);
		this.serviceList.add(b);
		this.serviceList.add(c);
		this.serviceList.add(d);
		this.serviceList.add(e);
		this.serviceList.add(f);
	}
}
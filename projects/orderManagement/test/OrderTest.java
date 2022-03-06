package edu.yu.cs.intro.orderManagement;
import java.util.*;
public class OrderTest{
	private List<Item> itemList;
	private Order order;
	public static void main(String[] args) {
		OrderTest ot = new OrderTest();
		ot.runOrderTest();
	}
	public OrderTest(){
		this.itemList = new ArrayList<>();
		this.order = new Order();
	}
	protected Order getOrder(){
		return this.order;
	}
	protected void runOrderTest(){
		this.createItems();
		this.addItemsToOrder();
		this.getTotalPrices();
		assert !this.order.isCompleted();
		this.order.setCompleted(true);
		assert this.order.isCompleted();
		System.out.println("All Tests Passed!!!");
	}
	protected void getTotalPrices(){
		System.out.println(this.order.getProductsTotalPrice());//4033.98
		System.out.println(this.order.getServicesTotalPrice());//4707.03

	}
	protected void addItemsToOrder(){
		for(Item a:this.itemList){
			for(int i = 0;i < 3; i++){
				this.order.addToOrder(a,i);
			}
		}
		assert this.order.getQuantity(this.itemList.get(3)) == 3;
		assert this.order.getQuantity(this.itemList.get(0)) == 3;
		assert this.order.getQuantity(this.itemList.get(7)) == 3;
		assert this.order.getQuantity(this.itemList.get(9)) == 3;
		this.order.addToOrder(this.itemList.get(3), 9);
		assert this.order.getQuantity(this.itemList.get(3)) == 12;
		Item[] copy = this.order.getItems();
		Set<Item> copySet = new HashSet<>();
		for(Item a:copy){
			copySet.add(a);
		}
		assert this.itemList.removeAll(copySet);
		assert this.itemList.isEmpty();
		assert copySet.containsAll(this.itemList);
		this.itemList.addAll(copySet);

	}
	protected void createItems(){
		Service aS = new Service(12.99,3,1000,"ART");
		Service bS = new Service(15.75,1,2000,"BACKSCRATCH");
		Service cS = new Service(3.29,1,3000,"CARDRIVER");
		Service dS = new Service(23.01,10,4000,"DIGGER");
		Service eS = new Service(30.00,2,6000,"ELECTRICIAN");
		Service fS = new Service(12.25,25,5000,"FARMER");
		Product aP = new Product("ALLIGATOR",129.75,1000);
		Product bP = new Product("BOOK",9.99,2000);
		Product cP = new Product("CUP",3.29,3000);
		Product dP = new Product("DOG",1230,4000);
		Product eP = new Product("E-BOOK",179.99,6000);
		Product fP = new Product("FAN",15.99,5000);
		this.itemList.add(aS);
		this.itemList.add(bS);
		this.itemList.add(cS);
		this.itemList.add(dS);
		this.itemList.add(eS);
		this.itemList.add(fS);
		assert this.itemList.add(aP);
		this.itemList.add(bP);
		this.itemList.add(cP);
		this.itemList.add(dP);
		this.itemList.add(eP);
		this.itemList.add(fP);
	}
}
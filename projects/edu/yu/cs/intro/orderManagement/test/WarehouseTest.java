package edu.yu.cs.intro.orderManagement;
import java.util.*;
public class WarehouseTest{
	private Warehouse wh;
	private Warehouse finalWH;
	public static void main(String[] args) {
		WarehouseTest wht = new WarehouseTest();
		wht.runWarehouseTest();
	}
	public WarehouseTest(){
		this.wh = new Warehouse();
	}
	protected void runWarehouseTest(){
		this.stockWarehouse();
		this.addToDoNotRestockList();
		this.restockWarehouse();
		this.fulfillOrders();
		Set<Product> productSet = this.wh.getAllProductsInCatalog();
		for(Product a:productSet){
			System.out.println(a.getDescription());
		}
		System.out.println("All Tests Passed!!!");
	}
	protected void fulfillOrders(){
		this.wh.fulfill(3000,3);
		assert this.wh.getStockLevel(3000) == 0;
		boolean caught = false;
        try{
            this.wh.fulfill(2000,200);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        assert caught;
        this.wh.fulfill(4000,0);
        assert this.wh.getStockLevel(4000) == 20;
        caught = false;
        try{
            this.wh.fulfill(100,1);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        assert caught;
	}
	protected void restockWarehouse(){
		assert this.wh.getStockLevel(4000) == 5;
		this.wh.restock(4000,20);
		assert this.wh.getStockLevel(4000) == 20;
		assert this.wh.getStockLevel(3000) == 3;
		this.wh.restock(3000,2);
		assert this.wh.getStockLevel(3000) == 3;
		this.wh.restock(3000,3);
		assert this.wh.getStockLevel(3000) == 3;
		boolean caught = false;
        try{
            this.wh.restock(1000,2);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        assert caught;
        assert !this.wh.isRestockable(1000);
        assert this.wh.isRestockable(4000);
        assert this.wh.isInCatalog(1000);
        assert !this.wh.isInCatalog(1500);
        assert this.wh.getStockLevel(6000) == 0;
        this.wh.restock(6000,0);
        assert this.wh.getStockLevel(6000) == 0;
        int i = this.wh.setDefaultStockLevel(6000,23);
        assert i == 0;
        this.wh.restock(6000,1);
        assert this.wh.getStockLevel(6000) == 23;
	}
	protected void addToDoNotRestockList(){
		int a = this.wh.doNotRestock(1000);
		assert a == 10;
		int b = this.wh.doNotRestock(2000);
		assert b == 100;
		boolean caught = false;
        try{
            this.wh.doNotRestock(1500);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        assert caught;
	}
	protected void stockWarehouse(){
		this.wh.addNewProductToWarehouse(new Product("ALLIGATOR",129.75,1000),10);
		this.wh.addNewProductToWarehouse(new Product("BOOK",9.99,2000),100);
		this.wh.addNewProductToWarehouse(new Product("CUP",3.29,3000),3);
		this.wh.addNewProductToWarehouse(new Product("DOG",1230,4000),5);
		this.wh.addNewProductToWarehouse(new Product("GOB",1.99,6000),0);
		boolean caught = false;
        try{
            this.wh.addNewProductToWarehouse(new Product("E-BOOK",179.99,2000),0);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        assert caught;
		this.wh.addNewProductToWarehouse(new Product("FAN",15.99,5000),8);
        caught = false;
        try{
            this.wh.addNewProductToWarehouse(new Product("ALLIGATOR",129.75,1000),10);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        assert caught;
	}
}
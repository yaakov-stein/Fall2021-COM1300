package edu.yu.cs.intro.orderManagement;
import java.util.*;
public class ProductTest{
	private List<Product> productList;
	public static void main(String[] args) {
		ProductTest pt = new ProductTest();
		pt.runProductTest();
	}
	public ProductTest(){
		this.productList = new ArrayList<>();
	}
	protected void runProductTest(){
		this.createProducts();
		for(Product a:productList){
			System.out.println("This Product is a " + a.getDescription() + ". " + "It costs " + a.getPrice() + ". " + "Its ID# is " + a.getItemNumber() + ".");
		}
		assert !this.productList.get(0).equals(this.productList.get(1));
		assert !this.productList.get(1).equals(this.productList.get(5));
		assert !this.productList.get(2).equals(this.productList.get(4));
		assert this.productList.get(1).equals(this.productList.get(4));
		assert this.productList.get(0).equals(this.productList.get(0));
		assert !this.productList.get(0).equals(null);
		assert this.productList.get(0).hashCode() != this.productList.get(1).hashCode();
		assert this.productList.get(1).hashCode() != this.productList.get(5).hashCode();
		assert this.productList.get(2).hashCode() != this.productList.get(4).hashCode();
		assert this.productList.get(1).hashCode() == this.productList.get(4).hashCode();
		assert this.productList.get(0).hashCode() == this.productList.get(0).hashCode();
		System.out.println("Tests Passed!!");
	}
	protected void createProducts(){
		Product a = new Product("ALLIGATOR",129.75,1000);
		Product b = new Product("BOOK",9.99,2000);
		Product c = new Product("CUP",3.29,3000);
		Product d = new Product("DOG",1230,4000);
		Product e = new Product("E-BOOK",179.99,2000);
		Product f = new Product("FAN",15.99,5000);
		this.productList.add(a);
		this.productList.add(b);
		this.productList.add(c);
		this.productList.add(d);
		this.productList.add(e);
		this.productList.add(f);
	}
}
//package AuctionHouse;
import java.util.ArrayList;
import java.util.List;

public class Subject {
	
   private List<Observer> observers = new ArrayList<Observer>();
   private int amount;
   private String username;
   private String description;
   
   public String getDescription() {
	   return description;
   }
   
   public int getAmount() {
      return amount;
   }
   
   public String getUsername() {
	   return username;
   }

   public void setState(int amount,String username,String description) {
      this.amount = amount;
      this.username=username;
      this.description=description;
      notifyAllObservers();
   }

   public void attach(Observer observer){
      observers.add(observer);		
   }

   public void notifyAllObservers(){
      for (Observer observer : observers) {
         observer.update();
      }
   } 	
}

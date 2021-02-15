package taxtool.input;

public class CoinTotal {

   public final String name;
   private double total = 0;

   public CoinTotal(String name) {
      super();
      this.name = name;
   }

   public double getTotal() {
      return total;
   }

   public void buy(double amount) {
      total += amount;
   }

   public void sell(double amount) {
      total -= amount;
   }
}

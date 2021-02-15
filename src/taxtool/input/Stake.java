package taxtool.input;

import java.io.Serializable;

public class Stake implements Serializable {

   private static final long serialVersionUID = -7866992443464065838L;
   private final long time;
   private final String coin;
   private double amount;

   public Stake(long _time, String _coin, double _amount) {
      this.time = _time;
      this.coin = _coin;
      this.amount = _amount;
   }

   public long getTime() {
      return time;
   }

   public String getCoin() {
      return coin;
   }

   public double getAmount() {
      return amount;
   }

}

package taxtool.ui;

public class CostBasis {

   public final String coin;
   public final double usdCost;
   public final double coinAmt;

   public CostBasis(String coin, double usdCost, double coinAmt) {
      this.usdCost = usdCost;
      this.coin = coin;
      this.coinAmt = coinAmt;
   }
}

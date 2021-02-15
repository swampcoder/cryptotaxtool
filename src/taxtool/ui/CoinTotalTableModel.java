package taxtool.ui;

import taxtool.input.CoinTotal;
import taxtool.ui.table.DataTableModel;

public class CoinTotalTableModel extends DataTableModel<CoinTotal> {

   private final static String[] COLS = new String[] { "COIN", "AMOUNT" };
   private final static int COIN = 0;
   private final static int TOTAL = 1;

   public CoinTotalTableModel(CoinTotalTable table) {
      super(table, COLS);

   }

   @Override
   public Object getValueAt(CoinTotal ct, int r, int c) {

      if (c == COIN)
         return ct.name;
      else if (c == TOTAL)
         return ct.getTotal();
      return null;
   }

}

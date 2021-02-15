package taxtool.ui;

import taxtool.input.Trade;
import taxtool.ui.table.DataTableModel;

public class TradeTableModel extends DataTableModel<Trade> {

   final static int INDEX = 0;
   final static int DATE = 1;
   final static int IN_TYPE = 2;
   final static int IN_AMT = 3;
   final static int IN_HOLDING = 4;
   final static int RATE = 5;
   final static int OUT_TYPE = 6;
   final static int OUT_AMT = 7;
   final static int OUT_HOLDING = 8;
   final static int OUT_RATE = 9;
   final static int EXCHANGE = 10;
   final static int BTC_PRICE = 11;
   final static int GAIN_LOSS = 12;
   final static int TOTAL_GAIN_LOSS = 13;
   final static int NOTES = 14;

   private final static String[] COLS = new String[] { "ID", "DATE", "IN COIN", "IN AMT", "IN HOLDING", "IN RATE",
         "OUT TYPE", "OUT AMT", "OUT HOLDING", "OUT RATE", "EXCHANGE", "BTC PRICE", "GAIN/LOSS", "TOTAL GAIN/LOSS",
         "NOTES" };

   public TradeTableModel(TradeTable table) {
      super(table, COLS);

      this.setColumnClass(Integer.class, INDEX);
      this.setColumnClass(Long.class, DATE);
      setColumnClass(Double.class, IN_AMT);
   }

   @Override
   public boolean isCellEditable(int r, int c) {
      return c == NOTES;
   }

   @Override
   public Object getValueAt(Trade t, int r, int c) {

      // special block for crowdsale insertions
      if (t.isCrowdsale()) {
         if (c == DATE)
            return t.date.getTime();
         else if (c == IN_TYPE)
            return t.getCrowdsale();
         else if (c == IN_AMT)
            return "CROWDSALE";
         else
            return null;
      }

      if (c == INDEX)
         return t.getTradeIndex();
      else if (c == DATE) {
         if (t.date == null) {
            System.out.println("Null date for=" + t);
            return null;
         }
         return t.date.getTime();
      } else if (c == IN_TYPE)
         return t.buyCoin;
      else if (c == IN_AMT)
         return t.buyAmount;
      else if (c == RATE)
         return 0;
      else if (c == OUT_TYPE)
         return t.sellCoin;
      else if (c == OUT_HOLDING) {
         return t.getSellTotal();
      } else if (c == IN_HOLDING)
         return t.getBuyTotal();
      else if (c == OUT_AMT)
         return t.sellTotal;
      else if (c == OUT_RATE)
         return 0;
      else if (c == EXCHANGE)
         return t.exchange;
      else if (c == BTC_PRICE) {
         return t.getBtcPriceAtTimeOfTrade();
      } else if (c == GAIN_LOSS)
         return 0;
      else if (c == NOTES)
         return "";
      return null;
   }

}

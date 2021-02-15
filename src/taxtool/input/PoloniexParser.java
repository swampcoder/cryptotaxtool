package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PoloniexParser extends CsvParser {

   // Date,Market,Category,Type,Price,Amount,Total,Fee,Order Number,Base Total Less
   // Fee,Quote Total Less Fee
   private final static DateFormat PoloDateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      PoloDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   @Override
   public Trade parseLine(String tradeCsv) throws ParseException {

      String[] lineArgs = tradeCsv.split(",");
      String[] coins = lineArgs[1].split("/");
      if (!lineArgs[2].equals("Exchange"))
         throw new RuntimeException("category not handled: " + lineArgs[2]);

      Double quantity = Double.parseDouble(lineArgs[5]);
      Double price = Double.parseDouble(lineArgs[4]);

      Double commission = Double.parseDouble(lineArgs[5]);
      // double amountOut = quantity * price + comission;
      double total = Double.parseDouble(lineArgs[6]);
      Date tradeDate = PoloDateFormat.parse(lineArgs[0]);
      long tradeTime = tradeDate.getTime();
      if (lineArgs[3].equals("Buy")) {
         Trade trade = new Trade(tradeCsv, tradeTime, coins[1], total, coins[0], quantity, "poloniex");
         // System.out.println(trade);
         return trade;
      } else if (lineArgs[3].equals("Sell")) {
         Trade trade = new Trade(tradeCsv, tradeTime, coins[0], quantity, coins[1], total, "poloniex");
         // System.out.println(trade);
         return trade;
      } else {
         throw new IllegalArgumentException("Unknown order type=" + lineArgs[2]);
      }
   }

}

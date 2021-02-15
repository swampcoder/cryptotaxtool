package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

//Date,Market,Type,Price,Amount,Total,Fee,OrderId,TradeId,Change Base, Change Quote
public class LiquiParser extends CsvParser {

   private final static DateFormat LiquiDateFormat = new SimpleDateFormat("dd.M.yyyy hh:mm:ss");
   static {
      LiquiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   @Override
   public Trade parseLine(String tradeCsv) throws ParseException {

      String[] lineArgs = tradeCsv.split(",");
      String[] coins = lineArgs[1].split("/");

      Double quantity = Double.parseDouble(lineArgs[5]);
      Double price = Double.parseDouble(lineArgs[4]);
      Double comission = Double.parseDouble(lineArgs[5]);
      // double amountOut = quantity * price + comission;
      double total = Double.parseDouble(lineArgs[5]);
      Date tradeDate = LiquiDateFormat.parse(lineArgs[0]);
      long tradeTime = tradeDate.getTime();
      if (lineArgs[2].equals("BUY")) {
         Trade trade = new Trade(tradeCsv, tradeTime, coins[0], quantity, coins[1], total, "liqui");
         // System.out.println(trade);
         return trade;
      } else if (lineArgs[2].equals("SELL")) {
         Trade trade = new Trade(tradeCsv, tradeTime, coins[1], quantity, coins[0], total, "liqui");
         // System.out.println(trade);
         return trade;
      } else {
         throw new IllegalArgumentException("Unknown order type=" + lineArgs[2]);
      }
   }

}

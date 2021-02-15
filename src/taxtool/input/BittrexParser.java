package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class BittrexParser extends CsvParser {

   // 1/18/2017 2:33:08 PM
   private final static DateFormat BittrexDateFormat = new SimpleDateFormat("M/dd/yyyy hh:mm:ss a");
   static {
      BittrexDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   public BittrexParser() {

   }

   @Override
   public Trade parseLine(String tradeCsv) throws ParseException {
      String[] lineArgs = tradeCsv.split(",");
      String[] coins = lineArgs[1].split("-");
      Double quantity = Double.parseDouble(lineArgs[3]);
      Double price = Double.parseDouble(lineArgs[6]);
      Double commission = Double.parseDouble(lineArgs[5]);
      double amountOut = quantity * price + commission;
      Date tradeDate = BittrexDateFormat.parse(lineArgs[8]);
      long tradeTime = tradeDate.getTime();
      if (lineArgs[2].equals("LIMIT_BUY")) {
         Trade trade = new Trade(tradeCsv, tradeTime, coins[0], quantity, coins[1], amountOut, "bittrex");
         // System.out.println(trade);
         return trade;
      } else if (lineArgs[2].equals("LIMIT_SELL")) {
         Trade trade = new Trade(tradeCsv, tradeTime, coins[1], quantity, coins[0], amountOut, "bittrex");

         // System.out.println(trade);
         return trade;
      } else {
         throw new IllegalArgumentException("Unknown order type=" + lineArgs[2]);
      }
   }
}

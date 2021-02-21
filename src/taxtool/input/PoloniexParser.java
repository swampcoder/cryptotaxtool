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
   public CryptoRecord parseLine(String tradeCsv) throws ParseException {

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
      CryptoRecord record = new CryptoRecord(RecordType.Trade);
      record.setRawLine(tradeCsv);
      record.setTime(tradeTime);
      record.setExchange("poloniex");
      if (lineArgs[3].equals("Buy")) {
         record.setCoinOrCoinIn(coins[1]);
         record.setAmountOrAmountIn(total);
         record.setCoinOut(coins[0]);
         record.setAmountOut(quantity);
         return record;
      } else if (lineArgs[3].equals("Sell")) {
         record.setCoinOrCoinIn(coins[0]);
         record.setAmountOrAmountIn(quantity);
         record.setCoinOut(coins[1]);
         record.setAmountOut(total);
         return record;
      } else {
         throw new IllegalArgumentException("Unknown order type=" + lineArgs[2]);
      }
   }

}

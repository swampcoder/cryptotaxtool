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
   public CryptoRecord parseLine(String tradeCsv) throws ParseException {

      String[] lineArgs = tradeCsv.split(",");
      String[] coins = lineArgs[1].split("/");

      Double quantity = Double.parseDouble(lineArgs[5]);
      Double price = Double.parseDouble(lineArgs[4]);
      Double comission = Double.parseDouble(lineArgs[5]);
      // double amountOut = quantity * price + comission;
      double total = Double.parseDouble(lineArgs[5]);
      Date tradeDate = LiquiDateFormat.parse(lineArgs[0]);
      long tradeTime = tradeDate.getTime();
      CryptoRecord record = new CryptoRecord(RecordType.Trade);
      record.setRawLine(tradeCsv);
      record.setTime(tradeTime);
      record.setExchange("liqui");
      if (lineArgs[2].equals("BUY")) {
         record.setCoinOrCoinIn(coins[0]);
         record.setAmountOrAmountIn(quantity);
         record.setCoinOut(coins[1]);
         record.setAmountOut(total);
         return record;
      } else if (lineArgs[2].equals("SELL")) {
         record.setCoinOrCoinIn(coins[1]);
         record.setAmountOrAmountIn(quantity);
         record.setCoinOut(coins[0]);
         record.setAmountOut(total);
         return record;
      } else {
         throw new IllegalArgumentException("Unknown order type=" + lineArgs[2]);
      }
   }

}

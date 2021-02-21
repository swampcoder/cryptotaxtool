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
   public CryptoRecord parseLine(String tradeCsv) throws ParseException {
      String[] lineArgs = tradeCsv.split(",");
      String[] coins = lineArgs[1].split("-");
      Double quantity = Double.parseDouble(lineArgs[3]);
      Double price = Double.parseDouble(lineArgs[4]);
      Double commission = Double.parseDouble(lineArgs[5]);
      double amountOut = quantity * price + commission;
      Date tradeDate = BittrexDateFormat.parse(lineArgs[8]);
      long tradeTime = tradeDate.getTime();
      CryptoRecord record = new CryptoRecord(RecordType.Trade);
      record.setExchange("bittrex");  
      record.setTime(tradeTime);
      record.setRawLine(tradeCsv);
     
      String calcDebug = "computing quantity=" + quantity + "  price=" + price + "  comission=" + commission;
      record.setCalcNotes(calcDebug);
      
      if (lineArgs[2].equals("LIMIT_BUY")) {
         record.setAmountOrAmountIn(quantity);
         record.setCoinOrCoinIn(coins[0]);
         record.setAmountOut(amountOut);
         record.setCoinOut(coins[1]);
         return record;
      } else if (lineArgs[2].equals("LIMIT_SELL")) {
         record.setAmountOrAmountIn(quantity);
         record.setCoinOrCoinIn(coins[1]);
         record.setAmountOut(amountOut);
         record.setCoinOut(coins[0]);
         return record;
      } else {
         throw new IllegalArgumentException("Unknown order type=" + lineArgs[2]);
      }
   }
}

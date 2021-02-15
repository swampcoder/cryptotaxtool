package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CoinbaseParser extends CsvParser {

   private final static DateFormat CoinbaseParser = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      CoinbaseParser.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   // Date,Action,Symbol,Exchange,Volume,Price,Currency,Fee,FeeCurrency,Total,Cost/Proceeds,ExchangeId,Memo,SymbolBalance,CurrencyBalance,FeeBalance
   @Override
   public Trade parseLine(String tradeCsv) throws ParseException {

      String[] line = tradeCsv.split(",");
      Date date = CoinbaseParser.parse(line[0]);
      if (line[1].equals("BUY")) {
         String buyCoin = line[2];
         Double buyAmount = Double.parseDouble(line[4]);
         Double buyPrice = Double.parseDouble(line[5]);
         Double usdOut = Double.parseDouble(line[9]);
         usdOut = Math.abs(usdOut);
         Trade trade = new Trade(tradeCsv, date.getTime(), "USD", usdOut, buyCoin, buyAmount, "Coinbase");
         // System.out.println(trade);
         return trade;

      } else if (line[1].equals("SELL")) {
         String sellCoin = line[2];
         Double sellAmount = Double.parseDouble(line[4]);
         Double sellPrice = Double.parseDouble(line[5]);
         Double usdIn = Double.parseDouble(line[9]);
         usdIn = Math.abs(usdIn);
         Trade trade = new Trade(tradeCsv, date.getTime(), sellCoin, sellAmount, "USD", usdIn, "Coinbase");
         return trade;
      }
      return null;
   }

}

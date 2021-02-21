package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class BinanceParser extends CsvParser {

   // Date(UTC),Market,Type,Price,Amount,Total,Fee,Fee Coin
   // 2018-10-24 02:06:18,ASTBTC,SELL,0.00001426,263,0.00375038,0.00000375,BTC
   private final static DateFormat BinanceFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      BinanceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   @Override
   public CryptoRecord parseLine(String tradeCsv) throws ParseException {

      String[] line = tradeCsv.split(",");

      Date date = BinanceFormat.parse(line[0]);
      Double price = Double.parseDouble(line[3]);
      Double amount = Double.parseDouble(line[4]);
      Double total = Double.parseDouble(line[5]);
      String major;
      String minor;
      if (line[1].length() != 6) {
         String[] tryParse = tryparse(line[1]);

         if (tryParse == null) {
            throw new RuntimeException("UKNOWN: " + line[1]);
         } else {
            major = tryParse[0];
            minor = tryParse[1];
         }

      } else {

         major = line[1].substring(0, 3);
         minor = line[1].substring(3, 6);

      }
      CryptoRecord trade = new CryptoRecord(RecordType.Trade);
      trade.setTime(date.getTime());
      trade.setRawLine(tradeCsv);
      trade.setExchange("BINANCE");
      
      if(line[2].equalsIgnoreCase("SELL")) 
      {
         trade.setCoinOrCoinIn(minor);
         trade.setCoinOut(major);
         trade.setAmountOut(amount);
         trade.setAmountOrAmountIn(amount*price);
      }
      else if(line[2].equalsIgnoreCase("BUY")) 
      {
         trade.setCoinOrCoinIn(major);
         trade.setCoinOut(minor);
         trade.setAmountOut(price*amount);
         trade.setAmountOrAmountIn(amount);
      }

      return trade;
   }

   private static String[] tryparse(String line) {
      if (line.equals("AEETH"))
         return new String[] { "AE", "ETH" };
      if (line.equals("AEBTC"))
         return new String[] { "AE", "BTC" };
      if (line.equals("QTUMETH"))
         return new String[] { "QTUM", "ETH" };
      else
         return null;
   }

}

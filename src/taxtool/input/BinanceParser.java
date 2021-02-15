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
   public Trade parseLine(String tradeCsv) throws ParseException {

      String[] line = tradeCsv.split(",");

      Date date = BinanceFormat.parse(line[0]);
      Double price = Double.parseDouble(line[3]);
      Double amount = Double.parseDouble(line[4]);
      Double total = Double.parseDouble(line[5]);
      String buyCoin;
      String sellCoin;
      if (line[1].length() != 6) {
         String[] tryParse = tryparse(line[1]);

         if (tryParse == null) {
            throw new RuntimeException("UKNOWN: " + line[1]);
         } else {
            buyCoin = tryParse[0];
            sellCoin = tryParse[1];
         }

      } else {

         buyCoin = line[1].substring(0, 3);
         sellCoin = line[1].substring(3, 6);

      }
      return null;
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

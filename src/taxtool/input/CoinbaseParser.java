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
   public CryptoRecord parseLine(String tradeCsv) throws ParseException {

      String[] line = tradeCsv.split(",");
      CryptoRecord record = new CryptoRecord(RecordType.Trade);
      Date date = CoinbaseParser.parse(line[0]);
      record.setTime(date.getTime());
      record.setExchange("Coinbase");
      record.setRawLine(tradeCsv);
      
      if (line[1].equals("BUY")) {
         String buyCoin = line[2];
         Double buyAmount = Double.parseDouble(line[4]);
         Double buyPrice = Double.parseDouble(line[5]);
         record.setBtcPriceInUSD(buyPrice);
         Double usdOut = Double.parseDouble(line[9]);
         usdOut = Math.abs(usdOut);
         record.setAmountOut(usdOut);
         record.setAmountOrAmountIn(buyAmount);
         record.setCoinOut("USD");
         record.setCoinOrCoinIn(buyCoin);
         return record;

      } else if (line[1].equals("SELL")) {
         String sellCoin = line[2];
         Double sellAmount = Double.parseDouble(line[4]);
         Double sellPrice = Double.parseDouble(line[5]);
         record.setBtcPriceInUSD(sellPrice);
         Double usdIn = Double.parseDouble(line[9]);
         usdIn = Math.abs(usdIn);
         record.setAmountOrAmountIn(usdIn);
         record.setCoinOrCoinIn("USD");
         record.setCoinOut(sellCoin); // eth or btc 
         record.setAmountOut(sellAmount);
         return record;
      }
      return null;
   }

}

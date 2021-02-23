package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

// new format from recent report generation 
// Timestamp,Transaction Type,Asset,Quantity Transacted,USD Spot Price at Transaction,USD Subtotal,USD Total (inclusive of fees),USD Fees,Notes
public class CoinbaseParser2 extends CsvParser {

   public static Set<String> SEND_ADDRESSES = new HashSet<String>();
   private final static DateFormat CoinbaseParser = new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss");
   static {
      CoinbaseParser.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   // Date,Action,Symbol,Exchange,Volume,Price,Currency,Fee,FeeCurrency,Total,Cost/Proceeds,ExchangeId,Memo,SymbolBalance,CurrencyBalance,FeeBalance
   @Override
   public CryptoRecord parseLine(String tradeCsv) throws ParseException {

      String[] line = tradeCsv.split(",");
      CryptoRecord record = new CryptoRecord();
      Date date = CoinbaseParser.parse(line[0]);
      record.setTime(date.getTime());
      record.setExchange("Coinbase");
      record.setRawLine(tradeCsv);
      
      if(line[1].equalsIgnoreCase("Send")) 
      {
         record.setRecordType(RecordType.Send);
         record.setCoinOrCoinIn(line[2]);
         record.setAmountOrAmountIn(Double.parseDouble(line[3]));
         int indexOfTo = line[line.length-1].indexOf("to ");
         record.setToAddress(line[line.length-1].substring(indexOfTo));
         SEND_ADDRESSES.add(record.getToAddressCS());
         record.setSend();
         return record;
      }
      else if(line[1].equalsIgnoreCase("Receive")) 
      {
         record.setRecordType(RecordType.Receive);
         record.setCoinOrCoinIn(line[2]);
         record.setAmountOrAmountIn(Double.parseDouble(line[3]));
         int indexOfTo = line[line.length-1].indexOf("from ");
         record.setToAddress(line[line.length-1].substring(indexOfTo));
         System.out.println(record.getToAddress());
         record.setRcv();
      }
      else if (line[1].equalsIgnoreCase("BUY")) {
         String buyCoin = line[2];
         Double buyAmount = Double.parseDouble(line[3]);
         Double buyPrice = Double.parseDouble(line[4]);
         record.setBtcPriceInUSD(buyPrice);
         Double usdOut = Double.parseDouble(line[5]);
         usdOut = Math.abs(usdOut);
         record.setAmountOut(usdOut);
         record.setAmountOrAmountIn(buyAmount);
         record.setCoinOut("USD");
         record.setCoinOrCoinIn(buyCoin);
         record.setRecordType(RecordType.Trade);
         return record;

      } else if (line[1].equals("SELL")) {
         String sellCoin = line[2];
         Double sellAmount = Double.parseDouble(line[3]);
         Double sellPrice = Double.parseDouble(line[4]);
         record.setBtcPriceInUSD(sellPrice);
         Double usdIn = Double.parseDouble(line[5]);
         usdIn = Math.abs(usdIn);
         record.setAmountOrAmountIn(usdIn);
         record.setCoinOrCoinIn("USD");
         record.setCoinOut(sellCoin); // eth or btc 
         record.setAmountOut(sellAmount);
         record.setRecordType(RecordType.Trade);
         return record;
      }
      return null;
   }

}

package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CircleParser extends CsvParser {

   private final static DateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss");

   // Date format: Mon Sep 29 2014 23:54:20 GMT+0000
   // Date, Reference ID, Transaction Type, From Account, To Account, From Amount,
   // From Currency, To Amount, To Currency, Status",

   @Override
   public CryptoRecord parseLine(String tradeCsv) throws ParseException {

      String[] lineArgs = tradeCsv.split(",");
      for (int i = 1; i < lineArgs.length; i++) {
         lineArgs[i] = strip(lineArgs[i]).trim();
      }

      String timeStr = lineArgs[0];
      timeStr = timeStr.replaceAll(" GMT+0000", "").trim();

      long time = DATE_FORMAT.parse(timeStr).getTime();
      String refId = lineArgs[1];
      String type = lineArgs[2];
      String fromAcct = lineArgs[3];
      String toAcct = lineArgs[4];
      String fromAmount = lineArgs[5];
      String fromCurrency = lineArgs[6];
      String toAmount = lineArgs[7];
      String toCurrency = lineArgs[8];
      String status = lineArgs[9];

      // System.out.println("FROM=" + fromCurrency + " TO=" + toCurrency);
      if (fromCurrency.equalsIgnoreCase("usd") && toCurrency.equalsIgnoreCase("btc")) {
         Double usdAmnt = Double.parseDouble(fromAmount);
         Double btcAmt = Double.parseDouble(toAmount);

         CryptoRecord trade = new CryptoRecord(RecordType.Trade);
         trade.setTime(time);
         trade.setCoinOut("USD");
         trade.setCoinOrCoinIn("BTC");
         trade.setAmountOut(usdAmnt);
         trade.setAmountOrAmountIn(btcAmt);
         trade.setExchange("circle");
         trade.setRawLine(tradeCsv);
         return trade;
      }
      return null;
   }

   private static String strip(String input) {
      input = input.replaceAll("\"", "");
      if (input.startsWith("$")) {
         input = input.substring(1);
      }
      input = input.trim();
      int space = input.indexOf(" ");
      if (space != -1) {
         input = input.substring(0, space);
      }

      return input;
   }
}

package taxtool.input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//"Date","Block height","Transaction ID","Confirmations","Input Count","Output Count","Amount (BTC)","Amount Fiat (USD) at transaction timestamp",
// "BTC to USD rate at transaction timestamp","Fees (BTC)","Size (kB)","Input List (BTC)","Output List (BTC)"
public class BitcoinTxParser {

   private final static DateFormat TimeFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
   private final static int NUM_COLUMNS = 13;

   public static List<BitcoinTx> parseBitcoinCsv(File file) throws IOException, ParseException {
      List<BitcoinTx> txList = new ArrayList<BitcoinTx>();
      String str = new String(Files.readAllBytes(file.toPath()));
      List<String> strs = extractValues(str);
      for (int i = 1; i < NUM_COLUMNS && (i * NUM_COLUMNS) < strs.size(); i++) {
         int base = i * NUM_COLUMNS;
         String dateStr = strs.get(base + 0);
         int lastSpace = dateStr.lastIndexOf(" ");
         dateStr = dateStr.substring(0, lastSpace).trim();
         Date date = TimeFormat.parse(dateStr);

         String txId = strs.get(base + 2);
         String amountStr = strs.get(base + 6);
         String amountFiatAtTxTime = strs.get(base + 7);
         String txFees = strs.get(base + 9);

         BitcoinTx tx = new BitcoinTx();
         tx.setTime(date.getTime());
         tx.setFromAddress(file.getName().replaceAll(".csv", ""));
         tx.setAmount(Double.parseDouble(amountStr));
         tx.setTxHash(txId);
         txList.add(tx);

         tx.setRecordSource(file, concatFromBase(strs, base));

      }

      return txList;
   }

   private static String concatFromBase(List<String> strs, int baseIdx) {
      String str = "";
      for (int i = 0; i < NUM_COLUMNS; i++) {
         str += strs.get(baseIdx + i);
      }
      return str;
   }

   private static List<String> extractValues(String line) {
      List<String> list = new ArrayList<String>();
      Pattern p = Pattern.compile("\"([^\"]*)\"");
      Matcher m = p.matcher(line);
      int i = 0;
      while (m.find()) {
         list.add(m.group(1));
      }
      return list;
   }

}

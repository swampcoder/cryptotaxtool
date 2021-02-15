package taxtool.input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Id,Amount,Currency,Confirmations,LastUpdated,TxId,CryptoAddress
public class BittrexDepositParser {

   private final static DateFormat TimeFormat = new SimpleDateFormat("M/dd/yyyy hh:mm:ss");
   static {
      TimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   public static List<Deposit> createDeposits(File file) throws IOException, ParseException {
      List<String> lines = Files.readAllLines(file.toPath());
      List<Deposit> deposits = new ArrayList<Deposit>();

      for (int i = 1; i < lines.size(); i++) {
         String line = lines.get(i);
         Deposit tx = parseTx(line);
         if (tx != null) {
            deposits.add(tx);
            System.out.println(tx);
         }
      }

      return deposits;
   }

   //// Id,Amount,Currency,Confirmations,LastUpdated,TxId,CryptoAddress
   private static Deposit parseTx(String line) throws ParseException, IOException {
      String[] csvs = line.split(",");
      Deposit tx = new Deposit("BITTREX");
      tx.setExchangeId(Integer.parseInt(csvs[0]));
      tx.setAmount(Double.parseDouble(csvs[1]));
      tx.setCoin(csvs[2]);
      Date date = TimeFormat.parse(csvs[4]);
      tx.setTime(date.getTime());
      tx.setTxId(csvs[5]);
      tx.setAddress(csvs[6]);

      return tx;
   }

   private static String textInQuotes(String line, String input) throws IOException {
      Pattern p = Pattern.compile("\"([^\"]*)\"");
      Matcher m = p.matcher(input);
      while (m.find()) {
         return m.group(1);
      }
      throw new IOException("bad input for=" + line);
   }

}

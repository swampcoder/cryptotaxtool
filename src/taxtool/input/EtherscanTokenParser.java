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
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// "Txhash","UnixTimestamp","DateTime","From","To","Value","ContractAddress","TokenName","TokenSymbol"
public class EtherscanTokenParser {

   private final static DateFormat TimeFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      TimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   public static List<EthereumTx> createEthTx(File file) throws IOException, ParseException {
      List<String> lines = Files.readAllLines(file.toPath());
      List<EthereumTx> ethTxs = new ArrayList<EthereumTx>();

      for (int i = 1; i < lines.size(); i++) {
         String line = lines.get(i);
         EthereumTx tx = parseTx(line);

         if (tx != null) {
            tx.setRecordSource(file, line);
            ethTxs.add(tx);
         }
      }

      return ethTxs;
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

   private static EthereumTx parseTx(String line) throws ParseException, IOException {
      List<String> csvs = extractValues(line);

      EthereumTx tx = new EthereumTx();

      tx.setTxHash(csvs.get(0));
      Date date = TimeFormat.parse(csvs.get(2));
      tx.setTime(date.getTime());
      tx.setFrom(csvs.get(3));
      tx.setTo(csvs.get(4));
      String valueStr = csvs.get(5).replaceAll(",", "");
      tx.setTokenValue(Double.parseDouble(valueStr));
      tx.setContract(csvs.get(6));
      tx.setTokenName(csvs.get(7));
      tx.setToken(csvs.get(8));

      if (tx.isValid() == false)
         return null;
      return tx;
   }

}

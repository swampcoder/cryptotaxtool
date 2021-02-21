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

//"Txhash","Blockno","UnixTimestamp","DateTime","From","To","ContractAddress",
//"Value_IN(ETH)","Value_OUT(ETH)","CurrentValue @ $1836.92/Eth","TxnFee(ETH)","TxnFee(USD)","Historical $Price/Eth","Status","ErrCode"
public class EtherscanParser {

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
            ethTxs.add(tx);
         }
      }

      return ethTxs;
   }

   private static EthereumTx parseTx(String line) throws ParseException, IOException {
      String[] csvs = line.split(",");
      for (int i = 0; i < csvs.length; i++) {
         String s = csvs[i];
         s = textInQuotes(line, s);
         if (s == null)
            throw new IOException("Bad line=" + line);
         csvs[i] = s;
      }
      EthereumTx tx = new EthereumTx();

      tx.setTxHash(csvs[0]);
      Date date = TimeFormat.parse(csvs[3]);
      tx.setTime(date.getTime());
      tx.setFrom(csvs[4]);
      tx.setTo(csvs[5]);
      tx.setContract(csvs[6]);
      tx.setValueInEth(Double.parseDouble(csvs[7]));
      tx.setValueOutEth(Double.parseDouble(csvs[8]));
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

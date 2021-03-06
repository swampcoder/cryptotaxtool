package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PoloniexWithdrawalParser extends WithdrawalParser {

   /*
    * Date,Currency,Amount,Address,Status
    */
   private final static DateFormat PoloDateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      PoloDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   public PoloniexWithdrawalParser() {
   }

   @Override
   public CryptoRecord parseCsv(String line) throws ParseException {

      String[] csv = line.split(",");
      Date time = PoloDateFormat.parse(csv[0]);

      CryptoRecord wd = new CryptoRecord(RecordType.Withdraw);
      wd.setTime(time.getTime());
      wd.setCoinOrCoinIn(csv[1]);
      wd.setAmountOrAmountIn(Double.parseDouble(csv[2]));
      wd.setToAddress(csv[3]);
      wd.setExchange("poloniex");
      if (csv[4].contains("COMPLETE")) {
         return wd;
      } else {
         System.out.println("IGNORING INCOMPLETE WITHDRAWAL=" + line);
      }

      return null;
   }

}

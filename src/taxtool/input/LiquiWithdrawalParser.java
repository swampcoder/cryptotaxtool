package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LiquiWithdrawalParser extends WithdrawalParser {

   private final static DateFormat LiquiDateFormat = new SimpleDateFormat("dd.M.yyyy hh:mm:ss");
   static {
      LiquiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   // Id,Time,Symbol,Name,Amount,Status,Address,TxHash
   @Override
   public Withdrawal parseCsv(String line) throws ParseException {

      String[] split = line.split(",");
      Date date = LiquiDateFormat.parse(split[1]);
      String coin = split[2];
      String coinName = split[3];
      Double amount = Double.parseDouble(split[4]);
      String status = split[5];
      if (status.equals("Canceled"))
         return null;
      String address = split[6];
      String txHash = split[7];

      Withdrawal wd = new Withdrawal();
      wd.setId(split[0]);
      wd.setAddress(address);
      wd.setCoin(coin);
      wd.setAmount(amount);
      wd.setTxHash(txHash);
      wd.setTime(date.getTime());
      wd.setExchange("liqui");
      return wd;
   }

}

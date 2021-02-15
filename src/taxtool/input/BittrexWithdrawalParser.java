package taxtool.input;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

// PaymentUuid,Currency,Amount,Address,Opened,Authorized,PendingPayment,TxCost,TxId,Canceled,InvalidAddress
public class BittrexWithdrawalParser extends WithdrawalParser {

   private final static DateFormat BittrexDateFormat = new SimpleDateFormat("M/dd/yyyy hh:mm:ss a");
   static {
      BittrexDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   @Override
   public Withdrawal parseCsv(String line) throws ParseException {

      Withdrawal wd = new Withdrawal();
      String[] csvs = line.split(",");

      String uuid = csvs[0];
      String coin = csvs[1];
      Double amount = Double.parseDouble(csvs[2]);
      String addr = csvs[3];
      String dateStr = csvs[4];
      Boolean opened = Boolean.parseBoolean(csvs[5]);
      Boolean auth = Boolean.parseBoolean(csvs[6]);
      // Double pendingPayment = Double.parseDouble(csvs[7]);
      Double txCost = Double.parseDouble(csvs[7]);
      String txHash = csvs[8];
      Boolean cancelled = Boolean.parseBoolean(csvs[9]);

      Boolean invalidAddr = Boolean.parseBoolean(csvs[10]);

      if (cancelled || invalidAddr)
         return null;

      wd.setCoin(coin);
      wd.setAddress(addr);
      wd.setAmount(amount);
      wd.setTime(BittrexDateFormat.parse(dateStr).getTime());
      wd.setTxHash(txHash);

      return wd;
   }

}

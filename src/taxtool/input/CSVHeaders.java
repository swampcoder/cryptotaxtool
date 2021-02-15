package taxtool.input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVHeaders {

   private final static Map<String, List<String>> TRADE_CSV_HEADERS = new HashMap<String, List<String>>();
   private final static Map<String, List<String>> WD_CSV_HEADERS = new HashMap<String, List<String>>();

   static {

      inputWdHeader("Id,Time,Symbol,Name,Amount,Status,Address,TxHash", "liqui");

      inptuTradeHeader(
            "Date,Action,Symbol,Exchange,Volume,Price,Currency,Fee,"
                  + "FeeCurrency,Total,Cost/Proceeds,ExchangeId,Memo,SymbolBalance,CurrencyBalance,FeeBalance",
            "coinbase");

      inptuTradeHeader(
            "Date,Market,Category,Type,Price,Amount,Total,Fee,Order Number,Base Total Less Fee,Quote Total Less Fee",
            "poloniex");

      inptuTradeHeader("Date,Market,Type,Price,Amount,Total,Fee,OrderId,TradeId,Change Base, Change Quote", "liqui");

      inptuTradeHeader("OrderUuid,Exchange,Type,Quantity,Limit,CommissionPaid,Price,Opened,Closed", "bittrex");

      inptuTradeHeader("Date(UTC),Market,Type,Price,Amount,Total,Fee,Fee Coin", "binance");

      inptuTradeHeader("Date, Reference ID, Transaction Type, From Account, To Account, "
            + "From Amount, From Currency, To Amount, To Currency, Status", "circle");

   }

   public static List<String> getWithdrawalHeaders(String exchange) {
      return WD_CSV_HEADERS.get(exchange.toUpperCase());
   }

   public static List<String> getTradeHeaders(String exchange) {
      return TRADE_CSV_HEADERS.get(exchange.toUpperCase());
   }

   private static void inptuTradeHeader(String csv, String exchange) {
      String[] values = csv.split(",");
      TRADE_CSV_HEADERS.put(exchange.toUpperCase(), Arrays.asList(values));
   }

   private static void inputWdHeader(String csv, String exchange) {
      String[] values = csv.split(",");
      WD_CSV_HEADERS.put(exchange.toUpperCase(), Arrays.asList(values));
   }
}

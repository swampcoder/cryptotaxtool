package taxtool.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ctc.calculator.IPriceInterface;
import ctc.enums.Currency;

public class HistoricalPriceTable implements IPriceInterface {

   private final static DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

   static {
      df.setMaximumFractionDigits(340);
   }

   public final static Path BTC_PRICES = (new File("price_data/coin_to_btc.dat")).toPath();
   public final static Path ETH_PRICES = (new File("price_data/coin_to_eth.dat")).toPath();

   private static Map<String, HistoricalPriceTable> tables = new HashMap<String, HistoricalPriceTable>();

   public static void initTables() throws IOException {
      tables.put("BTC", new HistoricalPriceTable(BTC_PRICES.toFile(), "BTC"));
      tables.put("ETH", new HistoricalPriceTable(ETH_PRICES.toFile(), "ETH"));
   }

   public static HistoricalPriceTable get(String priceCoin) {
      return tables.get(priceCoin);
   }

   private final File file;
   private final String priceCoin;
   private final Map<String, Double> priceInMap = new HashMap<String, Double>();

   public HistoricalPriceTable(File file, String priceCoin) throws IOException {
      this.file = file;
      this.priceCoin = priceCoin;
      file.createNewFile();
      initTable();
   }

   public void initTable() throws IOException {
      List<String> lines = Files.readAllLines(file.toPath());
      for (String line : lines) {
         line = line.trim();
         String[] csv = line.split(",");
         String key = csv[0] + "," + csv[1];

         try {
            if (csv[2].contains("E"))
               continue;
            Double v = Double.parseDouble(csv[2]);
            priceInMap.put(key, v);
         } catch (Exception e) {
            // ignore for now
         }
      }
   }

   public void requestTrade(CryptoRecord trade) throws IOException {
      makeRequest(trade.getCoinOut(), trade.getTime());
      makeRequest(trade.getCoinOrCoinIn(), trade.getTime());
      makeRequest("USD", trade.getTime()); // need btc/usd for all trade times

   }

   private void makeRequest(String coin, long time) throws IOException {
      // is this pivx???
      if (coin.equals("DNET"))
         return;
      if (coin.equals("STR"))
         return;

      if (coin.equalsIgnoreCase(priceCoin))
         return;
      String sellKey = coin + "," + time;
      if (priceInMap.containsKey(sellKey))
         return;

      if (priceInMap.containsKey(sellKey)) {
         System.out.println("Ignoring key=" + sellKey);
         return;
      }
      if (priceInMap.containsKey(sellKey))
         return;
      Double value = findPrice(coin, time, true);

      try {
         Thread.sleep(150);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public Double findPrice(String coin, Long time, boolean queryIfNotFound) throws IOException {
      if (coin == null)
         return null;
      String key = coin.trim() + "," + time.toString();
      Double value = priceInMap.get(key);
      if (value == null && queryIfNotFound) {
         value = queryPrice(coin, time);
         if (value == null)
            return null;
         String csv = key + "," + df.format(value) + "\n";
         Files.write(file.toPath(), csv.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
         priceInMap.put(key, value);
         System.out.println("wrote=" + csv + "     count=" + priceInMap.size());
      }
      return value;
   }

   private Double queryPrice(String coin, Long time) throws IOException {
      if (coin.equalsIgnoreCase(priceCoin))
         return null;

      String major = coin;
      String minor = priceCoin;
      String price = null;
      long timestamp = time / 1000;
      String url = "https://min-api.cryptocompare.com/data/histohour?fsym=" + major + "&tsym=" + minor
            + "&limit=1&toTs=" + timestamp;
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      int responseCode = con.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {

         String line;
         BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
         while ((line = in.readLine()) != null) {

            try {
               JSONObject parser = new JSONObject(line);
               System.out.println(parser.toString()); // Output json
               price = parser.getJSONArray("Data").getJSONObject(0).get("close").toString();
               System.out.println(time + ": " + major + "/" + minor + "     " + price);
               return Double.parseDouble(price);
            } catch (JSONException e) {
               System.out.println("Request failed for coin=" + coin);
               return null;
            }
         }
         in.close();
      }

      return 0d;
   }

   @Override
   public double getPriceInUSD(Currency currency, long time) {
      // TODO Auto-generated method stub
      return 0;
   }

}

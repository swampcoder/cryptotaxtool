package taxtool.input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ctc.calculator.IPriceInterface;
import ctc.enums.Currency;

public class USDTable implements IPriceInterface {

   private final static DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

   static {
      df.setMaximumFractionDigits(340);
   }

   public final static Path USD_FILE = (new File("price_data/coin_to_USD.dat")).toPath();

   private final Map<String, Double> priceInMap = new HashMap<String, Double>();

   public USDTable( ) throws IOException {

      USD_FILE.toFile().createNewFile();
      initTable();
   }
   
   public double getPriceInUSD(Currency currency, long timeOf) 
   {
      String key = currency.name() + "," + timeOf;
      return priceInMap.get(key);
   }

   public void initTable() throws IOException {
      List<String> lines = Files.readAllLines(USD_FILE.toFile().toPath());
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

   public Double findPrice(String coin, Long time, boolean queryIfNotFound) throws IOException {
      if (coin == null)
         return null;
      else if(coin.equalsIgnoreCase("usd")) return null;
      
      String key = coin.trim() + "," + time.toString();
      Double value = priceInMap.get(key);
      if (value == null && queryIfNotFound) {
         value = Utils.queryPriceInUSD(coin, time);
         Utils.sleep(150); // sleep to prevent rate limit 
         if (value == null)
            return null;
         String csv = key + "," + df.format(value) + "\n";
         Files.write(USD_FILE.toFile().toPath(), csv.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
         priceInMap.put(key, value);
         System.out.println("wrote=" + csv + "     count=" + priceInMap.size());
      }
      return value;
   }

}

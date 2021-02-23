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
   
   @Override
   public Double getPrice(Currency major, Currency minor, long timeOf, boolean queryIfNotFound) 
   {
      String key = major.name() + "," + minor.name() + "," + timeOf;
      Double price =  priceInMap.get(key);
      //System.out.println("KEY=" + key + "  PRICE=" + price + "   mapSize=" + this.priceInMap.size());
      if(price == null && queryIfNotFound) 
      {
         try {
            price = findPrice(major.name(), minor.name(), timeOf, true);
            
         } catch (IOException e) {
            e.printStackTrace();
            price = -1d;
         }
      }
      
      if(price == null) {
         priceInMap.put(key, -1d);
         return -1d;
      }
      
      return price;
   }

   public void initTable() throws IOException {
      List<String> lines = Files.readAllLines(USD_FILE.toFile().toPath());
      /*
      for(String line : lines) 
      {
         String[] parse = line.split(",");
         String newLine = parse[0] +",USD,"+parse[1]+"," +parse[2]+"\n";
         Files.write(USD_FILE2, newLine.getBytes(), StandardOpenOption.APPEND,
               StandardOpenOption.SYNC);
      }*/
      for (String line : lines) {
         line = line.trim();
         String[] csv = line.split(",");
         String key = csv[0] + "," + csv[1] + "," + csv[2];

         try {
            if (csv[3].contains("E"))
               continue;
            Double v = Double.parseDouble(csv[3]);
            if(v.doubleValue() == 0d) {
               System.out.println("Ignoring 0 USD price for=" + line);
               continue;
            }
            priceInMap.put(key, v);
         } catch (Exception e) {
            // ignore for now
            e.printStackTrace();
         }
      }
   }

   private Double findPrice(String major, String minor, Long time, boolean queryIfNotFound) throws IOException {
      if (major == null || minor == null)
         return null;

      String key = major.trim() + "," + minor.trim() + "," + time.toString();
      Double value = priceInMap.get(key);
      if (value == null && queryIfNotFound) {
         value = Utils.queryPrice(major, minor, time);
         System.out.println("RETRY Major=" + major + " Minor=" + minor + "  is value=" + value);
         Utils.sleep(150); // sleep to prevent rate limit 
         if (value == null || value.doubleValue() == 0d)
         {
            
            // try to find in terms of btc then btc to USD
            Double btcPrice = Utils.queryPriceInBTC(major,time);
            Double btcUsdPrice = Utils.queryPriceInUSD("BTC", time);
            if(btcPrice != null && btcUsdPrice != null) 
            {
               System.out.println("Division/Calc COIN=" + major + "   BTC price=" + btcPrice + "   BTC/USD=" + btcUsdPrice);
            }
            return null;
         }
         
         String csv = key + "," + df.format(value) + "\n";
        
         Files.write(USD_FILE.toFile().toPath(), csv.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
         priceInMap.put(key, value);
         System.out.println("wrote=" + csv + "     count=" + priceInMap.size());
      }
      return value;
   }

}

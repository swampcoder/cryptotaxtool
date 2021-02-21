package taxtool.input;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

   public static Object fromString(String s) throws IOException, ClassNotFoundException {
      byte[] data = Base64.getDecoder().decode(s);
      ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
      Object o = ois.readObject();
      ois.close();
      return o;
   }

   public static String toString(Serializable o) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(o);
      oos.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
   }
   
   public static boolean compare(Object o1, Object o2) 
   {
      if(o1 == o2) return true;
      else if(o1 == null && o2 == null) return true;
      else if(o1 != null) return o1.equals(o2);
      else return o2.equals(o1);
   }

   public static void notNull(Object...objs) 
   {
      int i =0;
      for(Object o : objs) 
      {
         if(o == null) throw new IllegalArgumentException("Index " + i + " is null");
         i++;
      }
   }
   
   public static void isNull(Object...objs) 
   {
      int i =0;
      for(Object o : objs) 
      {
         if(o != null) throw new IllegalArgumentException("Index " + i + " is not null");
         i++;
      }
   }
   
   public static void sleep(int ms) {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
   
   public static Double queryPriceInUSD(String coin, long time) throws IOException 
   {
      return queryPrice(coin, "USD", time);
   }
   
   public static Double queryPrice(String major, String minor, Long time) throws IOException {

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
               //System.out.println(parser.toString()); // Output json
               String priceStr = parser.getJSONArray("Data").getJSONObject(0).get("close").toString();
               System.out.println("Query result: " + major + "/" + minor + "   @  " + priceStr);
               if(priceStr == null) return null;
               Double price =  Double.parseDouble(priceStr);
               return price;
            } catch (JSONException e) {
               System.out.println("Request failed for coin pair=" + major + "/" + minor);
               return null;
            }
         }
         in.close();
      }

      return 0d;
   }
   
   private Utils() {}
}

package taxtool.input;

import java.io.Serializable;

public class Address implements Serializable {

   private static final long serialVersionUID = 41684243248567553L;
   private final String coin;
   private final String address;
   private final String originalAddress;
   private String source = null;

   public Address(String _coin, String _address) {
      this.coin = _coin;
      this.originalAddress = _address;
      this.address = _address.toUpperCase();

   }

   public String getCoin() {
      return coin;
   }

   public String getCaseSensitiveAddress() {
      return originalAddress;
   }

   public String getAddress() {
      return address;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public String getSource() {
      return source;
   }

   @Override
   public int hashCode() {
      return 31 * coin.hashCode() + 37 * address.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof Address) {
         Address a = (Address) o;
         if (a.coin.equalsIgnoreCase(coin) && a.address.equalsIgnoreCase(address)) {
            return true;
         }
      }
      return false;
   }

}

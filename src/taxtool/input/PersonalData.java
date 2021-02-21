package taxtool.input;

public class PersonalData {

   private static IRecordLoader recordLoader = null;
   private static IAddressResolver addrResolver = null;
   public static IRecordLoader getRecordLoader() {
      return recordLoader;
   }
   public static void setRecordLoader(IRecordLoader _recordLoader) {
      Utils.isNull(recordLoader);
      PersonalData.recordLoader = _recordLoader;
   }
   public static IAddressResolver getAddrResolver() {
      return addrResolver;
   }
   public static void setAddrResolver(IAddressResolver _addrResolver) {
      Utils.isNull(addrResolver);
      PersonalData.addrResolver = _addrResolver;
   }
   
   private PersonalData() {}
   
}

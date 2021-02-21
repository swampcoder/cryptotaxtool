package taxtool.input;

import java.util.UUID;

// TODO
public class RecordObfuscater {

   private static boolean enabled = false;
   
   
   public static double obfuscateAmt(double amt) 
   {
      
      
      return amt;
   }
   
   
   public static String obfuscateCoin(String addr) 
   {
      
      if(enabled) return UUID.randomUUID().toString();
      
      return addr;
   }
   
   private RecordObfuscater() {}
}

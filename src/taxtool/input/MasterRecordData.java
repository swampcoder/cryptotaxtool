package taxtool.input;

import java.awt.Color;

public class MasterRecordData {

   public final String txt;
   public final Color clr;

   public MasterRecordData(String txt) {
      this.txt = txt;
      clr = null;
   }

   public MasterRecordData(String txt, Color clr) {
      this.txt = txt;
      this.clr = clr;
   }
}

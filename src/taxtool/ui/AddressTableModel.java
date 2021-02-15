package taxtool.ui;

import taxtool.input.Address;
import taxtool.ui.table.DataTableModel;

public class AddressTableModel extends DataTableModel<Address> {

   public final static int COIN = 0;
   public final static int ADDRESS = 1;
   public final static int SOURCE = 2;
   public final static int NOTES = 3;
   private final static String[] COLS = new String[] { "COIN", "ADDRESS", "SOURCE", "NOTES" };

   public AddressTableModel(AddressTable table) {
      super(table, COLS);
   }

   @Override
   public Object getValueAt(Address addr, int r, int c) {

      if (c == COIN)
         return addr.getCoin();
      else if (c == ADDRESS)
         return addr.getCaseSensitiveAddress();
      else if (c == SOURCE)
         return addr.getSource();
      else if (c == NOTES)
         return null;
      return null;
   }

}

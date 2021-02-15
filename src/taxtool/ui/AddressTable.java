package taxtool.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JTable;

import taxtool.input.Address;
import taxtool.input.DataRecord;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;

public class AddressTable extends DataTable<Address> {

   public AddressTable(DataRecord record) {
      super();

      this.setAutoCreateRowSorter(true);
      for (String coin : record.getAddressedCoins()) {
         Set<Address> addrSet = record.getAddresses(coin);
         for (Address address : addrSet) {
            getModel().addRow(address);
         }
      }
   }

   @Override
   protected DataTableModel<Address> createModel() {
      return new AddressTableModel(this);
   }

   @Override
   protected void updateRendering(Address addr, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {

   }

   @Override
   protected void handleDoubleClick(MouseEvent me, int row, int col, Address rowObj) {
      if (col == AddressTableModel.ADDRESS) {
         StringSelection selection = new StringSelection(rowObj.getCaseSensitiveAddress());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(selection, selection);
      }
   }

}

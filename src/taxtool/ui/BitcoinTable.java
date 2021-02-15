package taxtool.ui;

import java.awt.Component;

import javax.swing.JTable;

import taxtool.input.BitcoinTx;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;

public class BitcoinTable extends DataTable<BitcoinTx> {

   @Override
   protected DataTableModel<BitcoinTx> createModel() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected void updateRendering(BitcoinTx rowObj, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {
      // TODO Auto-generated method stub

   }

}

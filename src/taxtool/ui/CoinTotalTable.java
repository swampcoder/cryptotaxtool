package taxtool.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;

import taxtool.input.CoinTotal;
import taxtool.input.DataRecord;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;

public class CoinTotalTable extends DataTable<CoinTotal> {

   private final static DecimalFormat MAX_PREC = new DecimalFormat("#.###");
   private final DataRecord record;

   public CoinTotalTable(DataRecord record) {
      this.record = record;
      this.setAutoCreateRowSorter(true);

      for (CoinTotal coin : record.getCoins()) {
         getModel().addOrUpdate(coin);
      }
   }

   @Override
   protected DataTableModel<CoinTotal> createModel() {

      return new CoinTotalTableModel(this);
   }

   @Override
   protected void updateRendering(CoinTotal ct, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {
      if (column == 1) {
         Double count = (Double) value;
         if (count < 0) {
            c.setForeground(Color.RED);
         } else {
            c.setForeground(Color.green.brighter());
         }
         JLabel l = (JLabel) c;
         l.setText(MAX_PREC.format(count));
      }

   }

}

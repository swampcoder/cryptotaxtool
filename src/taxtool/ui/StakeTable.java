package taxtool.ui;

import java.awt.Component;

import javax.swing.JTable;

import taxtool.input.Stake;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;

public class StakeTable extends DataTable<Stake> {

   @Override
   protected DataTableModel<Stake> createModel() {

      return null;
   }

   @Override
   protected void updateRendering(Stake stake, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {

   }

}

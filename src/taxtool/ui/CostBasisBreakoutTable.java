package taxtool.ui;

import java.awt.Component;

import javax.swing.JTable;

import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;

public class CostBasisBreakoutTable extends DataTable<CostBasis> {

   public CostBasisBreakoutTable() {
      super();
   }

   @Override
   protected DataTableModel<CostBasis> createModel() {

      return new CostBasisBreakoutTableModel(this);
   }

   @Override
   protected void updateRendering(CostBasis basis, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {

   }
}

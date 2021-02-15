package taxtool.ui;

import taxtool.ui.table.DataTableModel;

public class CostBasisBreakoutTableModel extends DataTableModel<CostBasis> {

   private final static String[] COLS = new String[] { "COIN", "AMOUNT", "COST BASIS" };

   public CostBasisBreakoutTableModel(CostBasisBreakoutTable table) {
      super(table, COLS);

   }

   @Override
   public Object getValueAt(CostBasis cb, int rowIndex, int columnIndex) {

      return null;
   }

}

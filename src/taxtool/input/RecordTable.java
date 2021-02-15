package taxtool.input;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;

import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;

abstract public class RecordTable<T extends IRecordInterface> extends DataTable<T> {

   private List<T> selections = null;

   public RecordTable() {

   }

   public void setSelections(List<T> selections) {
      if (selections == null || selections.size() == 0) {

      } else {
         // fire row updates
      }
   }

   @Override
   protected void updateRendering(T rowObj, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {
      // TODO Auto-generated method stub

   }
}

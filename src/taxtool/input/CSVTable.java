package taxtool.input;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CSVTable extends JTable {

   public CSVTable(List<String> headers, List<String> values) {
      setModel(new Model(headers, values));
   }

   public static class Model extends DefaultTableModel {

      private final List<String> headers;
      private final List<String> values;

      public Model(List<String> headers, List<String> values) {
         this.headers = headers;
         this.setColumnIdentifiers(headers.toArray());
         this.addRow(values.toArray());
         this.values = values;
      }

   }

}

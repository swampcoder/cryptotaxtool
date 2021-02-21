package taxtool.ui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

public class ColumnToggleMenu extends JMenu 
{

   public ColumnToggleMenu(DataTable<?> dataTable) 
   {
      super("Columns");
      
      for(int i =0 ; i < dataTable.getColumnCount(); i++)
      {
         final int i2 = i;
         JCheckBoxMenuItem mi = new JCheckBoxMenuItem(dataTable.getColumnName(i), true);
         mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
               if(mi.isSelected()) dataTable.hideColumn(i2);
               else dataTable.showColumn(i2);
            }
         });
         add(mi);
      }
      
   }
}

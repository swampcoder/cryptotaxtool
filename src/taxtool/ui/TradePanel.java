package taxtool.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import taxtool.input.DataRecord;
import taxtool.input.FilterControlPanel;
import taxtool.input.MasterRecordTable;
import taxtool.input.MasterTableMode;
import taxtool.input.RecordType;

public class TradePanel extends JPanel {

   private final JTabbedPane leftTabs = new JTabbedPane();
   private final CoinTotalTable totalTable;
   private final AddressTable addressTable;
   private final MasterRecordTable masterTable;
   private final EtherscanTable etherscanTable;
   private final CostBasisBreakoutTable basisTable = new CostBasisBreakoutTable();
   private final JTabbedPane tabs = new JTabbedPane();

   private JSplitPane tradeSplit = null;

   public TradePanel(JFrame frame, DataRecord record) throws IOException, ParseException {
      super();
      setLayout(new BorderLayout());

      etherscanTable = new EtherscanTable(record);
      addressTable = new AddressTable(record);

      totalTable = new CoinTotalTable(record);
      JPanel totalPane = new JPanel(new BorderLayout());
      totalPane.setPreferredSize(new Dimension(250, Integer.MAX_VALUE));
      totalPane.add(new JScrollPane(totalTable), BorderLayout.CENTER);

      JPanel masterPane = new JPanel(new BorderLayout());

      JTextField masterFilterIn = new JTextField();

      masterTable = new MasterRecordTable(frame, masterFilterIn, record, etherscanTable);
      masterPane.add(new JScrollPane(masterTable), BorderLayout.CENTER);
      masterFilterIn.getDocument().addDocumentListener(masterTable);
      
      JCheckBox fullscreen = new JCheckBox("Fullscreen");
      fullscreen.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            
            GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = graphics.getDefaultScreenDevice();

            if(fullscreen.isSelected())
               device.setFullScreenWindow(frame);
            else
               device.setFullScreenWindow(null);
         }
      
      });

       
        
      
      JPanel filterPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
      filterPane.add(fullscreen);
      filterPane.add(Box.createHorizontalStrut(40));
      filterPane.add(new JLabel("Filter Text"));
      filterPane.add(masterFilterIn);
      
      JButton queryInUsd = new JButton("Query In USD");
      JButton queryOutUsd = new JButton("Query Out USD");
      filterPane.add(Box.createHorizontalStrut(30));
      filterPane.add(queryInUsd);
      filterPane.add(Box.createHorizontalStrut(5));
      filterPane.add(queryOutUsd);
      
      queryInUsd.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            
         }
      });
      
      queryOutUsd.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) 
         {
            
         }
      });
      
      
      masterPane.add(filterPane, BorderLayout.NORTH);
      JPanel lowerMaster = new JPanel(new FlowLayout(FlowLayout.LEFT, 3,1));
      lowerMaster.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), 
            BorderFactory.createEmptyBorder(3, 0, 3, 0)));
      
      for(MasterTableMode tableMode : MasterTableMode.values()) 
      {
         JCheckBox modeBox = new JCheckBox(tableMode.name(), tableMode == MasterTableMode.Default);
         lowerMaster.add(modeBox);
         modeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
               masterTable.setTableMode(tableMode);
            }
         });
         
      }
      lowerMaster.add(Box.createHorizontalStrut(30));
      for(RecordType rt : RecordType.values()) 
      {
         JCheckBox rtBox = new JCheckBox(rt.name(), true);
         lowerMaster.add(rtBox);
         rtBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
               masterTable.setVisible(rt, rtBox.isSelected());
            }
         });
      }
      lowerMaster.add(Box.createHorizontalStrut(30));
      for(int i =2013; i <= 2021; i++) 
      {
         final int i2 = i;
         JCheckBox yearFilter = new JCheckBox(Integer.toString(i), true);
         yearFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               masterTable.setVisible(i2,yearFilter.isSelected());
            }
         });
         lowerMaster.add(yearFilter);
      }
      masterPane.add(lowerMaster,BorderLayout.SOUTH);
      tabs.addTab("MASTER",masterPane);
      tabs.addTab("ADDRESSES", new JScrollPane(addressTable));
      tabs.addTab("STAKES", new JPanel());
      tabs.addTab("ETHEREUM", new JScrollPane(etherscanTable));

      JPanel basisBreakout = new JPanel(new BorderLayout());
      basisBreakout.add(new JScrollPane(basisTable), BorderLayout.CENTER);
      add(basisBreakout, BorderLayout.EAST);
      basisBreakout.setPreferredSize(new Dimension(250, Integer.MAX_VALUE));

      
      leftTabs.addTab("TOTALS", totalPane);

     
    
      leftTabs.addTab("BASIS", basisBreakout);
      leftTabs.addTab("FILTERS", new FilterControlPanel(totalTable));

      tradeSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftTabs, tabs);
      add(tradeSplit, BorderLayout.CENTER);
      tradeSplit.setDividerLocation(300);
   }

   private void addTradePaneControls(JPanel panel) {

      JPanel ctlPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
      JButton exportToCsv = new JButton("Write to CSV");
      exportToCsv.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {

         }
      });
      ctlPane.add(exportToCsv);

      panel.add(ctlPane, BorderLayout.SOUTH);
   }
}

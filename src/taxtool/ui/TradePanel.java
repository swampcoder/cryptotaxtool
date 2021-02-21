package taxtool.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JButton;
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
      JPanel filterPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
      filterPane.add(new JLabel("Filter Text"));
      filterPane.add(masterFilterIn);
      masterPane.add(filterPane, BorderLayout.NORTH);
      
      tabs.addTab("MASTER", new JScrollPane(masterTable));
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

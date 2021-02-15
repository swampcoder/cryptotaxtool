package taxtool.input;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jdesktop.swingx.VerticalLayout;

import taxtool.ui.CoinTotalTable;
import taxtool.ui.TradeTable;

public class FilterControlPanel extends JPanel {

   private final TradeTable tradeTable;
   private final CoinTotalTable coinTable;

   public FilterControlPanel(TradeTable trade, CoinTotalTable coin) {
      this.tradeTable = trade;
      this.coinTable = coin;
      setLayout(new VerticalLayout());
      addFilters();
   }

   private void addFilters() {

      JCheckBox showUsdToCrypto = new JCheckBox("Show USD -> Crypto", true);
      JCheckBox showCryptoToUsd = new JCheckBox("Show Crypto -> USD", true);

      JCheckBox usdOnly = new JCheckBox("USD Only", false);
      usdOnly.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            tradeTable.setUsdOnly(usdOnly.isSelected());
         }
      });
      add(usdOnly);

      add(showUsdToCrypto);
      add(showCryptoToUsd);

      showUsdToCrypto.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            tradeTable.setShowUsdSells(showUsdToCrypto.isSelected());
         }
      });

      showCryptoToUsd.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            tradeTable.setShowUsdBuys(showCryptoToUsd.isSelected());
         }
      });
   }
}

package taxtool.ui;

import java.awt.Color;

import javax.swing.UIManager;

public class UIManagerUtils {

   public static Color labelForeground() {
      return (Color) UIManager.get("label.foreground");
   }

   public static Color tableForeground() {
      return (Color) UIManager.get("table.foreground");
   }

   public static Color tableBackground() {
      return (Color) UIManager.get("table.background");
   }

   public static Color tableSelectionBackground() {
      return (Color) UIManager.getColor("Table.selectionBackground");
   }

   public static Color tableSelectionForeground() {
      return (Color) UIManager.getColor("Table.selectionForeground");
   }

}

package gui;

import java.awt.Frame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram
{
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // РУСИФИКАЦИЯ КНОПОК JOptionPane (ТЕПЕРЬ ВНУТРИ МЕТОДА main)
    UIManager.put("OptionPane.yesButtonText", "Да");
    UIManager.put("OptionPane.noButtonText", "Нет");
    UIManager.put("OptionPane.cancelButtonText", "Отмена");
    UIManager.put("OptionPane.okButtonText", "OK");

    SwingUtilities.invokeLater(() -> {
      MainApplicationFrame frame = new MainApplicationFrame();
      frame.pack();
      frame.setVisible(true);
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    });
  }
}
package gui;

import java.awt.Frame; // класс Frame для работы с окнами

import javax.swing.SwingUtilities; // штука для потокобезопасности в работе с графическим интерфейсом
import javax.swing.UIManager;// Управление внешним видом

public class RobotsProgram
{
  public static void main(String[] args) { // Точка входа в программу
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); // кроссплатформинность (одинаковый вид на разных ос)
    } catch (Exception e) {
      e.printStackTrace();
    }

    // заменяет текст на кнопках на русский язык.
    UIManager.put("OptionPane.yesButtonText", "Да");
    UIManager.put("OptionPane.noButtonText", "Нет");
    UIManager.put("OptionPane.cancelButtonText", "Отмена");
    UIManager.put("OptionPane.okButtonText", "OK");

    SwingUtilities.invokeLater(() -> { //Гарантирует, что создание GUI произойдет в правильном потоке
      MainApplicationFrame frame = new MainApplicationFrame(); //создает главное окно
      frame.pack();// упаковывает компоненты (подбирает оптимальный размер)
      frame.setVisible(true);//показывает окно на экране
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);// разворачивает на весь экран
    });
  }
}
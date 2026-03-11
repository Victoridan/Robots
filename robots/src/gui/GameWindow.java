package gui;

//JInternalFrame: Это окно, которое живет внутри JDesktopPane
// У него нет собственной рамки ОС, оно управляется главным окном

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame // Внутреннее окно MDI
{
    private final GameVisualizer m_visualizer;// Компонент для отрисовки игры
    public GameWindow() 
    {
        // Параметры: заголовок, изменяемый размер, закрываемое, сворачиваемое, разворачиваемое
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();// Создаем визуализатор
        JPanel panel = new JPanel(new BorderLayout());// Панель с менеджером расположения
        panel.add(m_visualizer, BorderLayout.CENTER);// Добавляем визуализатор в центр
        getContentPane().add(panel);// Добавляем панель в окно
        pack();// yпаковываем
    }
}

package gui;

import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import model.RobotModel;
import controller.GameController;

public class GameWindow extends JInternalFrame implements SaveableWindow {
    private final GameVisualizer m_visualizer;
    private final GameController m_controller;

    public GameWindow(RobotModel model) {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer(model);
        m_controller = new GameController(model, m_visualizer);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public String getWindowName() {
        return "GameWindow";
    }

    @Override
    public WindowState getWindowState() {
        int state = isMaximum() ? 1 : (isIcon() ? 2 : 0);
        return new WindowState(getX(), getY(), getWidth(), getHeight(), state, isClosed());
    }
}
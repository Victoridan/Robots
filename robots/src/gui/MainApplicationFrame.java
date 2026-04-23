package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import log.Logger;
import model.RobotModel;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    // Загрузчик и сохранитель конфигурации
    private final WindowStateLoader stateLoader = new WindowStateLoader();
    private final WindowStateSaver stateSaver = new WindowStateSaver();
    private final RobotModel robotModel;
    // Единый реестр окон, поддерживающих сохранение состояния
    private final List<SaveableWindow> saveableWindows = new ArrayList<>();

    // ЗНАЧЕНИЯ ПО УМОЛЧАНИЮ ДЛЯ ОКОН (при первом запуске)
    private static final int LOG_WINDOW_X = 10;
    private static final int LOG_WINDOW_Y = 10;
    private static final int LOG_WINDOW_WIDTH = 300;
    private static final int LOG_WINDOW_HEIGHT = 800;

    private static final int GAME_WINDOW_X = 350;
    private static final int GAME_WINDOW_Y = 10;
    private static final int GAME_WINDOW_WIDTH = 500;
    private static final int GAME_WINDOW_HEIGHT = 500;

    private static final int COORD_WINDOW_X = 350;
    private static final int COORD_WINDOW_Y = 520;
    private static final int COORD_WINDOW_WIDTH = 500;
    private static final int COORD_WINDOW_HEIGHT = 200;

    public MainApplicationFrame(RobotModel model) {
        this.robotModel = model;

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // Создаём и сразу регистрируем окна в едином реестре
        addSaveableWindow(createLogWindow());
        addSaveableWindow(new GameWindow(robotModel));
        addSaveableWindow(createCoordinatesWindow());

        setJMenuBar(generateMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    /**
     * Устанавливает значения по умолчанию для окна (при первом запуске).
     * Вызывается ДО восстановления из конфига.
     */
    private void applyDefaultWindowSettings(JInternalFrame frame, String windowName) {
        switch (windowName) {
            case "LogWindow":
                frame.setLocation(LOG_WINDOW_X, LOG_WINDOW_Y);
                frame.setSize(LOG_WINDOW_WIDTH, LOG_WINDOW_HEIGHT);
                break;
            case "GameWindow":
                frame.setLocation(GAME_WINDOW_X, GAME_WINDOW_Y);
                frame.setSize(GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
                break;
            case "CoordinatesWindow":
                frame.setLocation(COORD_WINDOW_X, COORD_WINDOW_Y);
                frame.setSize(COORD_WINDOW_WIDTH, COORD_WINDOW_HEIGHT);
                break;
        }
    }
    /**
     * Универсальный метод: добавляет окно в MDI и регистрирует его как SaveableWindow
     * Восстановление состояния происходит ЗДЕСЬ
     */
    private void addSaveableWindow(SaveableWindow window) {
        if (!(window instanceof JInternalFrame)) return;
        JInternalFrame frame = (JInternalFrame) window;

        saveableWindows.add(window);// Регистрация для сохранения
        desktopPane.add(frame);// Добавление в контейнер

        // 1. СНАЧАЛА устанавливаем значения по умолчанию
        applyDefaultWindowSettings(frame, window.getWindowName());

        // 2. ПОТОМ, если есть сохранённое состояние — перезаписываем его
        WindowState savedState = stateLoader.getState(window.getWindowName());
        if (savedState != null) {
            frame.setSize(savedState.getWidth(), savedState.getHeight());
            frame.setLocation(savedState.getX(), savedState.getY());
            try {
                if (savedState.getState() == 1) {
                    frame.setMaximum(true);
                } else if (savedState.getState() == 2) {
                    frame.setIcon(true);
                }
                frame.setClosed(savedState.isClosed());
            } catch (Exception ignored) {}
        }

        frame.setVisible(!frame.isClosed());
    }

    protected LogWindow createLogWindow() {
        LogWindow lw = new LogWindow(Logger.getDefaultLogSource());
        lw.pack();
        return lw;
    }

    protected RobotCoordinatesWindow createCoordinatesWindow() {
        RobotCoordinatesWindow cw = new RobotCoordinatesWindow(robotModel);
        cw.pack();
        return cw;
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem exitMenuItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitMenuItem.addActionListener(event -> confirmExit());
        fileMenu.add(exitMenuItem);

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener(event -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        lookAndFeelMenu.add(systemLookAndFeel);
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener(event -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_M);
        addLogMessageItem.addActionListener(event -> Logger.debug("Новая строка"));
        testMenu.add(addLogMessageItem);

        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти из программы?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            saveAllWindowsAndExit();
        }
    }
    /**
     * Автоматическое сохранение состояния ВСЕХ зарегистрированных окон
     */
    private void saveAllWindowsAndExit() {
        for (SaveableWindow window : saveableWindows) {
            stateSaver.saveState(window.getWindowName(), window.getWindowState());
        }
        stateSaver.saveToFile();
        System.exit(0);
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
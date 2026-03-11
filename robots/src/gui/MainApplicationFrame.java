package gui;

import java.awt.Dimension; //используется для задания размеров компонентов (ширина, высота)
import java.awt.Toolkit;// класс для работы с системными настройками (например, получение размеров экрана)
import java.awt.event.KeyEvent;//содержит константы для обработки событий клавиатуры

import javax.swing.JDesktopPane;// контейнер для внутренних окон
import javax.swing.JFrame;//главное окно приложения
import javax.swing.JInternalFrame;//  внутреннее окно внутри JDesktopPane
import javax.swing.JMenu; //  для создания меню приложения
import javax.swing.JMenuBar;//для создания меню приложения
import javax.swing.JMenuItem;// для создания меню приложения
import javax.swing.JOptionPane;// для отображения диалоговых окон (сообщения, подтверждения, ввод данных)
import javax.swing.SwingUtilities; // класс для работы с Swing например, для потокобезопаснjcnv
import javax.swing.UIManager;// управление внешним видом
import javax.swing.UnsupportedLookAndFeelException;//исключение при попытке установить неподдерживаемый внешний вид

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame // Наследуемся от JFrame (главное окно ОС)
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        // Получает размер экрана и устанавливает главное окно так, чтобы оно занимало почти весь экран, но с отступами.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);// Устанавливаем рабочий стол как содержимое окна


        LogWindow logWindow = createLogWindow();// Создаем и добавляем окно логов
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow(); // Создаем и добавляем игровое окно
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());// Создаем и устанавливаем меню
        setDefaultCloseOperation(EXIT_ON_CLOSE);// При закрытии главного окна - выход
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());// Получаем источник логов
        logWindow.setLocation(10,10);// позиция на рабочем столе
        logWindow.setSize(300, 800);// hазмер окна
        setMinimumSize(logWindow.getSize());// устанавливаем минимальный размер главного окна
        logWindow.pack();// eпаковываем
        Logger.debug("Протокол работает");//первое сообщение в лог
        return logWindow;
    }
//Метод добавления окна на рабочий стол
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);//Добавляем на рабочий стол
        frame.setVisible(true);//делаем видимым
    }
    //Метод создания меню
    private JMenuBar generateMenuBar()// Создаем панель меню
    {
        JMenuBar menuBar = new JMenuBar();

        // МЕНЮ "Файл"
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F); // Alt + F для быстрого доступа

// Пункт "Выход"
        JMenuItem exitMenuItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitMenuItem.addActionListener((event) -> {// addActionListener: Подключает обработчик события. Когда пользователь кликает на пункт меню, выполняется код внутри лямбды.
            confirmExit(); // Вызываем метод подтверждения выхода
        });
        fileMenu.add(exitMenuItem);// Добавляем пункт в меню

        // МЕНЮ "Режим отображения"
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            // Пункт "Системная схема"  Позволяет менять внешний вид программы на системный (как у Windows) или универсальный
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        // СУЩЕСТВУЮЩЕЕ МЕНЮ "Тесты"
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            // Пункт "Сообщение в лог"
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }

        // Добавляем все меню
        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    //         Метод для подтверждения выхода из приложения

    private void confirmExit() {
        // Показываем диалог подтверждения
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти из программы?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,// Кнопки Да/Нет
                JOptionPane.QUESTION_MESSAGE// Иконка вопроса
        );

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);// Завершаем программу
        }
    }
    //Метод смены внешнего вида
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className); // Устанавливаем новый LookAndFeel
            SwingUtilities.updateComponentTreeUI(this);// Обновляем все компоненты
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
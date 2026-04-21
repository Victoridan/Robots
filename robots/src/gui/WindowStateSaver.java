package gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Сохранитель состояний окон в файл конфигурации.
 *
 */
public class WindowStateSaver {
    private static final String FILENAME = ".robots_app_config.properties";
    private final String filepath;

    // Временное хранилище состояний перед записью в файл
    private final Map<String, WindowState> windowStates = new HashMap<>();

    public WindowStateSaver() {
        String userHome = System.getProperty("user.home");
        filepath = userHome + File.separator + FILENAME;
    }

    public void saveState(String windowName, WindowState state) {
        windowStates.put(windowName, state);
    }

    /**
     * Записать все накопленные состояния в файл.
     * Вызывается один раз при выходе из программы.
     */
    public void saveToFile() {
        Properties props = new Properties();

        // Преобразуем Map в Properties
        for (Map.Entry<String, WindowState> entry : windowStates.entrySet()) {
            String name = entry.getKey();
            WindowState state = entry.getValue();

            // Формируем ключи вида "window.ИмяОкна.параметр"
            props.setProperty("window." + name + ".x", String.valueOf(state.getX()));
            props.setProperty("window." + name + ".y", String.valueOf(state.getY()));
            props.setProperty("window." + name + ".width", String.valueOf(state.getWidth()));
            props.setProperty("window." + name + ".height", String.valueOf(state.getHeight()));
            props.setProperty("window." + name + ".state", String.valueOf(state.getState()));
            props.setProperty("window." + name + ".closed", String.valueOf(state.isClosed()));
        }

        // Запись в файл
        try (FileOutputStream fos = new FileOutputStream(filepath)) {
            props.store(fos, "Application window configuration");
        } catch (IOException e) {
            System.err.println("Error saving window configuration: " + e.getMessage());
        }
    }
}
package gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Загрузчик состояний окон из файла конфигурации.
 * Единственная обязанность: прочитать файл и создать объекты WindowState.
 */
public class WindowStateLoader {
    private static final String FILENAME = ".robots_app_config.properties";
    private final String filepath;
    private final Map<String, WindowState> windowStates = new HashMap<>();

// Получить состояние окна по имени.

    public WindowStateLoader() {
        String userHome = System.getProperty("user.home");
        filepath = userHome + File.separator + FILENAME;
        loadFromFile();
    }

    public WindowState getState(String windowName) {
        return windowStates.get(windowName);
    }

    private void loadFromFile() {
        File configFile = new File(filepath);
        if (!configFile.exists()) return; // Если файла нет — первый запуск, ничего не загружаем

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filepath)) {
            props.load(fis);
            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("window.") && key.endsWith(".x")) {
                    String wname = key.substring(7, key.length() - 2);
                    String prefix = "window." + wname;
                    try {
                        int x = Integer.parseInt(props.getProperty(prefix + ".x", "0"));
                        int y = Integer.parseInt(props.getProperty(prefix + ".y", "0"));
                        int width = Integer.parseInt(props.getProperty(prefix + ".width", "400"));
                        int height = Integer.parseInt(props.getProperty(prefix + ".height", "300"));
                        int state = Integer.parseInt(props.getProperty(prefix + ".state", "0"));
                        boolean closed = Boolean.parseBoolean(props.getProperty(prefix + ".closed", "false"));

                        // Создаём объект состояния и сохраняем в Map
                        windowStates.put(wname, new WindowState(x, y, width, height, state, closed));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing window state for " + wname + ": " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading window configuration: " + e.getMessage());
        }
    }
}
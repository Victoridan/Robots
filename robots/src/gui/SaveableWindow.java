package gui;

/**
 * Интерфейс для окон, состояние которых можно сохранять.
 * Восстановление происходит через прямое управление из MainApplicationFrame.
 */
public interface SaveableWindow {
    String getWindowName();      // Уникальное имя окна
    WindowState getWindowState(); // Текущее состояние окна
}
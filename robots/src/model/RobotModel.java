package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель робота. Содержит всю логику движения и состояние.
 * - Слушатели (View) подписываются через addListener()
 * - При изменении состояния вызывается notifyListeners()
 * - View обновляются автоматически
 */
public class RobotModel {
    // Положение робота
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    // Цель
    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    // Константы движения
    private static final double maxVelocity = 0.1;          // Максимальная линейная скорость
    private static final double maxAngularVelocity = 0.001; // Максимальная угловая скорость

    // Список слушателей
    private final List<RobotModelListener> listeners = new ArrayList<>();

    /**
     * Интерфейс слушателя изменений модели.
     * View (окна) реализуют этот интерфейс для получения уведомлений.
     */
    public interface RobotModelListener {
        void onRobotPositionChanged(double x, double y, double direction);
    }

    /**
     * Подписка на обновления.
     * View вызывает этот метод, чтобы получать уведомления.
     */
    public void addListener(RobotModelListener listener) {
        synchronized(listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Отписка от обновлений (для предотвращения утечек памяти).
     */
    public void removeListener(RobotModelListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }

    //  Геттеры
    public double getRobotPositionX() { return m_robotPositionX; }
    public double getRobotPositionY() { return m_robotPositionY; }
    public double getRobotDirection() { return m_robotDirection; }
    public int getTargetPositionX() { return m_targetPositionX; }
    public int getTargetPositionY() { return m_targetPositionY; }

    //  Сеттеры
    public void setTargetPosition(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
    }

    public void setTargetPosition(double x, double y) {
        m_targetPositionX = (int)x;
        m_targetPositionY = (int)y;
    }

    /**
     * Обновляет позицию робота на один шаг.
     * Вызывается по таймеру из GameVisualizer.
     */
    public void updatePosition() {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);

        if (distance < 0.5) {
            return; // Уже достигли цели
        }

        double velocity = maxVelocity; //линейная скорость
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY,
                m_targetPositionX, m_targetPositionY); //угол до цели

        // ИСПРАВЛЕНИЕ ОШИБКИ: правильное вычисление угловой скорости
        // Теперь робот выбирает кратчайший путь поворота
        double angularVelocity = calculateAngularVelocity(angleToTarget);//Угловая скорость

        moveRobot(velocity, angularVelocity, 10);

        // Уведомляем всех слушателей об изменении
        notifyListeners();
    }

    /**
     * ИСПРАВЛЕННЫЙ МЕТОД: правильная логика выбора направления поворота.
     * Ошибка в старой версии: робот поворачивал только в одну сторону,
     * даже если это был более длинный путь.
     *
     * Исправление: нормализуем разницу углов в диапазон [-π, π] и выбираем
     * кратчайший путь (поворачиваем в сторону, где разница меньше по модулю).
     * angleToTarget угол до цели в радианах
     *
     * Метод calculateAngularVelocity():
     * Вычисляет разницу между желаемым углом (angleToTarget) и текущим углом робота
     * Нормализует разницу в диапазон [-π, π]
     * Возвращает +maxAngularVelocity, если нужно повернуть по часовой стрелке
     * Возвращает -maxAngularVelocity, если нужно повернуть против часовой стрелки
     * Возвращает 0, если уже смотрим на цель
     */
    private double calculateAngularVelocity(double angleToTarget) {
        // Вычисляем разность углов
        double diff = angleToTarget - m_robotDirection;

        // Нормализуем разность в диапазон [-π, π]
        while (diff < -Math.PI) diff += 2 * Math.PI;
        while (diff > Math.PI) diff -= 2 * Math.PI;

        // Если разница очень маленькая - не поворачиваем
        if (Math.abs(diff) < 0.01) {
            return 0;
        }

        // Поворачиваем в сторону, где разница положительная или отрицательная
        return diff > 0 ? maxAngularVelocity : -maxAngularVelocity;
    }

    /**
     * Перемещает робота на основе заданных скоростей и времени.
     * Поддерживает как прямолинейное движение, так и движение по дуге.
     *
     * по прямой:
     * V — скорость (velocity)
     * t — время (duration)
     * θ — угол направления (m_robotDirection)
     *
     * по дуге:
     * R — радиус окружности. velocity / angularVelocity	Радиус поворота (R)
     * V — линейная скорость
     * ω — угловая скорость (angularVelocity)
     * Физический смысл: Изменение координаты X равно радиусу, умноженному на изменение синуса угла.
     *  Изменение координаты Y равно радиусу, умноженному на изменение косинуса угла (со знаком минус).
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX, newY;

        if (Math.abs(angularVelocity) < 0.0001) {
            // Движение по прямой (угловая скорость близка к нулю)
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        } else {
            // Движение по дуге
            newX = m_robotPositionX + velocity / angularVelocity *
                    (Math.sin(m_robotDirection + angularVelocity * duration) - Math.sin(m_robotDirection));
            newY = m_robotPositionY - velocity / angularVelocity *
                    (Math.cos(m_robotDirection + angularVelocity * duration) - Math.cos(m_robotDirection));
        }

        // Защита от NaN и бесконечных значений
        if (!Double.isFinite(newX) || !Double.isFinite(newY)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
    }

    /**
     * Уведомляет всех зарегистрированных слушателей об изменении позиции
     */
    private void notifyListeners() {
        // Создаём копию списка для безопасного итерирования
        List<RobotModelListener> listenersCopy;
        synchronized(listeners) {
            listenersCopy = new ArrayList<>(listeners);
        }
        for (RobotModelListener listener : listenersCopy) {
            listener.onRobotPositionChanged(m_robotPositionX, m_robotPositionY, m_robotDirection);
        }
    }

    // Вспомогательные математические методы

    //вычисляет расстояние между двумя точками(корень из суммы квадрата координат)
    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    // находит угол под которым мы должны двигаться к цели
    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    // метод ограничивает значение заданными пределами (min и max).
    // Если значение выходит за пределы, оно "обрезается" до ближайшей границы.
    //робот не может двигаться быстрее максимальной скорости
    private static double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    // метод приводит угол к нормализованному виду — значению в диапазоне от 0 до 2π (0 до 360 градусов)
    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}
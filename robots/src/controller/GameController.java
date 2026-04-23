package controller;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import model.RobotModel;

public class GameController {
    private final RobotModel model;
    private final Timer timer;

    public GameController(RobotModel model, Component view) {
        this.model = model;

        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getPoint().x, e.getPoint().y);
            }
        });

        timer = new Timer("Model update timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.updatePosition();
            }
        }, 0, 10);
    }
}


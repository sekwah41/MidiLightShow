package com.sekwah.midistreamcontroller;

import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.LightStatus;
import com.sekwah.midistreamcontroller.controller.MidiController;
import com.sekwah.midistreamcontroller.keys.Key;
import com.sekwah.midistreamcontroller.lightdata.stored.PageButtons;
import com.sekwah.midistreamcontroller.menu.Menu;
import com.sekwah.midistreamcontroller.menu.PerformanceMenu;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ControllerWindow extends JFrame {

    public final MidiController midiController;
    public final JLabel keyLabel;

    //private Key[][] keyGrid = new Key[8][8];

    private long currentTime = 0;

    private long nextTime = System.currentTimeMillis();

    private long updateDelay = 20;

    public ArrayList<PageButtons> buttonPages = new ArrayList<>();
    private Menu menu;

    public ControllerWindow() {

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeFrame();
            }
        });

        this.buttonPages.add(new PageButtons());

        this.midiController = new MidiController(this);

        this.midiController.clearLaunchpad();

        //this.fillLights(LightData.GREEN_HIGH, LightStatus.STATUS_ON);
        //this.fillLights(LightData.GREEN_HIGH, LightStatus.STATUS_OFF);

        this.setTitle("SekC's LightShow");

        this.setSize(300, 60);

        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);

        keyLabel = new JLabel("No visuals yet, close to stop program.");
        keyLabel.setBounds(10, 3, 280, 20);

        this.add(keyLabel);

        super.setVisible(true);

        this.registerKeys();

        //this.showKeys();

        this.setMenu(new PerformanceMenu(this));

        Thread thread = new Thread(new LightThread());
        thread.start();
    }

    private void setMenu(Menu menu) {
        this.menu = menu;
        menu.init();
    }

    private int currentColor = -1;

    private LightData[] lightColor = {LightData.GREEN_HIGH, LightData.RED_HIGH, LightData.YELLOW_HIGH};

    public void runTopBar(int topBarButton) {
        menu.runTopBar(topBarButton);
    }

    public void runGrid(int x, int y) {
        if(x < 0 || x > 7|| y < 0 || y > 7) {
            return;
        }
        menu.runGrid(x,y);
        /*Key key = this.keyGrid[x][y];
        if(key != null) {
            key.run();
        }*/

    }

    class LightThread implements Runnable {

        @Override
        public void run() {
            while(true) {
                try {
                    // TODO update light code

                    nextTime = nextTime + updateDelay;
                    long timeDelay = nextTime - System.currentTimeMillis();
                    //System.out.println(updateDelay);
                    if(timeDelay < 0) timeDelay = 0;
                    Thread.sleep(timeDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void registerKeys() {


        /*this.registerKey(new Key(this.midiController, 8, 8, LightData.GREEN_HIGH) {
            @Override
            public void run() {
                long newTime = System.currentTimeMillis();
                long pressDistance = newTime - lastPress;
                lastPress = newTime;
                if(pressDistance >= 10000) {
                    return;
                }
                nextTime = newTime;
                for(int i = 0; i < averageDelay.length - 1; i++) {
                    averageDelay[i + 1] = averageDelay[i];
                }
                averageDelay[0] = pressDistance;
                long average = 0;
                for(int i = 0; i < averageDelay.length; i++) {
                    average += averageDelay[i];
                }
                average /= averageDelay.length;
                if(average < 10000) {
                    updateDelay = average;
                }
            }
        });*/
    }

    /*private void registerKey(Key key) {
        this.keyGrid[key.getX() - 1][key.getY() - 1] = key;
    }*/

    private long delayWithTimeDifference(int delay) {
        long diff = this.currentTime - System.currentTimeMillis() + delay;
        return diff > 0 ? diff : 0;
    }

    private void fillLights(LightData color, LightStatus status) {
        try {
            for(int y = 1; y <= 8; y++) {
                this.currentTime = System.currentTimeMillis();
                for(int x = 1; x <= 8; x++) {
                    this.midiController.setGrid(x, y, color, status);
                }

                Thread.sleep(this.delayWithTimeDifference(50));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeFrame() {
        /*this.closeLights(LightData.RED_HIGH, LightStatus.STATUS_ON);
        this.closeLightsRev(LightData.RED_HIGH, LightStatus.STATUS_OFF);*/
        this.midiController.clearLaunchpad();
        this.dispose();
        System.exit(0);
    }

    private void closeLightsRev(LightData color, LightStatus status) {
        try {
            for (int i = 7; i >= 0; i--) {
                this.closeLightFrame(i, color, status);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeLights(LightData color, LightStatus status) {
        try {
            for (int i = 0; i < 8; i++) {
                this.closeLightFrame(i, color, status);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeLightFrame(int i, LightData color, LightStatus status) throws InterruptedException {
        this.currentTime = System.currentTimeMillis();
        for (int x = 8; x >= 8 - i; x--) {
            this.midiController.setGrid(x, 8 - i, color, status);
        }
        for (int y = 8; y >= 8 - i; y--) {
            this.midiController.setGrid(8 - i, y, color, status);
        }
        Thread.sleep(this.delayWithTimeDifference(50));
    }
}

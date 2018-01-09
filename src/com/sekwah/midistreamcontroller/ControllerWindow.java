package com.sekwah.midistreamcontroller;

import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.LightStatus;
import com.sekwah.midistreamcontroller.controller.MidiController;
import com.sekwah.midistreamcontroller.keys.Key;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ControllerWindow extends JFrame {

    private final MidiController midiController;

     private Key[][] keyGrid = new Key[8][8];

    private long currentTime = 0;

    private long nextTime = System.currentTimeMillis();

    private long updateDelay = 20;

    private long[] averageDelay = {0,0,0,0};

    private long lastPress = 0;

    public ControllerWindow() {

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeFrame();
            }
        });

        this.midiController = new MidiController(this);

        this.midiController.clearLaunchpad();

        //this.fillLights(LightData.GREEN_HIGH, LightStatus.STATUS_ON);

        this.setTitle("SekC's Stream Controller");

        this.setSize(300, 60);

        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);

        JLabel keyLabel = new JLabel("No visuals yet, close to stop program.");
        keyLabel.setBounds(10, 3, 280, 20);

        this.add(keyLabel);

        super.setVisible(true);

        this.registerKeys();

        //this.showKeys();

        Thread thread = new Thread(new LightThread());
        thread.start();
    }

    private int currentColor = -1;

    private LightData[] lightColor = {LightData.GREEN_HIGH, LightData.RED_HIGH, LightData.YELLOW_HIGH};

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

    private int currentID = 0;

    private int lightOffset(int x, int y, int loop){
        return ((x + y * 16 + loop) % 3);
    }



    private void registerKeys() {


        this.registerKey(new Key(this.midiController, 8, 8, LightData.GREEN_HIGH) {
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
        });
    }

    private void registerKey(Key key) {
        this.keyGrid[key.getX() - 1][key.getY() - 1] = key;
    }

    private void showKeys() {
        try {
            for (int y = 1; y <= 8; y++) {
                this.currentTime = System.currentTimeMillis();
                for (int x = 1; x <= 8; x++) {
                    Key key = this.keyGrid[x - 1][y - 1];
                    if(key == null) {
                        this.midiController.setGrid(x, y, LightData.OFF, LightStatus.STATUS_OFF);
                    }
                    else {
                        this.midiController.setGrid(x, y, key.getDefaultColor(), LightStatus.STATUS_ON);
                    }
                }
                Thread.sleep(this.delayWithTimeDifference(50));
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

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
        this.closeLights(LightData.RED_HIGH, LightStatus.STATUS_ON);
        this.closeLightsRev(LightData.RED_HIGH, LightStatus.STATUS_OFF);
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


    public void runKey(int x, int y) {
        if(x < 0 || x > 7|| y < 0 || y > 7) {
            return;
        }
        Key key = this.keyGrid[x][y];
        if(key != null) {
            key.run();
        }
    }
}

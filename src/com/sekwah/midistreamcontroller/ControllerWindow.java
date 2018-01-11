package com.sekwah.midistreamcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sekwah.midistreamcontroller.animation.AnimationController;
import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.LightStatus;
import com.sekwah.midistreamcontroller.controller.MidiController;
import com.sekwah.midistreamcontroller.lightdata.stored.PageButtons;
import com.sekwah.midistreamcontroller.menu.Menu;
import com.sekwah.midistreamcontroller.menu.PerformanceMenu;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ControllerWindow extends JFrame {

    public final MidiController midiController;
    public final JLabel keyLabel;
    public final JTextField timeField;
    public final AnimationController animController;

    //private Key[][] keyGrid = new Key[8][8];

    private long currentTime = 0;

    private long nextTime = System.currentTimeMillis();

    private long updateDelay = 10;

    public ArrayList<PageButtons> buttonPages = new ArrayList<>();

    public int currentPage = 1;

    private Menu menu;

    public int lastKeyPlayed = -1;

    public String projectName = "Project.json";

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

        this.setSize(370, 60);

        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);

        keyLabel = new JLabel("No visuals yet, close to stop program.");
        keyLabel.setBounds(10, 3, 280, 20);

        this.add(keyLabel);

        timeField = new JTextField("100");
        timeField.setBounds(310, 3, 50, 20);

        this.add(timeField);

        super.setVisible(true);

        if(new File(projectName).exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(projectName));
                Gson gson = new Gson();
                java.lang.reflect.Type buttonPageType = new TypeToken<ArrayList<PageButtons>>(){}.getType();
                buttonPages = gson.fromJson(bufferedReader, buttonPageType);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //this.showKeys();

        animController = new AnimationController(midiController);

        this.setMenu(new PerformanceMenu(this));

        Thread thread = new Thread(new LightThread());
        thread.start();
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        menu.init();
    }

    private int currentColor = -1;

    private LightData[] lightColor = {LightData.GREEN_HIGH, LightData.RED_HIGH, LightData.YELLOW_HIGH};

    public void runTopBar(int topBarButton) {
        menu.runTopBar(topBarButton);
    }

    public void runGrid(int x, int y) {
        if(x < 0 || x > 8 || y < 0 || y > 7) {
            return;
        }
        if(x != 8) {
            menu.runGrid(x,y);
        }
        else {
            menu.sideBar(y);
        }
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
                    animController.updateDisplay(updateDelay);
                    //System.out.println(updateDelay);
                    if(timeDelay < 0) timeDelay = 0;
                    Thread.sleep(timeDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
        try (Writer writer = new FileWriter(projectName)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(buttonPages, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

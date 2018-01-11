package com.sekwah.midistreamcontroller.menu;

import com.sekwah.midistreamcontroller.ControllerWindow;
import com.sekwah.midistreamcontroller.animation.AnimationController;
import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.LightStatus;
import com.sekwah.midistreamcontroller.controller.MidiController;
import com.sekwah.midistreamcontroller.lightdata.stored.PageButtons;

import javax.swing.*;
import java.util.ArrayList;

public class PerformanceMenu implements Menu {

    private final ControllerWindow controllerWindow;
    private final AnimationController animController;
    private LightData currentColor = LightData.GREEN_HIGH;

    private LightData redStrength = LightData.RED_HIGH;
    private LightData greenStrength = LightData.GREEN_HIGH;

    private final MidiController midiController;
    private final JLabel keyLabel;
    private final ArrayList<PageButtons> buttonPages;

    public PerformanceMenu(ControllerWindow controllerWindow) {
        this.controllerWindow = controllerWindow;
        this.midiController = controllerWindow.midiController;
        this.keyLabel = controllerWindow.keyLabel;
        this.buttonPages = controllerWindow.buttonPages;
        this.animController = this.controllerWindow.animController;
    }

    @Override
    public void init() {
        this.midiController.clearLaunchpad();
        this.updateTopBar();
    }

    @Override
    public void runGrid(int x, int y) {
        int beforeKey = this.controllerWindow.lastKeyPlayed;
        this.controllerWindow.lastKeyPlayed = x + y * 8;
        if(beforeKey == -1) {
            this.updateTopBar();
        }
        if(this.controllerWindow.currentPage > 0) {
            this.animController.addAnimation(this.buttonPages.get(this.controllerWindow.currentPage - 1).buttons[x + y * 8]);
        }

    }

    @Override
    public void runTopBar(int topBarButton) {
        switch (topBarButton) {
            case 1:
                if(this.controllerWindow.currentPage > 0) {
                    this.buttonPages.remove(this.controllerWindow.currentPage - 1);
                    int size = this.buttonPages.size();
                    if(this.controllerWindow.currentPage > size) {
                        this.controllerWindow.currentPage = size;
                    }
                }
                break;
            case 2:
                if(this.controllerWindow.currentPage > 1) {
                    this.controllerWindow.currentPage--;
                }
                break;
            case 3:
                if(this.controllerWindow.currentPage++ >= this.buttonPages.size()) {
                    this.buttonPages.add(new PageButtons());
                }
                break;
            case 5:
                if(this.controllerWindow.lastKeyPlayed != -1) {
                    this.controllerWindow.setMenu(new EditMenu(this.controllerWindow));
                }
                return;
        }
        this.updateTopBar();
    }

    @Override
    public void sideBar(int y) {

    }

    private void updateTopBar() {
        this.midiController.setKey(108, LightData.YELLOW_HIGH, LightStatus.STATUS_CONTROL);
        if(this.controllerWindow.lastKeyPlayed == -1) {
            this.midiController.setKey(109, LightData.RED_LOW, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(109, LightData.YELLOW_LOW, LightStatus.STATUS_CONTROL);
        }

        if(this.controllerWindow.currentPage > 0) {
            this.midiController.setKey(105, LightData.RED_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(105, LightData.OFF, LightStatus.STATUS_CONTROL);
        }
        if(this.controllerWindow.currentPage > 1) {
            this.midiController.setKey(106, LightData.YELLOW_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(106, LightData.OFF, LightStatus.STATUS_CONTROL);
        }
        if(this.controllerWindow.currentPage < buttonPages.size()) {
            this.midiController.setKey(107, LightData.YELLOW_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(107, LightData.GREEN_HIGH, LightStatus.STATUS_CONTROL);
        }
        this.keyLabel.setText("PerformanceMode: Page " + this.controllerWindow.currentPage + " of " + buttonPages.size());
    }
}

package com.sekwah.midistreamcontroller.menu;

import com.sekwah.midistreamcontroller.ControllerWindow;
import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.LightStatus;
import com.sekwah.midistreamcontroller.controller.MidiController;
import com.sekwah.midistreamcontroller.lightdata.stored.PageButtons;

import javax.swing.*;
import java.util.ArrayList;

public class PerformanceMenu implements Menu {

    private final MidiController midiController;
    private final JLabel keyLabel;
    private final ArrayList<PageButtons> buttonPages;

    private int currentPage = 1;

    public PerformanceMenu(ControllerWindow controllerWindow) {
        this.midiController = controllerWindow.midiController;
        this.keyLabel = controllerWindow.keyLabel;
        this.buttonPages = controllerWindow.buttonPages;
    }

    @Override
    public void init() {
        this.midiController.clearLaunchpad();
        this.updateTopBar();
    }

    @Override
    public void runGrid(int x, int y) {

    }

    @Override
    public void runTopBar(int topBarButton) {
        switch (topBarButton) {
            case 1:
                if(currentPage > 0) {
                    this.buttonPages.remove(currentPage - 1);
                    int size = this.buttonPages.size();
                    if(currentPage > size) {
                        this.currentPage = size;
                    }
                }
                break;
            case 2:
                if(currentPage > 1) {
                    currentPage--;
                }
                break;
            case 3:
                if(currentPage++ >= this.buttonPages.size()) {
                    this.buttonPages.add(new PageButtons());
                }
                break;
        }
        this.updateTopBar();
    }

    private void updateTopBar() {
        this.midiController.setKey(108, LightData.YELLOW_LOW, LightStatus.STATUS_CONTROL);
        if(currentPage > 0) {
            this.midiController.setKey(105, LightData.RED_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(105, LightData.OFF, LightStatus.STATUS_CONTROL);
        }
        if(currentPage > 1) {
            this.midiController.setKey(106, LightData.YELLOW_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(106, LightData.OFF, LightStatus.STATUS_CONTROL);
        }
        if(currentPage < buttonPages.size()) {
            this.midiController.setKey(107, LightData.YELLOW_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(107, LightData.GREEN_HIGH, LightStatus.STATUS_CONTROL);
        }
        this.keyLabel.setText("PerformanceMode: Page " + currentPage + " of " + buttonPages.size());
    }
}

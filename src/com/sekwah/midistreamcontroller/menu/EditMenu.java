package com.sekwah.midistreamcontroller.menu;

import com.sekwah.midistreamcontroller.ControllerWindow;
import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.LightStatus;
import com.sekwah.midistreamcontroller.controller.MidiController;
import com.sekwah.midistreamcontroller.lightdata.stored.ButtonAnim;
import com.sekwah.midistreamcontroller.lightdata.stored.KeyFrame;

import javax.swing.*;

public class EditMenu implements Menu {

    private final ControllerWindow controllerWindow;
    private final ButtonAnim buttonAnim;
    private LightData currentColor = LightData.GREEN_HIGH;

    private LightData redStrength = LightData.RED_HIGH;
    private LightData greenStrength = LightData.GREEN_HIGH;

    private final MidiController midiController;
    private final JLabel keyLabel;

    private int currentFrame = 1;

    public EditMenu(ControllerWindow controllerWindow) {
        this.controllerWindow = controllerWindow;
        this.midiController = controllerWindow.midiController;
        this.keyLabel = controllerWindow.keyLabel;
        System.out.println(this.controllerWindow.lastKeyPlayed);
        this.buttonAnim = this.controllerWindow.buttonPages.get(this.controllerWindow.currentPage - 1).buttons[this.controllerWindow.lastKeyPlayed];
    }

    @Override
    public void init() {
        this.midiController.clearLaunchpad();
        this.updateTopBar();
        this.updateColorPreview();
        this.drawColorSelector();
        this.loadPage(currentFrame);
    }

    private void drawColorSelector() {
        this.midiController.setKey(8, LightData.RED_HIGH, LightStatus.STATUS_ON);
        this.midiController.setKey(8 + 16, LightData.RED_MEDIUM, LightStatus.STATUS_ON);
        this.midiController.setKey(8 + 16 * 2, LightData.RED_LOW, LightStatus.STATUS_ON);
        this.midiController.setKey(8 + 16 * 3, LightData.RED_OFF, LightStatus.STATUS_ON);
        this.midiController.setKey(8 + 16 * 4, LightData.GREEN_OFF, LightStatus.STATUS_ON);
        this.midiController.setKey(8 + 16 * 5, LightData.GREEN_LOW, LightStatus.STATUS_ON);
        this.midiController.setKey(8 + 16 * 6, LightData.GREEN_MEDIUM, LightStatus.STATUS_ON);
        this.midiController.setKey(8 + 16 * 7, LightData.GREEN_HIGH, LightStatus.STATUS_ON);
    }

    private void updateColorPreview() {
        this.midiController.setKey(111, this.redStrength.getValue() + this.greenStrength.getValue(), LightStatus.STATUS_CONTROL.getValue());
    }

    @Override
    public void runGrid(int x, int y) {
        if(this.currentFrame <= 0) {
            return;
        }
        KeyFrame frame = buttonAnim.buttonKeyframes.get(this.currentFrame - 1);
        frame.buttons[x + y * 8] = redStrength.getValue() + greenStrength.getValue();
        this.midiController.setKey(x + y * 16, this.redStrength.getValue() + this.greenStrength.getValue(), LightStatus.STATUS_ON.getValue());
    }

    private void saveTime() {
        try {
            if (currentFrame > 0) {
                KeyFrame frame = this.buttonAnim.buttonKeyframes.get(currentFrame - 1);
                frame.frameLength = Integer.parseInt(this.controllerWindow.timeField.getText());
            }
        }
        catch(NumberFormatException e) {

        }
    }

    private void loadPage(int frameNum) {
        if (frameNum <= 0) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    this.midiController.setKey(x + y * 16, LightData.OFF, LightStatus.STATUS_ON);
                }
            }
        }
        else{
            KeyFrame frame = this.buttonAnim.buttonKeyframes.get(frameNum - 1);
            this.controllerWindow.timeField.setText(String.valueOf(frame.frameLength));
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    this.midiController.setKey(x + y * 16, frame.buttons[x + y * 8], LightStatus.STATUS_ON.getValue());
                }
            }
        }
    }

    @Override
    public void runTopBar(int topBarButton) {
        switch (topBarButton) {
            case 0:
                if(this.currentFrame > 0) {
                    if (this.currentFrame++ >= this.buttonAnim.buttonKeyframes.size()) {
                        KeyFrame frame = this.buttonAnim.buttonKeyframes.get(this.currentFrame - 2).copy();
                        this.buttonAnim.buttonKeyframes.add(frame);
                    }
                }
                break;
            case 1:
                if(this.currentFrame > 0) {
                    this.buttonAnim.buttonKeyframes.remove(this.currentFrame - 1);
                    int size = this.buttonAnim.buttonKeyframes.size();
                    if(this.currentFrame > size) {
                        this.currentFrame = size;
                    }
                    this.loadPage(this.currentFrame);
                }
                break;
            case 2:
                if(this.currentFrame > 1) {
                    this.currentFrame--;
                    this.loadPage(this.currentFrame);
                }
                break;
            case 3:
                if(this.currentFrame++ >= this.buttonAnim.buttonKeyframes.size()) {
                    this.buttonAnim.buttonKeyframes.add(new KeyFrame());
                }
                this.loadPage(this.currentFrame);
                break;
            case 4:
                this.controllerWindow.setMenu(new PerformanceMenu(this.controllerWindow));
                return;
        }
        this.updateTopBar();
    }

    @Override
    public void sideBar(int y) {
        switch (y) {
            case 0:
                this.redStrength = LightData.RED_HIGH;
                break;
            case 1:
                this.redStrength = LightData.RED_MEDIUM;
                break;
            case 2:
                this.redStrength = LightData.RED_LOW;
                break;
            case 3:
                this.redStrength = LightData.RED_OFF;
                break;
            case 4:
                this.greenStrength = LightData.GREEN_OFF;
                break;
            case 5:
                this.greenStrength = LightData.GREEN_LOW;
                break;
            case 6:
                this.greenStrength = LightData.GREEN_MEDIUM;
                break;
            case 7:
                this.greenStrength = LightData.GREEN_HIGH;
                break;
        }
        this.updateColorPreview();
    }

    private void updateTopBar() {
        this.midiController.setKey(104, LightData.GREEN_HIGH, LightStatus.STATUS_CONTROL);
        this.midiController.setKey(108, LightData.YELLOW_LOW, LightStatus.STATUS_CONTROL);
        this.midiController.setKey(109, LightData.YELLOW_HIGH, LightStatus.STATUS_CONTROL);
        if(this.currentFrame > 0) {
            this.midiController.setKey(105, LightData.RED_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(105, LightData.OFF, LightStatus.STATUS_CONTROL);
        }
        if(this.currentFrame > 1) {
            this.midiController.setKey(106, LightData.YELLOW_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(106, LightData.OFF, LightStatus.STATUS_CONTROL);
        }
        if(this.currentFrame < buttonAnim.buttonKeyframes.size()) {
            this.midiController.setKey(107, LightData.YELLOW_HIGH, LightStatus.STATUS_CONTROL);
        }
        else {
            this.midiController.setKey(107, LightData.GREEN_HIGH, LightStatus.STATUS_CONTROL);
        }
        this.keyLabel.setText("EditMode: Frame " + this.currentFrame + " of " + buttonAnim.buttonKeyframes.size());
    }
}

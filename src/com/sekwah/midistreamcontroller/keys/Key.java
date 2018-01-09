package com.sekwah.midistreamcontroller.keys;

import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.LightStatus;
import com.sekwah.midistreamcontroller.controller.MidiController;

public abstract class Key {

    protected MidiController controller;
    protected final int x;
    protected final int y;
    protected final LightData defaultColor;

    public Key(MidiController controller, int x, int y, LightData defaultColor) {
        this.controller = controller;
        this.x = x;
        this.y = y;
        this.defaultColor = defaultColor;
    }

    public LightData getDefaultColor() {
        return this.defaultColor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected void runKeys(int... keyCombo) {
        for(int key : keyCombo) {
            this.controller.robot.keyPress(key);
        }
        this.controller.robot.delay(100);
        for(int key : keyCombo) {
            this.controller.robot.keyRelease(key);
        }
    }

    public abstract void run();
}

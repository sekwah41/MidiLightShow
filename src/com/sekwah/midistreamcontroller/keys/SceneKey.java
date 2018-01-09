package com.sekwah.midistreamcontroller.keys;

import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.MidiController;

public abstract class SceneKey extends Key {

    protected int sceneId;

    public SceneKey(MidiController controller, int x, int y, LightData defaultColor, int sceneId) {
        super(controller, x, y, defaultColor);
        this.sceneId = sceneId;
    }
}

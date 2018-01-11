package com.sekwah.midistreamcontroller.lightdata.stored;

public class KeyFrame {
    /**
     * Array of light color codes
     */
    public int[] buttons = new int[8 * 8];

    public int frameLength = 20;

    public KeyFrame copy() {
        KeyFrame keyFrame = new KeyFrame();
        keyFrame.frameLength = frameLength;
        for (int i = 0; i < this.buttons.length; i++) {
            keyFrame.buttons[i] = this.buttons[i];
        }
        return keyFrame;
    }
}

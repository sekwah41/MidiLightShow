package com.sekwah.midistreamcontroller.animation;

import com.sekwah.midistreamcontroller.lightdata.stored.ButtonAnim;

public class AnimationTracker {

    public int currentFrame = 0;
    public ButtonAnim buttonAnim;

    public int timePassed = 0;

    public AnimationTracker(ButtonAnim anim) {
        this.buttonAnim = anim;
    }
}

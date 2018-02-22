package com.sekwah.midistreamcontroller.animation;

import com.sekwah.midistreamcontroller.controller.LightData;
import com.sekwah.midistreamcontroller.controller.LightStatus;
import com.sekwah.midistreamcontroller.controller.MidiController;
import com.sekwah.midistreamcontroller.lightdata.LightPage;
import com.sekwah.midistreamcontroller.lightdata.stored.ButtonAnim;
import com.sekwah.midistreamcontroller.lightdata.stored.KeyFrame;

import java.util.ArrayList;

public class AnimationController {

    private final MidiController midiController;

    private LightPage currentPage = new LightPage();
    private LightPage wantedPage = new LightPage();

    private ArrayList<AnimationTracker> animationTrackers = new ArrayList<>();

    public AnimationController(MidiController midiController) {
        this.midiController = midiController;
    }

    public void updateDisplay(long timePassed) {
        AnimationTracker[] trackers = this.animationTrackers.toArray(new AnimationTracker[0]);
        for(AnimationTracker animTracker : trackers) {
            this.updateAnim(animTracker, timePassed);
        }
        //this.sendDisplayUpdate();
        this.sendRapidUpdate();
    }

    private void sendRapidUpdate() {
        this.midiController.setKey(9, LightData.OFF, LightStatus.STATUS_ON);
        for(int i = 0; i < currentPage.buttons.length; i += 2) {
            this.midiController.sendMessage(146, wantedPage.buttons[i], wantedPage.buttons[i+1]);
        }
    }

    private void sendDisplayUpdate() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int buttonId = x + y * 8;
                if(currentPage.buttons[buttonId] != wantedPage.buttons[buttonId]) {
                    currentPage.buttons[buttonId] = wantedPage.buttons[buttonId];
                    this.midiController.setKey(x + y * 16, wantedPage.buttons[buttonId], LightStatus.STATUS_ON.getValue());
                }
            }
        }
    }

    private void updateAnim(AnimationTracker animTracker, long timePassed) {
        System.out.println(animTracker.currentFrame);
        if(animTracker.currentFrame >= animTracker.buttonAnim.buttonKeyframes.size()) {
            return;
        }
        KeyFrame keyFrame = animTracker.buttonAnim.buttonKeyframes.get(animTracker.currentFrame);
        animTracker.timePassed += timePassed;
        int[] colorData = animTracker.buttonAnim.buttonKeyframes.get(animTracker.currentFrame).buttons;
        for (int i = 0; i < colorData.length; i++) {
            if(colorData[i] != 0) {
                this.wantedPage.buttons[i] = colorData[i];
                this.wantedPage.causes[i] = animTracker;
            }
        }
        if(keyFrame.frameLength <= animTracker.timePassed) {
            animTracker.timePassed -= keyFrame.frameLength;
            animTracker.currentFrame++;
            int[] nextFrame;
            if(animTracker.buttonAnim.buttonKeyframes.size() - 1 < animTracker.currentFrame) {
                this.animationTrackers.remove(animTracker);
                nextFrame = new int[8*8];
            }
            else {
                nextFrame = animTracker.buttonAnim.buttonKeyframes.get(animTracker.currentFrame).buttons;
            }
            for (int i = 0; i < nextFrame.length; i++) {
                if(nextFrame[i] == 0 && this.wantedPage.causes[i] == animTracker) {
                    this.wantedPage.buttons[i] = nextFrame[i];
                    this.wantedPage.causes[i] = animTracker;
                }
            }
            this.updateAnim(animTracker, 0);
        }
    }

    public void addAnimation(ButtonAnim anim) {
        if(anim.buttonKeyframes.size() > 0) {
            AnimationTracker tracker = new AnimationTracker(anim);
            this.animationTrackers.add(tracker);
            int[] colorData = anim.buttonKeyframes.get(0).buttons;
            for (int i = 0; i < colorData.length; i++) {
                if(colorData[i] != 0) {
                    this.wantedPage.buttons[i] = colorData[i];
                    this.wantedPage.causes[i] = tracker;
                }
            }
        }
    }

    public void cancelAndClearAnimations() {
        for(int i = 0; i < wantedPage.buttons.length; i++) {
            wantedPage.buttons[i] = 0;
        }
        this.updateDisplay(0);
    }
}

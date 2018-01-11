package com.sekwah.midistreamcontroller.lightdata.stored;

/**
 * List of animations on the page
 */
public class PageButtons {
    public ButtonAnim[] buttons = new ButtonAnim[8*8];

    public PageButtons() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new ButtonAnim();
        }
    }
}

package com.sekwah.midistreamcontroller.lightdata;

import com.sekwah.midistreamcontroller.lightdata.stored.ButtonState;

public class ButtonInfo {

    public ButtonState buttonState = new ButtonState();

    public ButtonInfo nextInfo = null;

    public int timeToNextUpdate = 0;
}

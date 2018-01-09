package com.sekwah.midistreamcontroller.controller;

public enum LightStatus {

    STATUS_OFF(0x80), // type, key, vel
    STATUS_ON(0x90), // type, key, vel
    STATUS_CONTROL(0xB0); // type, controller, data


    private final int value;

    LightStatus(int value) {
        this.value = value;
    }

    /**
     * Returns the value of this {@code Integer} as an
     * {@code int}.
     */
    public int intValue() {
        return value;
    }

    public int getValue(){
        return this.value;
    }
}

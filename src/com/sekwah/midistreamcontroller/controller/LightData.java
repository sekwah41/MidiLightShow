package com.sekwah.midistreamcontroller.controller;

/**
 * Should probably split into more files for better control over values but meh.
 */
public enum LightData {
    OFF(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3),

    RED_OFF(OFF.getValue()),
    RED_LOW(LOW.getValue()),
    RED_MEDIUM(MEDIUM.getValue()),
    RED_HIGH(HIGH.getValue()),

    GREEN_OFFSET(16),
    GREEN_OFF(OFF.getValue()),
    GREEN_LOW(LOW.getValue() * GREEN_OFFSET.getValue()),
    GREEN_MEDIUM(MEDIUM.getValue() * GREEN_OFFSET.getValue()),
    GREEN_HIGH(HIGH.getValue() * GREEN_OFFSET.getValue()),

    YELLOW_OFF(OFF.getValue()),
    YELLOW_LOW(RED_LOW.getValue() + GREEN_LOW.getValue()),
    YELLOW_MEDIUM(RED_MEDIUM.getValue() + GREEN_MEDIUM.getValue()),
    YELLOW_HIGH(RED_HIGH.getValue() + GREEN_HIGH.getValue()),;

    private final int value;

    LightData(int value) {
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

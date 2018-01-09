package com.sekwah.midistreamcontroller.controller;

import com.sekwah.midistreamcontroller.ControllerWindow;

import javax.sound.midi.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sekwah on 21/6/2016.
 */
public class MidiController {

    // Documentation for messages
    // https://d19ulaff0trnck.cloudfront.net/sites/default/files/novation/downloads/4080/launchpad-programmers-reference.pdf

    /*public final static int STATUS_OFF     = 0x80; // type, key, vel
    public final static int STATUS_ON      = 0x90; // type, key, vel
    public final static int STATUS_CONTROL = 0xB0; // type, controller, data*/

    // Control values
    public final static int CON_NIL       = 0x00; // type, controller, data

    // Add buffer or flash to the values to change the note behaviours. Still need to understand flashing.
    // Look at the set grid LEDs for this.
    public final static int BUFFER        =  12; // updates the LED for the current update_buffer only
    public final static int FLASH         =   8; // flashing updates the LED for flashing  - the new value will be written to buffer 0 while the LED will be off in buffer 1
    public Robot robot;

    private List<Receiver> outputRecievers = new ArrayList<Receiver>();

    private List<Receiver> midiRecievers = new ArrayList<Receiver>();

    private String deviceNameContains = "Launchpad";

    private ControllerWindow window;

    public MidiController(ControllerWindow window) {

        this.window = window;

        MidiDevice device;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.exit(0);
        }

        for (int i = 0; i < infos.length; i++) {

            try {
                device = MidiSystem.getMidiDevice(infos[i]);
                device.open();
                //does the device have any transmitters?
                //if it does, add it to the device list
                if(infos[i].getName().contains(deviceNameContains)){
                    System.out.println(infos[i] + " - Wanted device found!!!");


                    //get all transmitters
                    List<Transmitter> transmitters = device.getTransmitters();
                    //and for each transmitter

                    try{
                        if(device.getReceiver() != null){
                            midiRecievers.add(device.getReceiver());
                            System.out.println(infos[i] + " - Got reciever");
                        }
                        else{
                            System.out.println(infos[i] + " - Reciever not found");
                        }
                    }
                    catch (MidiUnavailableException e) {
                        System.out.println(infos[i] + " - Reciever error");
                        //e.printStackTrace();
                        //continue;
                    }

                    for(int j = 0; j<transmitters.size();j++) {
                        //create a new receiver
                        transmitters.get(j).setReceiver(
                                //using my own MidiInputReceiver
                                new MidiInputReceiver(device.getDeviceInfo().toString(), this.window)
                        );

                        System.out.println(infos[i] + " - Set Transmitter " + j);
                    }

                    Transmitter trans = device.getTransmitter();
                    trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString(), this.window));

                    //open each device
                    device.open();

                    //if code gets this far without throwing an exception
                    //print a success message
                    System.out.println(device.getDeviceInfo()+" - Was Opened");
                }
                else{
                    System.out.println(infos[i]);
                }
            } catch (MidiUnavailableException e) {
            }
        }

        // clears teh launchpad
        this.clearLaunchpad();

        // Sets the flash speed
        //sendMessage(STATUS_CONTROL, CON_NIL, 40);
    }

    public void clearLaunchpad() {
        this.sendMessage(LightStatus.STATUS_CONTROL.getValue(), CON_NIL, CON_NIL);
    }

    /**
     * Data 1 is the button pressed. Data 2 is the velocity (0 out 127 pressing (full velo) )
     */
    public class MidiInputReceiver implements Receiver {
        public String name;
        private ControllerWindow window;

        public MidiInputReceiver(String name, ControllerWindow window) {
            this.name = name;
            this.window = window;
        }
        public void send(MidiMessage msg, long timeStamp) {
            // Message status 144 is normal buttons 176 is the rop row of functions.
            //System.out.println(name + "midi received: " + msg.getMessage());
            // Possibly look at http://launchpaddr.com/ for the info it sends
            // It is just a data array, data[0] (unsure about data sent...) data[1] data[2]
            byte[] data = msg.getMessage();

            if(data.length != 3) {
                return;
            }

            System.out.println(name + " midi received: " + data[1] + ":" + data[2] + ":" + data[0]);

            // data[0] -112 is normal key
            // -80 is control button at the top
            int y = data[1] / 16;
            int x = data[1] % 16;

            if(data[2] == 127) {
                this.window.runKey(x,y);
            }
        }
        public void close() {}
    }

    public void setKey(int key, int color, int status){
        this.sendMessage(status, key, color);
    }

    public void setGrid(int x, int y, LightData color, LightStatus status){
        x -= 1; y -= 1;
        int key = (16 * y) + x;
        this.sendMessage(status.getValue(), key, color.getValue());
    }

    public void sendMessage(int... data){
        ShortMessage newMessage = new ShortMessage();
        try {
            //newMessage.setMessage(STATUS_ON, shortMessage.getData1(), RED_HIGH);
            newMessage.setMessage(data[0], data[1], data[2]);
            for(Receiver reciever : midiRecievers){
                //System.out.println("Message sent ");
                reciever.send(newMessage, 0);
            }
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
}

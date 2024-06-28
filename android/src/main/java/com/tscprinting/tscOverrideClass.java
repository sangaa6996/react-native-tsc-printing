package com.tscprinting;

import android.os.StrictMode;
import android.util.Log;

import com.example.tscdll.TscWifiActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class tscOverrideClass extends TscWifiActivity{
    public Socket socket = null;
    public InputStream InStream = null;
    public OutputStream OutStream = null;
    public int port_connected = 0;

    @Override
    public String openport(String ipaddress, int portnumber) {
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(ipaddress, portnumber), 2000);
            this.InStream = this.socket.getInputStream();
            this.OutStream = this.socket.getOutputStream();
            this.port_connected = 1;
        } catch (Exception var7) {
            try {
                this.socket.close();
            } catch (IOException var5) {
                this.port_connected = 0;
                return "-2";
            }

            this.port_connected = 0;
            return "-1";
        }

        try {
            Thread.sleep(100L);
        } catch (InterruptedException var6) {
            var6.printStackTrace();
        }

        return "1";
    }

    @Override
    public String setup(int width, int height, int speed, int density, int sensor, int sensor_distance, int sensor_offset) {
        String message = "";
        String size = "SIZE " + width + " mm" + ", " + height + " mm";
        String speed_value = "SPEED " + speed;
        String density_value = "DENSITY " + density;
        String sensor_value = "";
        if (sensor == 0) {
            sensor_value = "GAP " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        } else if (sensor == 1) {
            sensor_value = "BLINE " + sensor_distance + " mm" + ", " + sensor_offset + " mm";
        }

        message = size + "\r\n" + speed_value + "\r\n" + density_value + "\r\n" + sensor_value + "\r\n";
        byte[] msgBuffer = message.getBytes();

        try {
            this.OutStream.write(msgBuffer);
            return "1";
        } catch (IOException var15) {
            return "-1";
        }
    }

    @Override
    public String sendcommand(String message) {
        if (this.port_connected == 0) {
            return "-1";
        } else {
            byte[] msgBuffer = message.getBytes();

            try {
                this.OutStream.write(msgBuffer);
                return "1";
            } catch (IOException var4) {
                return "-1";
            }
        }
    }

    @Override
    public String closeport(int timeout) {
        try {
            Thread.sleep((long)timeout);
        } catch (InterruptedException var5) {
            var5.printStackTrace();
        }

        if (this.port_connected == 0) {
            return "-1";
        } else {
            try {
                this.socket.close();
            } catch (IOException var4) {
                return "-1";
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            }

            return "1";
        }
    }
}

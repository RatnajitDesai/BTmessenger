package rd.sqllitepractice.btmessenger.Models;

import android.bluetooth.BluetoothDevice;

public class BTDevice {

    private boolean isPaired;
    private BluetoothDevice BluetoothDevice;

    public BTDevice(BluetoothDevice device, boolean isPaired) {
        BluetoothDevice = device;
        this.isPaired = isPaired;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public void setPaired(boolean paired) {
        isPaired = paired;
    }

    public BluetoothDevice getBluetoothDevice() {
        return BluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.BluetoothDevice = mBluetoothDevice;
    }
}

package com.example.vucuong12.kaistchicken;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;



/**
 * Created by vucuong12 on 11/14/16.
 */
public abstract class WifiListener {
    private static final String TAG = "WifiListener";
    public Context myContext = null;
    public BroadcastReceiver conReceiver = null;
    public BroadcastReceiver disConReceiver = null;
    public BroadcastReceiver mReceiver;
    static String lastState = null;
    static String MAC = null;
    static String IP = null;

    public WifiListener(Context context){
        Log.d(TAG, "Constructor of WifiListener");
        myContext = context;
    }


    public abstract void WifiConnectionHandler(String connectionType,String MAC, String IP);

    public void receive(){
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("android.net.wifi.STATE_CHANGE");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    NetworkInfo networkInfo =
                            intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if(networkInfo.isConnected() && (lastState == null || lastState.equals("DISCONNECTED"))) {
                        // Wifi is connected
                        lastState = "CONNECTED";
                        Bundle extras = intent.getExtras();
                        Log.d(TAG, extras.toString());
                        WifiInfo wifiInfo = (WifiInfo) extras.get("wifiInfo");
                        MAC =  wifiInfo.getBSSID();

                        IP = routerIP(context);

                        WifiConnectionHandler("WIFI_CONNECTED", MAC, IP);
                    }
                    if(networkInfo.getState() == NetworkInfo.State.DISCONNECTED && (lastState == null ||lastState.equals("CONNECTED"))) {
                        // Wifi is disconnected
                        lastState = "DISCONNECTED";
                        WifiConnectionHandler("WIFI_DISCONNECTED", MAC, IP);
                    }
                }

            }
        };

        myContext.registerReceiver(mReceiver, mFilter);
    }

    private boolean checkWifiOnAndConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    protected String wifiIpAddress(int ipAddress) {
        //WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

    protected String routerIP(Context context){
        final WifiManager manager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        final String address = wifiIpAddress(dhcp.gateway);

        return address;
    }


}

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.og.tracerouteping.uitl;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.util.Log;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.og.tracerouteping.ui.TraceActivity;




public class Utils {

	public static  String launchPing(String url) throws Exception {
		// Build ping command with parameters
		Process p;
		String command = "ping -c 10 ";

		// Launch command
		p = Runtime.getRuntime().exec(command + url);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		// Construct the response from ping
		String s;
		String res = "";
		
		while ((s = stdInput.readLine()) != null) {
			res = res + s + "\n";
		}

		p.destroy();


		return res;
	}
	
	
	public static  String launchCmd(String cmd) throws Exception {
		// Build ping command with parameters
		Process p;
		// Launch command
		p = Runtime.getRuntime().exec(cmd);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		// Construct the response from ping
		String s;
		String res = "";
				
		while ((s = stdInput.readLine()) != null) {
			res = res + s + "\n";
		}

		p.destroy();
		return res;
	}
	
	
	/**
	 * 获取网络信号以及接入方式
	 */
	@SuppressLint("NewApi")
	public static  String getApnAndSignalStrength(TraceActivity context) throws Exception {
		
		String networkType = "";
		String signalStrength = "";
		
		ConnectivityManager conManager = 
		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = conManager.getActiveNetworkInfo(); 


		
		if (ni != null
				&& ni.getType() == ConnectivityManager.TYPE_WIFI) {
			networkType = "wifi";
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			int linkSpeed = wifiManager.getConnectionInfo().getRssi();
			signalStrength = String.valueOf(linkSpeed);
		}else
		{
			networkType = ni.getExtraInfo();
			TelephonyManager telephonyManager =        (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
			CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
			signalStrength = String.valueOf(cellSignalStrengthGsm.getDbm());
		}
		Log.i("xxxxxx",networkType + " : " + signalStrength);
		return networkType + " : " + signalStrength;
		
	}
	
	

	
	/**
	 * Check for connectivity (wifi and mobile)
	 * 
	 * @return true if there is a connectivity, false otherwise
	 */
	public boolean hasConnectivity(TraceActivity context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}





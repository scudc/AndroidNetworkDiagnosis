/*
This file is part of the project TraceroutePing, which is an Android library
implementing Traceroute with ping under GPL license v3.
Copyright (C) 2013  Olivier Goutay

TraceroutePing is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

TraceroutePing is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with TraceroutePing.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.og.tracerouteping.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.og.tracerouteping.R;
import com.og.tracerouteping.network.NetworkContainer;
import com.og.tracerouteping.network.TracerouteContainer;
import com.og.tracerouteping.network.TracerouteWithPing;
import com.og.tracerouteping.uitl.Utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

/**
 * The main activity
 * 
 * @author Olivier Goutay
 * 
 */
public class TraceActivity extends Activity {

	public static final String tag = "TraceroutePing";
	public static final String INTENT_TRACE = "INTENT_TRACE";

	private Button buttonLaunch;
	private EditText editTextPing;
	private ProgressBar progressBarPing;
	private ListView listViewTraceroute;
	private TraceListAdapter traceListAdapter;
	
	
	private ListView listViewNetwork;
	private NetworkListAdapter networkListAdapter;

	private TracerouteWithPing tracerouteWithPing;
	private final int maxTtl = 40;

	private List<TracerouteContainer> traces;
	
	//保存网络信息的数据接口
	private List<NetworkContainer> networkList;

	/**
	 * onCreate, init main components from view
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);

		this.tracerouteWithPing = new TracerouteWithPing(this);
		this.traces = new ArrayList<TracerouteContainer>();
		this.networkList = new ArrayList<NetworkContainer>();

		this.buttonLaunch = (Button) this.findViewById(R.id.buttonLaunch);
		this.editTextPing = (EditText) this.findViewById(R.id.editTextPing);
		this.listViewTraceroute = (ListView) this.findViewById(R.id.listViewTraceroute);
		this.progressBarPing = (ProgressBar) this.findViewById(R.id.progressBarPing);
		this.listViewNetwork = (ListView) this.findViewById(R.id.listViewNetwork);
		
		
		initView();
		
	}

	/**
	 * initView, init the main view components (action, adapter...)
	 */
	private void initView() {
		buttonLaunch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editTextPing.getText().length() == 0) {
					Toast.makeText(TraceActivity.this, getString(R.string.no_text), Toast.LENGTH_SHORT).show();
				} else {
					startProgressBar();
					hideSoftwareKeyboard(editTextPing);
					tracerouteWithPing.executeTraceroute(editTextPing.getText().toString(), maxTtl);
					
					/**
					 * 调用相关获取网络信息的函数
					 */
					
					new Thread(new Runnable() {
						@Override
						public void run() {	
							String domain = editTextPing.getText().toString();
							String cmd = "";
							String result = "";
							cmd = "ping -c 10 " + domain;
							try {
								result = Utils.launchCmd(cmd);
								networkList.add(new NetworkContainer(cmd,result));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							/*
							cmd = "nslookup "+domain;
							try {
								result = Utils.launchCmd(cmd);
								networkList.add(new NetworkContainer(cmd,result));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							
							cmd = "Network Info";
							try {
								result = Utils.getApnAndSignalStrength(getApplicationContext());
								networkList.add(new NetworkContainer(cmd,result));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}).start();
					
				}
			}
		});

		traceListAdapter = new TraceListAdapter(getApplicationContext());
		listViewTraceroute.setAdapter(traceListAdapter);
		
		this.networkListAdapter = new NetworkListAdapter(getApplicationContext());
		listViewNetwork.setAdapter(networkListAdapter);
		
		
	}

	/**
	 * Allows to refresh the listview of traces
	 * 
	 * @param traces
	 *            The list of traces to refresh
	 */
	public void refreshList(List<TracerouteContainer> traces) {
		this.traces = traces;
		traceListAdapter.notifyDataSetChanged();
	}

	/**
	 * The adapter of the listview (build the views)
	 */
	public class TraceListAdapter extends BaseAdapter {

		private Context context;

		public TraceListAdapter(Context c) {
			context = c;
		}

		public int getCount() {
			return traces.size();
		}

		public TracerouteContainer getItem(int position) {
			return traces.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			// first init
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.item_list_trace, null);

				TextView textViewNumber = (TextView) convertView.findViewById(R.id.textViewNumber);
				TextView textViewIp = (TextView) convertView.findViewById(R.id.textViewIp);
				TextView textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
				ImageView imageViewStatusPing = (ImageView) convertView.findViewById(R.id.imageViewStatusPing);

				// Set up the ViewHolder.
				holder = new ViewHolder();
				holder.textViewNumber = textViewNumber;
				holder.textViewIp = textViewIp;
				holder.textViewTime = textViewTime;
				holder.imageViewStatusPing = imageViewStatusPing;

				// Store the holder with the view.
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			TracerouteContainer currentTrace = getItem(position);

			if (position % 2 == 1) {
				convertView.setBackgroundResource(R.drawable.table_odd_lines);
			} else {
				convertView.setBackgroundResource(R.drawable.table_pair_lines);
			}

			if (currentTrace.isSuccessful()) {
				holder.imageViewStatusPing.setImageResource(R.drawable.check);
			} else {
				holder.imageViewStatusPing.setImageResource(R.drawable.cross);
			}

			holder.textViewNumber.setText(position + "");
			holder.textViewIp.setText(currentTrace.getHostname() + " (" + currentTrace.getIp() + ")");
			holder.textViewTime.setText(currentTrace.getMs() + "ms");

			return convertView;
		}

		// ViewHolder pattern
		class ViewHolder {
			TextView textViewNumber;
			TextView textViewIp;
			TextView textViewTime;
			ImageView imageViewStatusPing;
		}
	}
	
	/**
	 * The adapter of the listview (build the views)
	 */
	public class NetworkListAdapter extends BaseAdapter {

		private Context context;

		public NetworkListAdapter(Context c) {
			context = c;
		}

		public int getCount() {
			return networkList.size();
		}

		public NetworkContainer getItem(int position) {
			return networkList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			// first init
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.item_list_network, null);


				TextView textViewCmd = (TextView) convertView.findViewById(R.id.networkType);
				TextView textViewResults = (TextView) convertView.findViewById(R.id.networkContent);
				
				// Set up the ViewHolder.
				holder = new ViewHolder();
				holder.textViewCmd = textViewCmd;
				holder.textViewResult = textViewResults;

				// Store the holder with the view.
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			NetworkContainer currentNetwork = getItem(position);

			

			Log.i("xxxxx",currentNetwork.getCmdName());

			holder.textViewCmd.setText(currentNetwork.getCmdName());
			holder.textViewResult.setText(currentNetwork.getCmdResult());

			return convertView;
		}

		// ViewHolder pattern
		class ViewHolder {
			TextView textViewCmd;
			TextView textViewResult;
		}
	}
	

	/**
	 * Hides the keyboard
	 * 
	 * @param currentEditText
	 *            The current selected edittext
	 */
	public void hideSoftwareKeyboard(EditText currentEditText) {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(currentEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void startProgressBar() {
		progressBarPing.setVisibility(View.VISIBLE);
	}

	public void stopProgressBar() {
		progressBarPing.setVisibility(View.GONE);
	}

}

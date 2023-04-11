package de.plinzen.android.rttmanager.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.plinzen.android.rttmanager.R;

class WifiNetworkAdapter extends RecyclerView.Adapter<WifiNetworkAdapter.ViewHolder> {

    private static final String TAG = "CUSTOM";
    private static final HashSet<String> TARGETS = new HashSet<String>(){{
        add("38:8b:59:d2:8f:32");
        add("b0:6a:41:dc:c5:f0");
        add("58:cb:52:d1:81:48");
        add("58:cb:52:d1:81:b6");
        add("3c:28:6d:86:2b:4c");
        add("58:cb:52:d1:82:87");
        add("b0:6a:41:87:62:5c");
        add("b0:6a:41:87:63:a6");
        add("b0:6a:41:87:69:26");
        add("3c:28:6d:86:33:55");
        add("b0:6a:41:87:65:95");
        add("b0:6a:41:87:66:2f");

    }};
    interface OnClickListener {
        void onItemClicked(final View view, final ScanResult wifiNetwork);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.wifi_network_item)
        RelativeLayout wifi_network_item;

        @BindView(R.id.checkBox)
        CheckBox checkBox;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private final Context context;
    private List<ScanResult> wifiNetworks;
    private OnClickListener clickListener;

    WifiNetworkAdapter(final Context context) {
        this.context = context;
        wifiNetworks = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_network, parent,
                false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position >= wifiNetworks.size()) {
            return;
        }
        final ScanResult result = wifiNetworks.get(position);
        holder.checkBox.setText(result.SSID + "/" + result.BSSID);
        holder.checkBox.setChecked(MainActivity.set.get(result.BSSID) != null ? true : false);
        bind(holder.wifi_network_item, result, clickListener);
    }

    public void setClickListener(final OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        if (wifiNetworks == null) {
            return 0;
        }
        return wifiNetworks.size();
    }

    void setWifiNetworks(final List<ScanResult> wifiNetworks) {
        if (wifiNetworks == null) {
            return;
        }
        final List<String> duplicates = new ArrayList<>(wifiNetworks.size());
        final List<ScanResult> filteredResults = new ArrayList<>(wifiNetworks.size());
        final List<String> filtered = new ArrayList<>(wifiNetworks.size());

        for (ScanResult result : wifiNetworks) {
            String id = String.format("%s/%s", result.SSID, result.BSSID);
            if (result.SSID.contains("802.11mc") || true) {
                duplicates.add(id);
                filtered.add(id);
            }
            if(TARGETS.contains(result.BSSID)){
                MainActivity.set.put(result.BSSID,result);
            }

        }

        Collections.sort(filtered);
        for (String item : filtered) {
            ScanResult result = wifiNetworks.stream()
                    .filter(r -> item.equals(String.format("%s/%s", r.SSID, r.BSSID)))
                    .findFirst()
                    .orElse(null);

            if (result != null) {

                filteredResults.add(result);

            }
        }
        Toast.makeText(context, "Found "+filteredResults.size()+" APs", Toast.LENGTH_SHORT).show();
        this.wifiNetworks = filteredResults;
        notifyDataSetChanged();
    }


    private void bind(final View wifiNetworkItem, final ScanResult result, final OnClickListener listener) {
        Log.d(TAG, "bind: ");
        wifiNetworkItem.setOnClickListener(view -> listener.onItemClicked(wifiNetworkItem.findViewById(R.id.checkBox), result));
    }
}

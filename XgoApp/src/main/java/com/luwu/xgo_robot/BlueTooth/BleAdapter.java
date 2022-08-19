package com.luwu.xgo_robot.BlueTooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luwu.xgo_robot.R;

import java.util.List;

public class BleAdapter extends ArrayAdapter<BleDeviceEntity> {

    private Context context;
    private int resourceId;
    private List<BleDeviceEntity> list;


    public BleAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<BleDeviceEntity> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
        this.list = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BleDeviceEntity bluetoothEntity = getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.ble_name);
            viewHolder.mac = convertView.findViewById(R.id.ble_macId);
            viewHolder.rssi = convertView.findViewById(R.id.ble_rssi);
            viewHolder.imageView = convertView.findViewById(R.id.ble_rssi_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(bluetoothEntity.getName());
        viewHolder.mac.setText(bluetoothEntity.getMac());
        viewHolder.rssi.setText(bluetoothEntity.getRssi() + "");

        if (bluetoothEntity.getRssi() >= -55) {
            viewHolder.imageView.setImageResource(R.drawable.ble_level5);
        } else if (bluetoothEntity.getRssi() >= -70 && bluetoothEntity.getRssi() < -55) {
            viewHolder.imageView.setImageResource(R.drawable.ble_level4);
        } else if (bluetoothEntity.getRssi() >= -85 && bluetoothEntity.getRssi() < -70) {
            viewHolder.imageView.setImageResource(R.drawable.ble_level3);
        } else if (bluetoothEntity.getRssi() >= -100 && bluetoothEntity.getRssi() < -85) {
            viewHolder.imageView.setImageResource(R.drawable.ble_level2);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.ble_level1);
        }
        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView mac;
        TextView rssi;
        ImageView imageView;
    }
}


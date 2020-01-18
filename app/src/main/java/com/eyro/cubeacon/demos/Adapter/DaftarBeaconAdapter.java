package com.eyro.cubeacon.demos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eyro.cubeacon.demos.Model.BeaconModel;
import com.eyro.cubeacon.demos.R;

import java.util.ArrayList;
import java.util.List;

public class DaftarBeaconAdapter extends ArrayAdapter<BeaconModel> {

    private ArrayList<BeaconModel> beaconModels;
    Context context;

    public DaftarBeaconAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BeaconModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.beaconModels = objects;
    }

    class Viewholder{
        TextView tnama;
        TextView tUUID;
        TextView tmajor;
        TextView tminor;
        TextView trange;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Viewholder holder;
        BeaconModel beaconModel = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_beacon, parent, false);
            holder = new Viewholder();
            holder.tnama = convertView.findViewById(R.id.txtNama);
            holder.tUUID = convertView.findViewById(R.id.txtID);
            holder.tmajor = convertView.findViewById(R.id.txtMajor);
            holder.tminor = convertView.findViewById(R.id.txtMinor);
            holder.trange = convertView.findViewById(R.id.txtRange);
            convertView.setTag(holder);
        } else
            holder = (Viewholder)convertView.getTag();

        if (beaconModel != null){
            holder.tnama.setText(beaconModel.getNamaBeacon());
            holder.tUUID.setText(beaconModel.getUUID());
            holder.tmajor.setText(beaconModel.getMajor());
            holder.tminor.setText(beaconModel.getMinor());
            holder.trange.setText(beaconModel.getRange());
        }

        return convertView;
    }
}

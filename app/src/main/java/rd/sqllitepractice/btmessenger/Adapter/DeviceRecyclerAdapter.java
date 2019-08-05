package rd.sqllitepractice.btmessenger.Adapter;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import rd.sqllitepractice.btmessenger.Models.BTDevice;
import rd.sqllitepractice.btmessenger.R;

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceRecyclerAdapter.ViewHolder>{

    public interface OnDeviceListener{
        void onDeviceClick(int position);
    }

    private static final String TAG = "DeviceRecyclerAdapter";

    private ArrayList<BTDevice> mBtDevices = new ArrayList<>();
    private OnDeviceListener mOnDeviceListener;

    public DeviceRecyclerAdapter(ArrayList<BTDevice> mBtDevices, OnDeviceListener deviceListener) {
        mOnDeviceListener = deviceListener;
        this.mBtDevices = mBtDevices;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView deviceName, paired;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            deviceName = itemView.findViewById(R.id.device_name);
            paired = itemView.findViewById(R.id.device_paired);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mOnDeviceListener.onDeviceClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_devices_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try{
            String deviceName = mBtDevices.get(position).getBluetoothDevice().getName();
            Boolean isPaired = mBtDevices.get(position).isPaired();

            holder.deviceName.setText(deviceName);

            if (isPaired == true)
            {
                holder.paired.setText("Paired");
            }
            else {
                holder.paired.setText("Unpaired");
            }

        }
        catch (NullPointerException e)
        {
            Log.e(TAG, "onBindViewHolder: NullPointerException"+ e.getMessage() );
        }


    }

    @Override
    public int getItemCount() {
        return mBtDevices.size();
    }

}

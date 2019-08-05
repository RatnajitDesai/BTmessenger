package rd.sqllitepractice.btmessenger.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import rd.sqllitepractice.btmessenger.Models.Message;
import rd.sqllitepractice.btmessenger.R;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "MessagesRecyclerAdapter";
    private ArrayList<Message> messages = new ArrayList<>();

    public MessagesRecyclerAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView sent_message, sent_timestamp, received_message, received_timestamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sent_message = itemView.findViewById(R.id.tvSent_message);
            sent_timestamp = itemView.findViewById(R.id.sent_message_time);
            received_message = itemView.findViewById(R.id.tvReceivedMessage);
            received_timestamp = itemView.findViewById(R.id.received_message_time);

        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.snippet_sent_message , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String msg = messages.get(position).getMessage();
        Log.d(TAG, "onBindViewHolder: MESSAGE :"+msg);
        String timestamp = messages.get(position).getTimeStamp();
        try
        {
            if (messages.get(position).isIs_sender()){

                holder.sent_message.setText(msg);
                holder.sent_timestamp.setText(timestamp);
                holder.received_message.setVisibility(View.GONE);
                holder.received_timestamp.setVisibility(View.GONE);

            }
            else {
                holder.received_message.setText(msg);
                holder.received_timestamp.setText(timestamp);
                holder.sent_message.setVisibility(View.GONE);
                holder.sent_timestamp.setVisibility(View.GONE);
            }

        }
        catch (NullPointerException e)
        {
            Log.e(TAG, "onBindViewHolder: NullPointerException: "+e.getMessage() );
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

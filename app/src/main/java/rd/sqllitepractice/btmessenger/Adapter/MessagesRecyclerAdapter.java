package rd.sqllitepractice.btmessenger.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import rd.sqllitepractice.btmessenger.Models.Message;
import rd.sqllitepractice.btmessenger.R;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MessagesRecyclerAdapter";
    private static final int IMAGE = 1;
    private static final int PLAIN_TEXT = 0;
    private ArrayList<Message> messages = new ArrayList<>();

    public MessagesRecyclerAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMAGE) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.snippet_sent_image, parent, false);
            return new ImageViewHolder(view);
        } else if (viewType == PLAIN_TEXT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.snippet_sent_message, parent, false);
            return new MessageViewHolder(view);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isImage()) {
            return IMAGE;
        } else {
            return PLAIN_TEXT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == IMAGE) {
            String msg = messages.get(position).getMessage();
            Log.d(TAG, "onBindViewHolder: MESSAGE :" + msg);
            String timestamp = messages.get(position).getTimeStamp();
            try {
                ImageViewHolder image_holder = (ImageViewHolder) holder;
                if (messages.get(position).isIs_sender()) {

//                    image_holder.sent_image.setImageBitmap(messages.get(position).getBitmap());

                    Glide.with(image_holder.mContext)
                            .load(messages.get(position).getBitmap())
                            .placeholder(R.drawable.ic_close)
                            .into(image_holder.sent_image);
                    image_holder.sent_timestamp.setText(timestamp);
                    image_holder.received_image_relative.setVisibility(View.GONE);
                    image_holder.received_timestamp.setVisibility(View.GONE);

                } else {
//                    image_holder.received_image.setImageBitmap(messages.get(position).getBitmap());

                    Glide.with(image_holder.mContext)
                            .load(messages.get(position).getBitmap())
                            .error(R.drawable.ic_close)
                            .into(image_holder.received_image);

                    image_holder.received_timestamp.setText(timestamp);
                    image_holder.sent_image_relative.setVisibility(View.GONE);
                    image_holder.sent_timestamp.setVisibility(View.GONE);
                }

            } catch (NullPointerException e) {
                Log.e(TAG, "onBindViewHolder: NullPointerException: " + e.getMessage());
            }


        } else if (getItemViewType(position) == PLAIN_TEXT) {


            String msg = messages.get(position).getMessage();
            Log.d(TAG, "onBindViewHolder: MESSAGE :" + msg);
            String timestamp = messages.get(position).getTimeStamp();
            try {
                MessageViewHolder message_holder = (MessageViewHolder) holder;
                if (messages.get(position).isIs_sender()) {
                    message_holder.sent_message.setText(msg);
                    message_holder.sent_timestamp.setText(timestamp);
                    message_holder.received_message.setVisibility(View.GONE);
                    message_holder.received_timestamp.setVisibility(View.GONE);

                } else {
                    message_holder.received_message.setText(msg);
                    message_holder.received_timestamp.setText(timestamp);
                    message_holder.sent_message.setVisibility(View.GONE);
                    message_holder.sent_timestamp.setVisibility(View.GONE);
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "onBindViewHolder: NullPointerException: " + e.getMessage());
            }
        }

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView sent_message, sent_timestamp, received_message, received_timestamp;
        Context mContext;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sent_message = itemView.findViewById(R.id.tvSent_message);
            sent_timestamp = itemView.findViewById(R.id.sent_message_time);
            received_message = itemView.findViewById(R.id.tvReceivedMessage);
            received_timestamp = itemView.findViewById(R.id.received_message_time);
            mContext = itemView.getContext();

        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView sent_image, received_image;
        RelativeLayout sent_image_relative, received_image_relative;
        TextView sent_timestamp, received_timestamp;
        Context mContext;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            sent_image = itemView.findViewById(R.id.sent_image);
            sent_timestamp = itemView.findViewById(R.id.sent_image_time);
            sent_image_relative = itemView.findViewById(R.id.ivSentImage);
            received_image = itemView.findViewById(R.id.received_image);
            received_timestamp = itemView.findViewById(R.id.received_image_time);
            received_image_relative = itemView.findViewById(R.id.ivReceivedImage);
            mContext = itemView.getContext();

        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }
}

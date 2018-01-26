package io.gresse.hugo.tp3;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.List;

/**
 * Display chat messages
 * <p>
 * Created by Hugo Gresse on 26/11/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Listener mListener;
    private List<Message> mData;
    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 0;
    private User user;

    public MessageAdapter(Listener listener, List<Message> data, User user) {
        mListener = listener;
        mData = data;
        this.user = user;
    }

    public void setData(List<Message> data) {
        mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_messages, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_receved_messages, parent, false);
        }
        return new ViewHolder(view);
    }

    public int getItemViewType(int position){
        if(mData.get(position).userEmail.equals(user.email)){
            return TYPE_SENT;
        }
        else {
            return TYPE_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ImageView mUserImageView;
        TextView  mUserTextView;
        TextView  mContentTextView;
        RelativeTimeTextView time;

        ViewHolder(View itemView) {
            super(itemView);

            //itemView.setOnClickListener(this);
            mUserImageView = itemView.findViewById(R.id.userImageView);
            mUserTextView = itemView.findViewById(R.id.userTextView);
            mContentTextView = itemView.findViewById(R.id.contentTextView);
            time = (RelativeTimeTextView)itemView.findViewById(R.id.timestamp); //Or just use Butterknife!
            itemView.setOnLongClickListener((View.OnLongClickListener) this);

        }

        void setData(Message message) {
            time.setReferenceTime(message.timestamp);
            mUserTextView.setText(message.userName + ": ");
            mContentTextView.setText(message.content);



            if (!TextUtils.isEmpty(message.userEmail)) {
                Glide
                        .with(mUserImageView.getContext())
                        .load(Constant.GRAVATAR_PREFIX + Utils.md5(message.userEmail))
                        .apply(RequestOptions.circleCropTransform())
                        .into(mUserImageView);
            } else {
                mUserImageView.setImageResource(R.color.colorAccent);
            }
        }

        @Override
        public boolean onLongClick(View view) {

            PopupMenu popup;
            popup = new PopupMenu(mContentTextView.getContext(), view);
            popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()  {
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(mContentTextView.getContext(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                    if(item.getItemId()==R.id.sup){
                        mListener.onItemClick(getAdapterPosition(), mData.get(getAdapterPosition()));
                    }
                    else if((item.getItemId()==R.id.cqvv)){
                        mListener.onItemClick2(getAdapterPosition(), mData.get(getAdapterPosition()));
                    }
                    return true;
                }
            });

            popup.show();//showing popup menu
            return true;
        }
    }

    public interface Listener {
        void onItemClick(int position, Message message);

        void onItemClick2(int adapterPosition, Message message);
    }
}

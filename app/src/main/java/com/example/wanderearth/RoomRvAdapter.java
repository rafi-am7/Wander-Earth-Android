package com.example.wanderearth;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoomRvAdapter extends RecyclerView.Adapter<RoomRvAdapter.RoomViewHolder> {
    private static CustomClickListener mCustomClickListener;
    private final ArrayList<DailyBookingDT> roomArray;
    private final ArrayList<String> ratingsList;
    private DailyBookingDT room;
    private String rating;
    private final Context mContext;
    private final int bookedColor;
    private final int unBookedColor;

    //constructor
    public RoomRvAdapter(Context context, int bookedColor, int unbookedColor, ArrayList<DailyBookingDT> room, ArrayList<String> ratingsList) {
        this.roomArray = room;
        this.mContext = context;
        this.bookedColor = bookedColor;
        this.unBookedColor = unbookedColor;
        this.ratingsList = ratingsList;
    }

    public void setCustomClickListener(CustomClickListener customClickListener) //called from mainactivity
    {
        mCustomClickListener = customClickListener; //setting data
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  //an object of roomview holder which contain itemview
        View view = LayoutInflater.from(mContext).inflate(R.layout.room_item, parent, false);
        return new RoomViewHolder(view); //passed in itemview

    }

    @Override
    public void onBindViewHolder(@NonNull RoomRvAdapter.RoomViewHolder holder, int position) {
        room = roomArray.get(position);
        if (!ratingsList.isEmpty()) rating = ratingsList.get(position);
        holder.roomNameTextView.setText("room no " + room.getRoomNo());
        holder.roomRatingTextView.setText(rating);
        if (room.getIsBooked()) {
            Drawable buttonDrawable = holder.containerCardView.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, bookedColor);
            holder.containerCardView.setBackground(buttonDrawable);
            holder.containerCardView.setCardBackgroundColor(bookedColor);
        } else {

            Drawable buttonDrawable = holder.containerCardView.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, unBookedColor);
            holder.containerCardView.setBackground(buttonDrawable);
            holder.containerCardView.setCardBackgroundColor(unBookedColor);

        }
    }

    @Override
    public int getItemCount() {
        return roomArray.size();
    }

    public interface CustomClickListener {
        void customOnClick(int position, View v);

        void customOnLongClick(int position, View v);
        //declaring method which will provide to main activity //position and view will also be provided
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView roomNameTextView;
        CardView containerCardView;
        TextView roomRatingTextView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.tvRoomName);
            containerCardView = itemView.findViewById(R.id.llContainerCardView);
            roomRatingTextView = itemView.findViewById(R.id.tvRoomRating);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        } //to create view of every list item

        @Override
        public void onClick(View view) {
            mCustomClickListener.customOnClick(getAdapterPosition(), view);  //position and view setting to provide to mainactivity


        }

        public boolean onLongClick(View view) {

            mCustomClickListener.customOnLongClick(getAdapterPosition(), view);  //position and view setting to provide to mainactivity
            return true;

        }
    }


}

package com.example.plansly;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter <CardAdapter.CardItemViewHolder> {
    private ArrayList<CardItems> cardItemsList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    public static class CardItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView cardTimeText;
        public TextView cardTaskText;
        public TextView cardNoteText;


        public CardItemViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            cardTimeText = itemView.findViewById(R.id.cardTimeText);
            cardTaskText = itemView.findViewById(R.id.cardTaskText);
            cardNoteText = itemView.findViewById(R.id.cardNoteText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                    {
                        int position = getAdapterPosition();
                        //RecyclerView.NO_POSITION makes sure its a valid position
                        if (position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    //Receives the data for our cards from main activity
    public CardAdapter(ArrayList<CardItems> cardItemsList)
    {
        this.cardItemsList = cardItemsList;
    }

    @NonNull
    @Override
    public CardItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_items, viewGroup, false);
        CardItemViewHolder cvh = new CardItemViewHolder(v, mListener);
        return cvh;
    }

    //pass values to text views in here. The int position allows us to know what card we are looking at
    // so if we are looking at card 2 then we will get the information from position 2 in our array list
    @Override
    public void onBindViewHolder(@NonNull CardItemViewHolder cardItemViewHolder, int position) {

        //currentItem references the card we are currently looking at
        CardItems currentItem = cardItemsList.get(position);

        //Sets the text views of the card we are looking at
        cardItemViewHolder.cardTimeText.setText(currentItem.getTime());
        cardItemViewHolder.cardTaskText.setText(currentItem.getTask());
        cardItemViewHolder.cardNoteText.setText(currentItem.getNote());

    }

    @Override
    public int getItemCount() {
        return cardItemsList.size();
    }
}

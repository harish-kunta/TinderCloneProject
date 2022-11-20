package com.harish.tinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.harish.tinder.Matches.MatchesObject;
import com.harish.tinder.R;
import com.harish.tinder.viewholders.LikesViewHolder;

import java.util.List;

/**
 * Created by manel on 10/31/2017.
 */

public class LikesAdapter extends RecyclerView.Adapter<LikesViewHolder>{
    private List<MatchesObject> matchesList;
    private Context context;


    public LikesAdapter(List<MatchesObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public LikesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        LikesViewHolder likesViewHolder = new LikesViewHolder(layoutView);
        return likesViewHolder;
    }

    @Override
    public void onBindViewHolder(LikesViewHolder holder, int position) {
        //holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.mUserName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).into(holder.mUserImage);
        }
        holder.mUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), holder.mUserName.getText(), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}

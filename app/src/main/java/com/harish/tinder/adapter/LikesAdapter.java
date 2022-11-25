package com.harish.tinder.adapter;

import static com.harish.tinder.model.Constants.USER_ID;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.harish.tinder.R;
import com.harish.tinder.material_ui.UserProfileActivity;
import com.harish.tinder.model.UserObject;
import com.harish.tinder.viewholders.LikesViewHolder;

import java.util.List;

/**
 * Created by manel on 10/31/2017.
 */

public class LikesAdapter extends RecyclerView.Adapter<LikesViewHolder> {
    private List<UserObject> likesList;
    private Context context;

    public LikesAdapter(List<UserObject> likesList, Context context) {
        this.likesList = likesList;
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
        //Set ViewTag
        holder.itemView.setTag(position);
        UserObject userObject = likesList.get(position);
        holder.setPostImage(userObject, holder.itemView.getContext());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), UserProfileActivity.class);
            intent.putExtra(USER_ID, userObject.getUserId());
            context.startActivity(intent);
            // userItemClickListener.onUserClick(likesList.get(position), holder.mUserImage);
        });
    }

    @Override
    public int getItemCount() {
        return this.likesList.size();
    }
}

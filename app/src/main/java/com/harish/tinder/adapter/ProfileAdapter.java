package com.harish.tinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.harish.tinder.R;
import com.harish.tinder.model.Profile;


import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private Context context;
    private List<Profile> profileList;
    public ProfileAdapter(@NonNull Context context, List<Profile> profileList) {
        this.context=context;
        this.profileList=profileList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.card_view_profile, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Profile profile=profileList.get(position);
        if(profile!=null){
            holder.fullNameText.setText(""+profile.getName());
            holder.ageText.setText(""+profile.getAge()+"  year");
            Glide.with(holder.image_pic).load(profile.getProfile_pic()).into(holder.image_pic);
        }

    }
    public int getImage(String imageName) {

        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }


    @Override
    public int getItemCount() {
        if (profileList==null)
            return 0;
        return profileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameText, ageText;
        ImageView image_pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_pic=itemView.findViewById(R.id.item_image);
            fullNameText=itemView.findViewById(R.id.item_name);
            ageText=itemView.findViewById(R.id.item_age);

        }
    }
}

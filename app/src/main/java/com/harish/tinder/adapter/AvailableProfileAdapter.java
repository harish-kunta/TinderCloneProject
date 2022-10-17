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
import com.harish.tinder.custom.CustomDialog;
import com.harish.tinder.model.AvailableProfile;
import com.harish.tinder.model.Profile;

import java.util.List;

public class AvailableProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<AvailableProfile> availableProfiles;
    public AvailableProfileAdapter(@NonNull Context context, List<AvailableProfile> availableProfiles) {
        this.context=context;
        this.availableProfiles=availableProfiles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.card_view_available_profile, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //  if( availableProfiles.get(position) instanceof AvailableProfile){
        AvailableProfile availableProfile= (AvailableProfile) availableProfiles.get(position);
        final Profile profile=availableProfile.getProfile();
        ViewHolder viewHolder= (ViewHolder) holder;
        viewHolder.fullNameText.setText(""+profile.getName());
        viewHolder.ageText.setText(""+profile.getAge()+"  year");
        Glide.with(viewHolder.image_pic).load(profile.getProfile_pic()).into(viewHolder.image_pic);
        viewHolder.textView.setText(availableProfile.getText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog =new CustomDialog();
                customDialog.createDialog(context);
            }
        });

    }

    public int getImage(String imageName) {
        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        return drawableResourceId;
    }

    @Override
    public int getItemCount() {
        return availableProfiles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameText, ageText, textView;
        ImageView image_pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_pic=itemView.findViewById(R.id.item_image);
            fullNameText=itemView.findViewById(R.id.item_name);
            ageText=itemView.findViewById(R.id.item_age);
            textView=itemView.findViewById(R.id.item_text);
        }
    }
}

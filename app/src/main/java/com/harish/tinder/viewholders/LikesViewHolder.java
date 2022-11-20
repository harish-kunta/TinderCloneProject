package com.harish.tinder.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.harish.tinder.R;

/**
 * Created by manel on 10/31/2017.
 */
public class LikesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMatchId, mUserName;
    public ImageView mUserImage;
    public CardView mUserLayout;
    public LikesViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

       // mMatchId = (TextView) itemView.findViewById(R.id.user_name);
        mUserLayout = itemView.findViewById(R.id.user_layout);
        mUserName = (TextView) itemView.findViewById(R.id.user_name);
        mUserImage = (ImageView) itemView.findViewById(R.id.card_view_image);
    }

    @Override
    public void onClick(View view) {
//        Intent intent = new Intent(view.getContext(), ChatActivity.class);
//        Bundle b = new Bundle();
//        b.putString("matchId", mMatchId.getText().toString());
//        intent.putExtras(b);
//        view.getContext().startActivity(intent);
        Toast.makeText(view.getContext(), mUserName.getText(), Toast.LENGTH_LONG);
    }
}

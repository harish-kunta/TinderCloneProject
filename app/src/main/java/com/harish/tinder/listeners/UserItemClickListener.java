/*
 * Copyright (c) 2020. rogergcc
 */

package com.harish.tinder.listeners;

import android.widget.ImageView;

import com.harish.tinder.model.UserObject;

public interface UserItemClickListener {

    void onUserClick(UserObject userObject, ImageView imageView);
}

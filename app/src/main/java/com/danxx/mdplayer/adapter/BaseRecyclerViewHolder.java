package com.danxx.mdplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Base RecyclerViewHolder
 * Created by Danxingxi on 2016/3/31.
 */
public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract View getView();

}

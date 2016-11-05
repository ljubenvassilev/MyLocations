package com.ljubo87bg.mylocations.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ljubo87bg.mylocations.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljubo on 11/5/2016.
 */
public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.ViewHolder> {
    private ArrayList<String> pics;
    Context context;

    public PicturesAdapter(ArrayList<String> itemsData) {
        this.pics = itemsData;
    }

    @Override
    public PicturesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        context=parent.getContext();
        View itemLayoutView = LayoutInflater.from(context)
                .inflate(R.layout.row_layout, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(pics.get(position)).into(viewHolder.pic);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView pic;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            pic = (ImageView) itemLayoutView.findViewById(R.id.picture);
        }
    }

    @Override
    public int getItemCount() {
        return pics.size();
    }
}


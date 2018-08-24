package com.example.leehyungyu.draw_example;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LeeHyunGyu on 2018-06-09.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.DataObjectHolder>{
private static String LOG_TAG="ReviewAdapter";
private ArrayList<Review> mDataset;
private static MyClickListener myClickListener;

public static class DataObjectHolder extends RecyclerView.ViewHolder
        implements View
        .OnClickListener {
    TextView userid;
    TextView review;
    RatingBar ratingBar;
    TextView time;

    public DataObjectHolder(View itemView) {
        super(itemView);
        userid = (TextView) itemView.findViewById(R.id.userid);
        review = (TextView) itemView.findViewById(R.id.review);
        ratingBar = (RatingBar) itemView.findViewById(R.id.review_ratingBar);
        time = (TextView) itemView.findViewById(R.id.time);

        Log.i(LOG_TAG, "Adding Listener");
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        myClickListener.onItemClick(getAdapterPosition(), v);
    }
}

    public void setOnItemClickListener(ReviewAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ReviewAdapter(ArrayList<Review> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ReviewAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_view, parent, false);

        ReviewAdapter.DataObjectHolder dataObjectHolder = new ReviewAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.DataObjectHolder holder, int position) {
        holder.userid.setText(mDataset.get(position).getUserid());
        holder.review.setText(mDataset.get(position).getContent());
        holder.ratingBar.setStepSize((float) 0.5);        //별 색깔이 1칸씩줄어들고 늘어남 0.5로하면 반칸씩 들어감
        holder.ratingBar.setIsIndicator(true);           //true - 별점만 표시 사용자가 변경 불가 , false - 사용자가 변경가능
        holder.ratingBar.setRating(Float.parseFloat(mDataset.get(position).getRating()));
        Log.d("REVIEW_RATING",mDataset.get(position).getRating());
        holder.ratingBar.setIsIndicator(false);
        holder.time.setText(mDataset.get(position).getTime());
    }

    public void addItem(Review dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
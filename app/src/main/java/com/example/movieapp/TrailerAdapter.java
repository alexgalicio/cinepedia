package com.example.movieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<VideoModel> trailers;
    private Context context;

    public TrailerAdapter(List<VideoModel> trailers, Context context) {
        this.trailers = trailers;
        this.context = context;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        VideoModel trailer = trailers.get(position);
        String thumbnailUrl = "https://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";

        Glide.with(context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.no_poster)
                .apply(new RequestOptions()
                        .transform(new CenterCrop(), new RoundedCorners(20)))
                .into(holder.imageThumbnail);


        holder.textTrailerName.setText(trailer.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    static class TrailerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumbnail;
        TextView textTrailerName;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.imageThumbnail);
            textTrailerName = itemView.findViewById(R.id.textTrailerName);
        }
    }

}

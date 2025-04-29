package com.example.movieapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<CreditsResponse.Cast> castList;

    public CastAdapter(List<CreditsResponse.Cast> castList) {
        this.castList = castList;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {
        CreditsResponse.Cast cast = castList.get(position);
        holder.name.setText(cast.getName());
        holder.character.setText(cast.getCharacter());

        Glide.with(holder.itemView.getContext())
                .load(cast.getProfilePath() != null ? cast.getProfileUrl() : null)
                .placeholder(R.drawable.no_profile)
                .error(R.drawable.no_profile)
                .into(holder.profile);

    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView name, character;

        public CastViewHolder(View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.imageProfile);
            name = itemView.findViewById(R.id.textName);
            character = itemView.findViewById(R.id.textCharacter);
        }
    }
}

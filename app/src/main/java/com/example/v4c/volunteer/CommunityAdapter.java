package com.example.v4c.volunteer;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.v4c.R;
import com.example.v4c.volunteer.CommunityDetailActivity;
import com.example.v4c.volunteer.CommunityModel;
import com.google.gson.Gson;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter. CommunityViewHolder> {

    private Context context;
    private List<CommunityModel> communityList;

    public CommunityAdapter(Context context, List<CommunityModel> ngoList) {
        this.context = context;
        this.communityList = ngoList;
    }

    @NonNull
    @Override
    public  CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.community, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  CommunityViewHolder holder, int position) {
        CommunityModel ngo = communityList.get(position);
        holder.name.setText(ngo.name);
        holder.tagline.setText(ngo.tagline);
        Glide.with(context).load(ngo.main_image).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommunityDetailActivity.class);
            intent.putExtra("ngo", new Gson().toJson(ngo));
            context.startActivity(intent);
            //animation
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView name, tagline;
        ImageView image;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ngo_name);
            tagline = itemView.findViewById(R.id.ngo_tagline);
            image = itemView.findViewById(R.id.ngo_image);
        }
    }
}

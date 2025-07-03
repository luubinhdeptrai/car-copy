package com.example.login.ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.R;
import java.util.List;

public class AdBannerAdapter extends RecyclerView.Adapter<AdBannerAdapter.ViewHolder> {

    private Context context;
    private List<Integer> adImageResourceIds;

    public AdBannerAdapter(Context context, List<Integer> adImageResourceIds) {
        this.context = context;
        this.adImageResourceIds = adImageResourceIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ad_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageResId = adImageResourceIds.get(position);
        holder.adImage.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        return adImageResourceIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView adImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.ad_banner_image_item);
        }
    }
}
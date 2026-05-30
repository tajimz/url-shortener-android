package com.tajim.urlshortener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.databinding.ItemUrlBinding;
import com.tajim.urlshortener.models.ShortUrl;
import com.tajim.urlshortener.utils.AppUtils;

import java.util.List;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.ViewHolder> {
    List<ShortUrl> shortUrls;
    public UrlAdapter( List<ShortUrl> shortUrls){
        this.shortUrls = shortUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUrlBinding binding = ItemUrlBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShortUrl shortUrl = shortUrls.get(position);
        holder.binding.tvShortUrlAlias.setText(shortUrl.short_code);
        holder.binding.tvLongUrl.setText(shortUrl.long_url);
        holder.binding.tvViews.setText("Clicks: "+shortUrl.clicks);
        holder.binding.getRoot().setOnLongClickListener(v -> {

            AppUtils.copyToClipBoard(v.getContext(), ApiConfig.PUBLIC_BASE + shortUrl.short_code);

            Toast.makeText(v.getContext(), "Copied", Toast.LENGTH_SHORT).show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return shortUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemUrlBinding binding;
        public ViewHolder(ItemUrlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

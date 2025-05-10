package com.www.avia.kg.app.ui.profile;

import android.view.LayoutInflater;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.www.avia.kg.app.databinding.TaItemInfoBinding;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    private final List<InfoItem> infoItemList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(InfoItem item);
    }

    public InfoAdapter(List<InfoItem> infoItemList, OnItemClickListener listener) {
        this.infoItemList = infoItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaItemInfoBinding binding = TaItemInfoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new InfoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItem item = infoItemList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return infoItemList.size();
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder {

        private final TaItemInfoBinding binding;

        public InfoViewHolder(@NonNull TaItemInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final InfoItem item, final OnItemClickListener listener) {
            binding.icon.setImageResource(item.iconRes);
            binding.title.setText(item.title);
            binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}

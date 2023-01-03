package com.example.android.wifirttscan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private List<String> fileNames;

    public FileAdapter(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_recycler_row_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        String fileName = fileNames.get(position);
        holder.textView.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return fileNames.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public FileViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text_view);
        }
    }
}

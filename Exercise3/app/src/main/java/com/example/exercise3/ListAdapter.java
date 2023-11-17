package com.example.exercise3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


public class ListAdapter extends ArrayAdapter<FileSystemModel> {
    private List<FileSystemModel> itemList;
    private LayoutInflater inflater;

    private static class ViewHolder {
        ImageView fileImageView;
        TextView fileNameTextView;
        TextView albumTextView;
    }
    public ListAdapter(@NonNull Context context, List<FileSystemModel> itemList) {
        super(context, R.layout.recycler_item, itemList);
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.recycler_item, parent, false);
            holder = new ViewHolder();
            holder.fileImageView = view.findViewById(R.id.icon_view);
            holder.fileNameTextView = view.findViewById(R.id.music_title_text);
//            holder.albumTextView = view.findViewById(R.id.albumTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        FileSystemModel item = itemList.get(position);
        holder.fileNameTextView.setText(item.getTitle());

        if (item.isFolder()) {
            holder.fileImageView.setImageResource(R.drawable.baseline_folder_24);
        } else {
//            if (item.getAlbumName() != null && !item.getAlbumName().isEmpty()) {
//                holder.albumTextView.setText(item.getAlbumName());
//            } else {
//                holder.albumTextView.setVisibility(View.GONE);
//                holder.fileImageView.setImageResource(R.drawable.music_icon);
//            }
            if (isMusicFile(item.getTitle())) {
                if(item.getAlbumName()== null)
                    holder.fileImageView.setImageResource(R.drawable.music_icon);
                else
                    holder.fileImageView.setImageBitmap(item.getAlbumName());
            } else
                holder.fileImageView.setImageResource(R.drawable.baseline_folder_24);
        }

        return view;
    }

    private boolean isMusicFile(String fileName) {
        String[] supportedExtensions = {".mp3", ".wav", ".ogg", ".mp4"};
        for (String extension : supportedExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}

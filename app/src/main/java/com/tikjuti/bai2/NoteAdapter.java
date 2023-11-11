package com.tikjuti.bai2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private MainNote context;

    private int layout;
    private List<Note> noteList;

    public NoteAdapter(MainNote context, int layout, List<Note> noteList) {
        this.context = context;
        this.layout = layout;
        this.noteList = noteList;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class  ViewHolder{
        TextView txtTextTitle;
        TextView txtTextTime;
        TextView txtTextContent;
        LinearLayout itemList;
        ImageView noteImage;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            viewHolder.txtTextTitle = (TextView) view.findViewById(R.id.txtTextTitle);
            viewHolder.txtTextTime = (TextView) view.findViewById(R.id.txtTextTime);
            viewHolder.txtTextContent = (TextView) view.findViewById(R.id.txtTextContent);
            viewHolder.itemList = (LinearLayout) view.findViewById(R.id.itemList);
            viewHolder.noteImage = view.findViewById(R.id.noteEditImage);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Note note = noteList.get(i);
        viewHolder.txtTextTitle.setText(note.getTitle());
        viewHolder.txtTextTime.setText(note.getDateTime());
        viewHolder.txtTextContent.setText(note.getContent());

        viewHolder.itemList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                context.dialogDelete(note.getId());
                return true;
            }
        });
        viewHolder.itemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.updateNote(note.getId());
            }
        });

        return view;
    }
}

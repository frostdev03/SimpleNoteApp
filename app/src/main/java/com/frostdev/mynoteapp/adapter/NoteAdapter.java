package com.frostdev.mynoteapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.frostdev.mynoteapp.CustomOnItemClickListener;
import com.frostdev.mynoteapp.Note;
import com.frostdev.mynoteapp.NoteAddUpdateActivity;
import com.frostdev.mynoteapp.R;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final ArrayList< Note > listNotes = new ArrayList<>();
    private Activity activity;

    public NoteAdapter(Activity activity){
        this.activity = activity;
    }

    public ArrayList<Note> getListNotes(){
        return listNotes;
    }

    public void setListNotes(ArrayList<Note> listNotes){
        if (listNotes.size() > 0){
            this.listNotes.clear();
        }
        this.listNotes.addAll(listNotes);

        notifyDataSetChanged();
    }

    public void addItem(Note note){
        this.listNotes.add(note);
        notifyItemInserted(listNotes.size() - 1);
    }

    public void updateItem(int position, Note note){
        this.listNotes.set(position, note);
        notifyItemChanged(position, note);
    }

    public void removeItem(int position){
        this.listNotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listNotes.size());
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
	public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
	holder.tvTitle.setText(listNotes.get(position).getTitle());
    holder.tvdate.setText(listNotes.get(position).getDate());
    holder.tvDesc.setText(listNotes.get(position).getDescription());
    holder.cvNote.setOnClickListener(new CustomOnItemClickListener(position, (view, position1) -> {
            Intent intent = new Intent(activity, NoteAddUpdateActivity.class);
            intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position1);
            intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, listNotes.get(position1));
            activity.startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_UPDATE);
        }));
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

        final TextView tvTitle, tvDesc, tvdate;
        final CardView cvNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDesc = itemView.findViewById(R.id.tv_item_description);
            tvdate = itemView.findViewById(R.id.tv_item_date);
            cvNote = itemView.findViewById(R.id.cv_item_note);
        }
    }
}

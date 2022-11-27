package com.example.medicly;

import static com.example.medicly.Schedule.SCHEDULE_COLLECTION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class SchedListAdapter extends RecyclerView.Adapter<SchedListAdapter.SchedListViewHolder>{
    private List<SchedData> data;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SchedData> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SchedListAdapter.SchedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sched_list_item, parent, false);
        return new SchedListAdapter.SchedListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedListAdapter.SchedListViewHolder holder, int position) {
        SchedData entry = data.get(position);

        holder.place.setText(entry.getPlace());
        holder.date.setText(entry.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context contextForIntent = holder.itemView.getContext();
                String idOfNoteToEdit = entry.getId();

                openSchedEditor(contextForIntent, idOfNoteToEdit);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Context contextForDialog = holder.itemView.getContext();
                String idOfNoteToDelete = entry.getId();

                openDeleteDialog(contextForDialog, idOfNoteToDelete);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    private void openSchedEditor(Context contextForIntent, String idOfNoteToEdit) {
        Intent intent = new Intent(contextForIntent, SchedEditor.class);

        intent.putExtra(SchedEditor.SCHEDULE_ID_KEY, idOfNoteToEdit);

        contextForIntent.startActivity(intent);
    }

    private void openDeleteDialog(Context contextForDialog, String idOfNoteToDelete) {
        new AlertDialog.Builder(contextForDialog)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (idOfNoteToDelete != null) {

                            FirebaseFirestore.getInstance()
                                    .collection(SCHEDULE_COLLECTION)
                                    .document(idOfNoteToDelete)
                                    .delete();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static class SchedListViewHolder extends RecyclerView.ViewHolder {

        TextView place, date;

        public SchedListViewHolder(@NonNull View itemView) {
            super(itemView);
            place = itemView.findViewById(R.id.place);
            date = itemView.findViewById(R.id.date);
        }
    }

}

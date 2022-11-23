package com.example.medicly;

import static com.example.medicly.Diagnosis.DIAGNOSIS_COLLECTION;

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

public class DiagnosisListAdapter extends RecyclerView.Adapter<DiagnosisListAdapter.DiagnosisListViewHolder> {

    private List<DiagnosisData> data;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<DiagnosisData> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiagnosisListAdapter.DiagnosisListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.diagnosis_list_item, parent, false);
        return new DiagnosisListAdapter.DiagnosisListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiagnosisListAdapter.DiagnosisListViewHolder holder, int position) {
        DiagnosisData entry = data.get(position);

        holder.title.setText(entry.getTitle());
        holder.description.setText(entry.getDescription());
        holder.date.setText(entry.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context contextForIntent = holder.itemView.getContext();
                String idOfNoteToEdit = entry.getId();

                openDiagnosisEditor(contextForIntent, idOfNoteToEdit);
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

    private void openDiagnosisEditor(Context contextForIntent, String idOfNoteToEdit) {
        Intent intent = new Intent(contextForIntent, DiagnosisEditor.class);

        intent.putExtra(DiagnosisEditor.DIAGNOSIS_ID_KEY, idOfNoteToEdit);

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
                                    .collection(DIAGNOSIS_COLLECTION)
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

    public static class DiagnosisListViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, date;

        public DiagnosisListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.diagTitle);
            description = itemView.findViewById(R.id.diagDescription);
            date = itemView.findViewById(R.id.diagDate);
        }
    }
}

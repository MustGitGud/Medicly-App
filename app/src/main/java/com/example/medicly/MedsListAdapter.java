package com.example.medicly;

import static com.example.medicly.Medications.MEDICATIONS_COLLECTION;

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

public class MedsListAdapter extends RecyclerView.Adapter<MedsListAdapter.MedsListViewHolder> {

    private List<MedsData> data;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MedsData> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedsListAdapter.MedsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.medications_list_item, parent, false);
        return new MedsListAdapter.MedsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedsListAdapter.MedsListViewHolder holder, int position) {
        MedsData entry = data.get(position);

        holder.medicine.setText(entry.getMedicine());
        holder.intake.setText(entry.getIntake());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context contextForIntent = holder.itemView.getContext();
                String idOfNoteToEdit = entry.getId();

                openMedicationsEditor(contextForIntent, idOfNoteToEdit);
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

    private void openMedicationsEditor(Context contextForIntent, String idOfNoteToEdit) {
        Intent intent = new Intent(contextForIntent, MedicationsEditor.class);

        intent.putExtra(MedicationsEditor.MEDICATIONS_ID_KEY, idOfNoteToEdit);

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
                                    .collection(MEDICATIONS_COLLECTION)
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

    public static class MedsListViewHolder extends RecyclerView.ViewHolder {

        TextView medicine, intake;

        public MedsListViewHolder(@NonNull View itemView) {
            super(itemView);
            medicine = itemView.findViewById(R.id.medCol);
            intake = itemView.findViewById(R.id.intCol);
        }
    }

}

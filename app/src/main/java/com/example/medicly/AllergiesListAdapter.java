package com.example.medicly;


import static com.example.medicly.Allergies.ALLERGIES_COLLECTION;

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

/*
 * RecyclerView adapter
 * reference: https://developer.android.com/develop/ui/views/layout/recyclerview#java
 */
public class AllergiesListAdapter extends RecyclerView.Adapter<AllergiesListAdapter.AllergiesListViewHolder> {

    private List<Allergy> data;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Allergy> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AllergiesListAdapter.AllergiesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.allergies_list_item, parent, false);
        return new AllergiesListAdapter.AllergiesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergiesListAdapter.AllergiesListViewHolder holder, int position) {
        Allergy entry = data.get(position);

        holder.title.setText(entry.getTitle());
        holder.description.setText(entry.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context contextForIntent = holder.itemView.getContext();
                String idOfNoteToEdit = entry.getId();

                openAllergiesEditor(contextForIntent, idOfNoteToEdit);
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

    private void openAllergiesEditor(Context contextForIntent, String idOfNoteToEdit) {
        Intent intent = new Intent(contextForIntent, AllergiesEditor.class);

        /*
         * Pass data to another activity
         * reference: https://developer.android.com/reference/android/content/Intent#putExtra(java.lang.String,%20java.lang.String)
         */
        intent.putExtra(AllergiesEditor.ALLERGY_ID_KEY, idOfNoteToEdit);

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

                        // ensure id is not null before deleting to avoid errors with firebase
                        if (idOfNoteToDelete != null) {
                            /*
                             * Delete a document
                             * reference: https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
                             */
                            FirebaseFirestore.getInstance()
                                    .collection(ALLERGIES_COLLECTION)
                                    .document(idOfNoteToDelete)
                                    .delete();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing, just close dialog when user clicks "No"
                        dialog.dismiss();
                    }
                })
                .show();
    }

    // class to hold references to views (title and description) of each row (allergy)
    public static class AllergiesListViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;

        public AllergiesListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
}
package com.ayusma.textrecognition.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ayusma.textrecognition.Helper.Saver;
import com.ayusma.textrecognition.Operation;
import com.ayusma.textrecognition.R;
import com.ayusma.textrecognition.TextActivity;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Saver> mData;
    private Operation operation;
    private Context mContext;

    public RecyclerViewAdapter(List<Saver> data) {
        this.mData = data;

    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        operation = new Operation(mContext);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String myListData = mData.get(position).getText();
        holder.preview_text.setText(mData.get(position).getText());
        holder.preview_id.setText(String.valueOf(mData.get(position).getId()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = operation.getSavedText(position + 1);
                Intent intent = new Intent(mContext, TextActivity.class);
                intent.putExtra("Text", text);
                mContext.startActivity(intent);
                Activity activity = (Activity)mContext;
                activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


            }
        });

        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = operation.getSavedText(position + 1);
                operation.copy(text);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = operation.getSavedText(position + 1);
                operation.share(text);
            }
        });

        holder.export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.txt_export);
                builder.setMessage(R.string.export_confirmation);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = operation.getSavedText(position + 1);
                        operation.export(text, position + 1);
                    }
                }).setNegativeButton(R.string.no,null).show();


            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete);
                builder.setMessage(R.string.delete_confirmation);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        operation.delete(mData.get(position));
                        mData.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,mData.size());
                    }
                }).setNegativeButton(R.string.no,null).show();



            }
        });


    }




    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView preview_id, preview_text;
        public MaterialCardView cardView;
        public ImageView copy, export, share, delete;


        public ViewHolder(View itemView) {
            super(itemView);
            this.preview_text = itemView.findViewById(R.id.preview_text);
            this.preview_id = itemView.findViewById(R.id.preview_id);
            this.cardView = itemView.findViewById(R.id.card_view);
            this.copy = itemView.findViewById(R.id.icon_copy);
            this.delete = itemView.findViewById(R.id.icon_delete);
            this.export = itemView.findViewById(R.id.icon_export);
            this.share = itemView.findViewById(R.id.icon_share);


        }


    }


}


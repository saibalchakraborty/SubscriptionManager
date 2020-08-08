package com.subscriptionmanager.utility;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.subscriptionmanager.R;
import com.subscriptionmanager.SubscriptionActivity;
import com.subscriptionmanager.dao.SubscriptionManagerDAO;
import com.subscriptionmanager.model.Subscription;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    ArrayList<Subscription> subscriptionList;

    public RecyclerAdapter(ArrayList<Subscription> subscriptionList) {
        this.subscriptionList = subscriptionList;
        Log.i("Submgr.info", "Size of adaptor list "+subscriptionList.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.subscriptionView.setText("Subscription: "+subscriptionList.get(position).getSubscription());
        holder.costView.setText("Cost: "+Double.toString(subscriptionList.get(position).getCost()));
        holder.startDateView.setText("Start: "+Utility.convertDate(subscriptionList.get(position).getStartDate()));
        holder.endDateView.setText("End: "+Utility.convertDate(subscriptionList.get(position).getEndDate()));
        holder.notifyDateView.setText("Notify: "+Utility.convertDate(subscriptionList.get(position).getNotifyDate()));
    }

    @Override
    public int getItemCount() {
        Log.i("Submgr.info", "Setting size to "+subscriptionList.size());
        return subscriptionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView subscriptionView, costView, startDateView, endDateView, notifyDateView;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subscriptionView = itemView.findViewById(R.id.subscriptionview);
            costView = itemView.findViewById(R.id.costview);
            startDateView = itemView.findViewById(R.id.startdateview);
            endDateView = itemView.findViewById(R.id.enddateview);
            notifyDateView = itemView.findViewById(R.id.notifydateview);
            parentLayout = itemView.findViewById(R.id.parentview);
            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Subscription subscription = subscriptionList.get(getAdapterPosition());
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                    View alertView = LayoutInflater.from(view.getContext()).inflate(R.layout.activity_custom_dialogue, null);
                    Button editButton = alertView.findViewById(R.id.edit);
                    Button deleteButton = alertView.findViewById(R.id.delete);
                    alertDialog.setView(alertView);
                    final AlertDialog dialog = alertDialog.create();
                    dialog.setCanceledOnTouchOutside(true);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View myView) {
                            Intent intent = new Intent(view.getContext(), SubscriptionActivity.class);
                            intent.putExtra("Subscription", subscription);
                            view.getContext().startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(new SubscriptionManagerDAO(view.getContext()).deleteSubscription(subscription).size() == 0){
                                Toast.makeText(view.getContext(), subscription.getSubscription() + " deleted successfully", Toast.LENGTH_LONG).show();
                                subscriptionList.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }
            });
        }

        @Override
        public void onClick(final View view) {
            final Subscription subscription = subscriptionList.get(getAdapterPosition());
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
            View alertView = LayoutInflater.from(view.getContext()).inflate(R.layout.activity_custom_dialogue, null);
            Button editButton = alertView.findViewById(R.id.edit);
            Button deleteButton = alertView.findViewById(R.id.delete);
            alertDialog.setView(alertView);
            final AlertDialog dialog = alertDialog.create();
            dialog.setCanceledOnTouchOutside(true);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View myView) {
                    Intent intent = new Intent(view.getContext(), SubscriptionActivity.class);
                    intent.putExtra("Subscription", subscription);
                    view.getContext().startActivity(intent);
                    dialog.dismiss();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(new SubscriptionManagerDAO(view.getContext()).deleteSubscription(subscription).size() == 0){
                        Toast.makeText(view.getContext(), subscription.getSubscription() + " deleted successfully", Toast.LENGTH_LONG).show();
                        subscriptionList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}

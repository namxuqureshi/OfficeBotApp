package com.example.n_u.officebotapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.n_u.officebotapp.R;
import com.example.n_u.officebotapp.models.Friend;
import com.example.n_u.officebotapp.utils.AppLog;
import com.example.n_u.officebotapp.viewsholders.SelectionHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SelectionFriendAdapter
        extends RecyclerView.Adapter<SelectionHolder> {
    private List<Friend> filterData;
    private List<Friend> adapterData;
    private List<Integer> id = new ArrayList<>();
    private Context mContext;

    public SelectionFriendAdapter(Context paramContext, List<Friend> paramList) {
        this.adapterData = paramList;
        this.filterData = paramList;
        this.mContext = paramContext;
    }

    public void addAll(List<Friend> paramList) {
        this.filterData.addAll(paramList);
        notifyDataSetChanged();
    }

    public List<Integer> checkedList() {
        if (this.id.isEmpty()) {
            Iterator localIterator = filterData.iterator();
            while (localIterator.hasNext()) {
                Friend localFriend = (Friend) localIterator.next();
                if (localFriend.isCheckBox()) {
                    id.add(localFriend.getFriendId());
                }
            }
        }
        return Collections.unmodifiableList(this.id);
    }

    public void clear() {
        if (!this.filterData.isEmpty()) {
            this.filterData.clear();
        }
        notifyDataSetChanged();
    }

    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterData.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    filterData.addAll(adapterData);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Friend item : adapterData) {
                        if (item.getName().toLowerCase().contains(text.toLowerCase())
//                                || item.place.toLowerCase().contains(text.toLowerCase())
                                ) {
                            // Adding Matched items
                            filterData.add(item);
                        }
                    }
                }

                // Set on UI Thread
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }

    public int getItemCount() {
        if (this.filterData != null) {
            return this.filterData.size();
        }
        return 0;
    }

    public void onBindViewHolder(final SelectionHolder holder
            , int position) {
        TextView textView = holder.getName();
        final CheckBox checkBox = holder.getCheckBox();
        textView.setText(this.filterData.get(holder.getAdapterPosition()).getName());
        if (this.filterData.get(holder.getAdapterPosition()).isCheckBox()) {
            Log.e("TAGAdapt", "onBindViewHolder: " + this.filterData.get(holder.getAdapterPosition()).isCheckBox());
            checkBox.setChecked(true);
        }
        checkBox.setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                AppLog.setVibrate(mContext, AppLog.INTENSITY_MIDDLE);
                if (checkBox.isChecked()) {
                    if (!id.contains(filterData.get(holder.getAdapterPosition()).getFriendId())) {
                        id.add(filterData.get(holder.getAdapterPosition()).getFriendId());
                        filterData.get(holder.getAdapterPosition()).setCheckBox(true);
                    }
                    Log.e("TAGAdapt", "onBindViewHolder: " + filterData.get(holder.getAdapterPosition()).isCheckBox());
                } else {
                    filterData.get(holder.getAdapterPosition()).setCheckBox(false);
                    int ids = filterData.get(holder.getAdapterPosition()).getFriendId();
                    Log.e("TAGAdapt", "onBindViewHolder: " + filterData.get(holder.getAdapterPosition()).isCheckBox());
                    if (id.contains(ids)) {
                        id.remove(id.indexOf(ids));
                        Log.e("TAGAdapt", "onBindViewHolder: " + id.toString());
                    }
                }
            }
        });

        onViewDetachedFromWindow(holder);
        onViewDetachedFromWindow(holder);
    }

    public SelectionHolder onCreateViewHolder(ViewGroup viewGroup
            , int viewType) {
        return new SelectionHolder(LayoutInflater.
                from(this.mContext).
                inflate(R.layout.custom_selection_list,
                        viewGroup,
                        false));
    }

    public void onViewAttachedToWindow(SelectionHolder selectionHolder) {
        super.onViewAttachedToWindow(selectionHolder);
        if (this.filterData.get(selectionHolder.getAdapterPosition()).isCheckBox()) {
            selectionHolder.getCheckBox().setChecked(true);
        }
    }

    public void onViewDetachedFromWindow(SelectionHolder selectionHolder) {
        super.onViewDetachedFromWindow(selectionHolder);
        if (selectionHolder.getCheckBox().isChecked()) {
            selectionHolder.getCheckBox().setChecked(false);
        }
    }
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.adapters.SelectionFriendAdapter

 * JD-Core Version:    0.7.0.1

 */
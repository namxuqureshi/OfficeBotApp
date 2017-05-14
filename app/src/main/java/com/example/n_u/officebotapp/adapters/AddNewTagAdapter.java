package com.example.n_u.officebotapp.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.n_u.officebotapp.R;

import java.util.ArrayList;
import java.util.List;

public class AddNewTagAdapter
        extends BaseAdapter {
    private final Activity context;
    private List<Integer> adapterData = new ArrayList<>();
    private boolean a, b, c;
    private RadioButton ab, bc, ca;

    public AddNewTagAdapter(Activity paramActivity, List<Integer> paramList) {
        this.context = paramActivity;
        this.adapterData = paramList;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public int getCount() {
        return this.adapterData.size();
    }

    public Integer getItem(int i) {
        return this.adapterData.get(i);
    }

    public long getItemId(int paramInt) {
        return this.adapterData.get(paramInt).longValue();
    }

    @NonNull
    public View getView(final int position, View rowView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final ViewHolder holder;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.grid_view, parent, false);
            holder = new ViewHolder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.imageView.setImageResource(adapterData.get(position));
        if (adapterData.get(position).equals(R.drawable.office)) {
            ab = holder.rd;
        }
        if (adapterData.get(position).equals(R.drawable.home)) {
            bc = holder.rd;
        }
        if (adapterData.get(position).equals(R.drawable.logo)) {
            ca = holder.rd;
        }
        holder.rd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterData.get(position).equals(R.drawable.office)) {
                    a = true;
                    b = false;
                    c = false;
                    ab.setChecked(true);
                    bc.setChecked(false);
                    ca.setChecked(false);
                }
                if (adapterData.get(position).equals(R.drawable.home)) {
                    a = false;
                    b = true;
                    c = false;
                    bc.setChecked(true);
                    ca.setChecked(false);
                    ab.setChecked(false);
                }
                if (adapterData.get(position).equals(R.drawable.logo)) {
                    a = false;
                    b = false;
                    c = true;
                    ca.setChecked(true);
                    ab.setChecked(false);
                    bc.setChecked(false);
                }
            }
        });
        return rowView;
    }

    public String getName() {
        if (a) {
            return "office";
        } else if (b) {
            return "home";
        } else if (c) {
            return "logo";
        } else return "0";
    }

    public boolean hasStableIds() {
        return true;
    }


    private static class ViewHolder {
        ImageView imageView;
        RadioButton rd;

        ViewHolder(View view) {
            imageView = ((ImageView) view.findViewById(R.id.grid_item_image));
            rd = (RadioButton) view.findViewById(R.id.checked_pic);
        }
    }
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.adapters.AddNewTagAdapter

 * JD-Core Version:    0.7.0.1

 */
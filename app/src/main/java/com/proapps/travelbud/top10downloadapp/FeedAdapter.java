package com.proapps.travelbud.top10downloadapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Atique on 12/21/2017.
 */

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        // TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
        // TextView tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);

        FeedEntry feedEntry = applications.get(position);

        viewHolder.tvArtist.setText(feedEntry.getArtist());
        viewHolder.tvName.setText(feedEntry.getName());
        viewHolder.tvSummary.setText(feedEntry.getSummary());
        return convertView;
    }

    private class ViewHolder {
        private final TextView tvName;
        private final TextView tvArtist;
        private final TextView tvSummary;

        private ViewHolder(View v) {
            this.tvName = (TextView) v.findViewById(R.id.tvName);
            this.tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            this.tvSummary = (TextView) v.findViewById(R.id.tvSummary);
        }
    }
}

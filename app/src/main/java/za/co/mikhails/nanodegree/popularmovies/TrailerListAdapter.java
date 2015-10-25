package za.co.mikhails.nanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.MessageFormat;

public class TrailerListAdapter extends CursorAdapter {
    public TrailerListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public TrailerListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);
        view.setTag(R.id.LIST_ITEM_TRAILER, view.findViewById(R.id.list_item_text));
        view.setTag(R.id.LIST_ITEM_THUMBNAIL, view.findViewById(R.id.list_item_thumbnail));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.getTag(R.id.LIST_ITEM_TRAILER);
        textView.setText(cursor.getString(TrailerListFragment.COLUMN_NAME));

        ImageView imageView = (ImageView) view.getTag(R.id.LIST_ITEM_THUMBNAIL);
        String trailerKey = cursor.getString(TrailerListFragment.COLUMN_KEY);
        if (trailerKey != null) {
            String url = MessageFormat.format(context.getString(R.string.youtube_thumbnail_url), trailerKey);
            Picasso.with(context).load(url).into(imageView);
        }
        view.setTag(R.id.TRAILER_KEY, trailerKey);

        view.setTag(R.id.TRAILER_ID, cursor.getInt(TrailerListFragment.COLUMN_TRAILER_ID));
    }

}

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

    public TrailerListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);
        view.setTag(R.id.LIST_ITEM_TYPE, view.findViewById(R.id.list_item_type));
        view.setTag(R.id.LIST_ITEM_NAME, view.findViewById(R.id.list_item_name));
        view.setTag(R.id.LIST_ITEM_SIZE, view.findViewById(R.id.list_item_size));
        view.setTag(R.id.LIST_ITEM_THUMBNAIL, view.findViewById(R.id.list_item_thumbnail));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.getTag(R.id.LIST_ITEM_TYPE)).setText(cursor.getString(MovieDetailFragment.TRAILER_COLUMN_TYPE));
        ((TextView) view.getTag(R.id.LIST_ITEM_NAME)).setText(cursor.getString(MovieDetailFragment.TRAILER_COLUMN_NAME));
        ((TextView) view.getTag(R.id.LIST_ITEM_SIZE)).setText(String.valueOf(cursor.getInt(MovieDetailFragment.TRAILER_COLUMN_SIZE)));

        ImageView imageView = (ImageView) view.getTag(R.id.LIST_ITEM_THUMBNAIL);
        String trailerKey = cursor.getString(MovieDetailFragment.TRAILER_COLUMN_KEY);
        if (trailerKey != null) {
            String url = MessageFormat.format(context.getString(R.string.youtube_thumbnail_url), trailerKey);
            Picasso.with(context).load(url).into(imageView);
        }
        view.setTag(R.id.TRAILER_KEY, trailerKey);
    }

}

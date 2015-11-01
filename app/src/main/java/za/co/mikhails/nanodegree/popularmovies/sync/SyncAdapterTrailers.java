package za.co.mikhails.nanodegree.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import za.co.mikhails.nanodegree.popularmovies.R;
import za.co.mikhails.nanodegree.popularmovies.Utils;
import za.co.mikhails.nanodegree.popularmovies.data.MoviesContract.TrailersEntry;

public class SyncAdapterTrailers extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = SyncAdapterTrailers.class.getSimpleName();
    public static final String MOVIE_ID = "movieid";

    public SyncAdapterTrailers(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");
        String movieId = extras.getString(MOVIE_ID);
        if (movieId != null && !movieId.trim().isEmpty() && Utils.isNetworkConnected(getContext())) {
            retrieveTrailerData(movieId);
        }
    }

    private boolean retrieveTrailerData(String movieId) {
        Log.d(LOG_TAG, "retrieveTrailerData movieId[" + movieId + "]");

        boolean result = false;

        HttpURLConnection urlConnection = null;
        JsonReader reader = null;
        try {
            Uri builtUri = Uri.parse(MessageFormat.format(getContext().getString(R.string.tmdb_trailer_url), movieId))
                    .buildUpon()
                    .appendQueryParameter("api_key", getContext().getString(R.string.tmdb_api_key))
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "URL: " + url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            result = parseResultValues(movieId, reader);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return result;
    }

    private boolean parseResultValues(String movieId, JsonReader reader) throws IOException {
        int inserted = 0;
        while (reader.hasNext()) {
            List<ContentValues> trailerList = new ArrayList();

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("results") && reader.peek() != JsonToken.NULL) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        trailerList.add(parseContentValues(movieId, reader));
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            inserted += insertDataIntoContentProvider(trailerList);
        }
        Log.d(LOG_TAG, "Found [" + inserted + "] trailers");
        return inserted > 0;
    }

    private int insertDataIntoContentProvider(List<ContentValues> moviesList) {
        int result = 0;
        if (moviesList.size() > 0) {
            result = getContext().getContentResolver().bulkInsert(
                    TrailersEntry.CONTENT_URI,
                    moviesList.toArray(new ContentValues[moviesList.size()]));
        }
        return result;
    }

    @NonNull
    private ContentValues parseContentValues(String movieId, JsonReader reader) throws IOException {
        reader.beginObject();

        ContentValues trailerValues = new ContentValues();
        trailerValues.put(TrailersEntry.COLUMN_MOVIE_ID, movieId);
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                trailerValues.put(TrailersEntry.COLUMN_TRAILER_ID, reader.nextString());
            } else if (name.equals("iso_639_1")) {
                trailerValues.put(TrailersEntry.COLUMN_ISO_639_1, reader.nextString());
            } else if (name.equals("key")) {
                trailerValues.put(TrailersEntry.COLUMN_KEY, reader.nextString());
            } else if (name.equals("name")) {
                trailerValues.put(TrailersEntry.COLUMN_NAME, reader.nextString());
            } else if (name.equals("site")) {
                trailerValues.put(TrailersEntry.COLUMN_SITE, reader.nextString());
            } else if (name.equals("size")) {
                trailerValues.put(TrailersEntry.COLUMN_SIZE, reader.nextInt());
            } else if (name.equals("type")) {
                trailerValues.put(TrailersEntry.COLUMN_TYPE, reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return trailerValues;
    }

    public static void syncImmediately(Context context, String movieId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (movieId != null) {
            bundle.putString(MOVIE_ID, movieId);
        }
        ContentResolver.requestSync(getSyncAccount(context, movieId),
                context.getString(R.string.content_authority_trailers), bundle);
    }

    public static Account getSyncAccount(Context context, String movieId) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context, movieId);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context, String movieId) {
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority_trailers), true);
        syncImmediately(context, movieId);
    }

}
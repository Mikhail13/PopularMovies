package za.co.mikhails.nanodegree.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ThemoviedbSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static ThemoviedbSyncAdapter sThemoviedbSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sThemoviedbSyncAdapter == null) {
                sThemoviedbSyncAdapter = new ThemoviedbSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sThemoviedbSyncAdapter.getSyncAdapterBinder();
    }
}
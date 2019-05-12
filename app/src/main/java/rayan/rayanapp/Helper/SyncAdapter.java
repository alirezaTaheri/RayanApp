package rayan.rayanapp.Helper;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final String TAG = "Sync_Adapter";
    private final ContentResolver resolver;

    public SyncAdapter(Context c, boolean autoInit) {
        this(c, autoInit, false);
    }
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.w(TAG, "Starting synchronization...");

//        try {
//            // Synchronize our news feed
//
//            // Add any other things you may want to sync
//
//        } catch (IOException ex) {
//            Log.e(TAG, "Error synchronizing!", ex);
//            syncResult.stats.numIoExceptions++;
//        } catch (JSONException ex) {
//            Log.e(TAG, "Error synchronizing!", ex);
//            syncResult.stats.numParseExceptions++;
//        } catch (RemoteException |OperationApplicationException ex) {
//            Log.e(TAG, "Error synchronizing!", ex);
//            syncResult.stats.numAuthExceptions++;
//        }
//
//        Log.w(TAG, "Finished synchronization!");
    }

}

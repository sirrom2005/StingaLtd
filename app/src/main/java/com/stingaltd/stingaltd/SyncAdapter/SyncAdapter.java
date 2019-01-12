package com.stingaltd.stingaltd.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.R;
import com.stingaltd.stingaltd.SyncData.SyncData;

public class SyncAdapter extends AbstractThreadedSyncAdapter
{
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long SYNC_INTERVAL_IN_MINUTES = 15L;
    private static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        Context c = getContext();
        int id = extras.getInt(Common.POST_WORK_ID, 0);

        Log.d(Common.LOG_TAG, "Starting sync with id# " + id);

        SyncData.DeletePhoto(c, id);
        SyncData.UploadPhoto(c, id);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     * @param WorkId
     */
    public static void syncImmediately(Context context, int WorkId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(Common.POST_WORK_ID, WorkId);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet. If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(final Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));
        // If the password doesn't exist, the account doesn't exist
        if(null == accountManager.getPassword(newAccount)){
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
            Log.e(Common.LOG_TAG, "Add Sync Account " + newAccount);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context);
        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
    }

    private static void configurePeriodicSync(Context context)
    {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        ContentResolver.addPeriodicSync(
                account,
                authority,
                new Bundle(),
                SYNC_INTERVAL);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}

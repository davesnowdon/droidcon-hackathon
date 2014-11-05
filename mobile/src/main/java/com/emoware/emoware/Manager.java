package com.emoware.emoware;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Co-ordinate background communication and actions
 */
public class Manager extends IntentService {

    GoogleApiClient dataClient;

    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    public Manager() {
        super("Manager");
    }



    protected void start() {
        dataClient.connect();
    }

    protected void stop() {
        dataClient.disconnect();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

        /*
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);

                        if (mResolvingError) {
                            // Already attempting to resolve an error.
                            return;
                        } else if (result.hasResolution()) {
                            try {
                                mResolvingError = true;
                                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                            } catch (IntentSender.SendIntentException e) {
                                // There was an error with the resolution intent. Try again.
                                dataClient.connect();
                            }
                        } else {
                            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
                            showErrorDialog(result.getErrorCode());
                            mResolvingError = true;
                        }
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    public void sendBitmapToWearable(Bitmap bitmap) {
        if(null != bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            Log.d(TAG, "requestLandingSiteImage loaded. Bitmap size: " +
                    bitmap.getWidth() + " x " + bitmap.getHeight());

            Asset landingSiteAsset = Asset.createFromBytes(stream.toByteArray());

            PutDataMapRequest dataMap =
                    PutDataMapRequest.create(Constant.LANDING_SITE_IMAGE_RESPONSE);
            dataMap.getDataMap().putAsset(Constant.KEY_LANDING_SITE_IMAGE,
                    landingSiteAsset);
            dataMap.getDataMap().putLong(Constant.KEY_TIMESTAMP, new Date().getTime());

            PutDataRequest request = dataMap.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                    .putDataItem(dataClient, request);
            DataApi.DataItemResult result = pendingResult.await();
            Log.d(TAG, "requestLandingSiteImage. Bitmap asset set: " +
                    result.getDataItem().getUri());
        } else {
            Log.d(TAG, "Loading bitmap failed.");
        }
    }

    // Creates a dialog for an error message
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    // Called from ErrorDialogFragment when the dialog is dismissed.
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    // A fragment to display an error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((Main)getActivity()).onDialogDismissed();
        }
    }
    */

}

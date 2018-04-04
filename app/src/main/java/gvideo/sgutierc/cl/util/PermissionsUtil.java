package gvideo.sgutierc.cl.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;

import gvideo.sgutierc.cl.videorecorder.R;

/**
 * Created by sgutierc on 23-03-2018.
 */

public class PermissionsUtil {

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    public static boolean shouldShowRequestPermissionRationale(String[] permissions, Activity activity) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    public static boolean shouldShowRequestPermissionRationale(String[] permissions, Fragment fragment) {
        for (String permission : permissions) {
            if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPermissionsGranted(String[] permissions, Activity activity) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Requests permissions needed for recording video.
     */
    public static void requestVideoPermissions(String[] permissions,int requestCode,Activity activity) {
        if (PermissionsUtil.shouldShowRequestPermissionRationale(permissions, activity)) {
            new PermissionsUtil.ConfirmationDialog().setPermissions(permissions).show(activity.getFragmentManager(), "dialog");
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }



    public static class ConfirmationDialog extends DialogFragment {

        private String[] permissions;
        private int reqCode;
        public ConfirmationDialog setPermissions(String[] permissions) {
            this.permissions = permissions;
            return this;
        }

        public ConfirmationDialog setReqCode(int code){
            this.reqCode=code;
            return this;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_request)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(parent, permissions,
                                    reqCode);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    parent.getActivity().finish();
                                }
                            })
                    .create();
        }

    }
}

/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gvideo.sgutierc.cl.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.util.Comparator;

import gvideo.sgutierc.cl.util.PermissionsUtil;
import gvideo.sgutierc.cl.videorecorder.LocationEngine;
import gvideo.sgutierc.cl.videorecorder.R;
import gvideo.sgutierc.cl.videorecorder.VideoEngine;

public class Camera2VideoFragment extends Fragment
        implements View.OnClickListener, FragmentCompat.OnRequestPermissionsResultCallback, VideoEngine.PathProvider {

    private static final int MY_REQUEST_CODE = 100;

    private static final String TAG = "Camera2VideoFragment";
    private static final String FRAGMENT_DIALOG = "dialog";

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private LocationEngine locationEngine;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * Button to record video
     */
    private Button mButtonVideo;

    public static Camera2VideoFragment newInstance() {
        return new Camera2VideoFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2_video, container, false);
    }

    private VideoEngine videoEngine;

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        mButtonVideo = (Button) getActivity().findViewById(R.id.recordButton);
        mButtonVideo.setOnClickListener(this);
        getActivity().findViewById(R.id.infoButton).setOnClickListener(this);
        reviewPermissions();
    }

    private void reviewPermissions() {
        if (PermissionsUtil.hasPermissionsGranted(VIDEO_PERMISSIONS, this.getActivity()) == false) {
            PermissionsUtil.requestVideoPermissions(VIDEO_PERMISSIONS, MY_REQUEST_CODE, this.getActivity());
            return;
        } else
            startMagic();
    }

    private void startMagic() {
        if (videoEngine == null) {//new initiation, lets start the preview..
            this.videoEngine = new VideoEngine(getActivity(), mTextureView, this, locationEngine);
            videoEngine.startPreview();
        } else if (videoEngine.isRecording() == false) //comming back from pause.. will check if resume recording or previewing
            videoEngine.resumePreview();
        else
            videoEngine.resumeRecording();
    }

    @Override
    public void onResume() {
        super.onResume();
        reviewPermissions();
    }

    @Override
    public void onPause() {
        if (videoEngine != null)
            if (videoEngine.isRecording() == false)
                videoEngine.pausePreview();
            else
                videoEngine.pauseRecording();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recordButton: {
                if (videoEngine.isRecording()) {
                    videoEngine.stopRecording();
                } else {
                    videoEngine.startRecording();
                }
                break;
            }
            case R.id.infoButton: {
                Activity activity = getActivity();
                if (null != activity) {
                    new AlertDialog.Builder(activity)
                            .setMessage(R.string.intro_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean granted = true;
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length == VIDEO_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        ErrorDialog.newInstance(getString(R.string.permission_request))
                                .show(getChildFragmentManager(), FRAGMENT_DIALOG);
                        granted = false;
                        break;
                    }
                }
            } else {
                granted = false;
                ErrorDialog.newInstance(getString(R.string.permission_request))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            granted = false;
            PermissionsUtil.requestVideoPermissions(VIDEO_PERMISSIONS, MY_REQUEST_CODE, this.getActivity());
        }

        if (granted) {
            startMagic();
        }
    }


    @Override
    public String getPath() {
        return this.getVideoFilePath(getActivity());
    }

    private String getVideoFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + System.currentTimeMillis() + ".mp4";
    }


    public void setLocationEngine(LocationEngine locationEngine) {
        this.locationEngine = locationEngine;
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    public static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }


}

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

package gvideo.sgutierc.cl;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import gvideo.sgutierc.cl.videorecorder.LocationEngine;
import gvideo.sgutierc.cl.location.LocationRecorder;
import gvideo.sgutierc.cl.videorecorder.R;
import gvideo.sgutierc.cl.view.Camera2VideoFragment;
import gvideo.sgutierc.cl.view.GMapFragment;

public class CameraActivity extends Activity {
    private LocationEngine locationEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mantiene pantalla siempre en vision portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (null == savedInstanceState) {

            locationEngine = new LocationEngine(this);

            //add camera fragment
            Camera2VideoFragment cameraFragment = Camera2VideoFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.container, cameraFragment).commit();

            //then add map fragment
            GMapFragment mapFragment = GMapFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.mapContainer, mapFragment).commit();

            //and now start listening locations
            LocationRecorder recorder = new LocationRecorder(this);
            recorder.init();
            locationEngine.addHandler(mapFragment);
            locationEngine.addHandler(recorder);
            locationEngine.startListening();

        }
    }

}

/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java.facedetector;

import static com.google.common.primitives.Floats.min;
import static java.lang.Float.max;

import android.content.Context;
import android.graphics.PointF;
import androidx.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import java.util.List;
import java.util.Locale;

/** Face Detector Demo. */
public class FaceDetectorProcessor extends VisionProcessorBase<List<Face>> {

  private static final String TAG = "FaceDetectorProcessor";

  private final FaceDetector detector;
  public static float x_diff;
  public static float y_diff;

  public FaceDetectorProcessor(Context context) {
    this(
        context,
        new FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build());
  }

  public FaceDetectorProcessor(Context context, FaceDetectorOptions options) {
    super(context);
    Log.v(MANUAL_TESTING_LOG, "Face detector options: " + options);
    detector = FaceDetection.getClient(options);
  }

  @Override
  public void stop() {
    super.stop();
    detector.close();
  }

  @Override
  protected Task<List<Face>> detectInImage(InputImage image) {
    return detector.process(image);
  }

  @Override
  protected void onSuccess(@NonNull List<Face> faces, @NonNull GraphicOverlay graphicOverlay) {
    for (Face face : faces) {
      if(face != faces.get(0)) continue;
      float eulerX = face.getHeadEulerAngleX();
      float eulerY = face.getHeadEulerAngleY();
      float eulerZ = face.getHeadEulerAngleZ();
      Log.d("TMAX", "eulerX,Y,Z: (" +eulerX+","+eulerY+","+eulerZ+")");
      if(eulerX > 17.0f) y_diff = max(20.0f, eulerX-17.0f);
      else if(eulerX < -2.0f) y_diff = min(-20.0f,eulerX+2.0f);
      else y_diff = 0.0f;
      if(eulerY > 20.0f) x_diff = max(1.0f,eulerY-20.0f);
      else if(eulerY < -20.0f) x_diff = min(- 1.0f,eulerY+20.0f);
      else x_diff = 0.0f;
    }
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Face detection failed " + e);
  }
}

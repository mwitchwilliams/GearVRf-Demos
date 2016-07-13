/* Copyright 2016 Samsung Electronics Co., LTD
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

package org.gearvrf.x3ddemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;

import org.gearvrf.FutureWrapper;
import org.gearvrf.GVRActivity;
//import org.gearvrf.FutureWrapper;
import org.gearvrf.GVRAndroidResource;
//import org.gearvrf.GVRBaseSensor;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVREventReceiver;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRResourceVolume;
import org.gearvrf.GVRPicker.GVRPickedObject;
//import org.gearvrf.GVRRenderPass.GVRCullFaceEnum;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.keyframe.GVRAnimationBehavior;
import org.gearvrf.animation.keyframe.GVRAnimationChannel;
import org.gearvrf.animation.keyframe.GVRKeyFrameAnimation;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRModelSceneObject;
//import org.gearvrf.util.GazeController;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.scene_objects.GVRViewSceneObject;
import org.gearvrf.scene_objects.view.GVRWebView;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScreenshot3DCallback;
import org.gearvrf.GVRScreenshotCallback;
import org.gearvrf.GVRScript;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;
import org.gearvrf.GVRTransform;
//import org.gearvrf.IEventReceiver;
//import org.gearvrf.NativeMesh;
import org.gearvrf.utility.Threads;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;

import org.gearvrf.x3d.*;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class X3DparserScript extends GVRScript
{

  private static final String TAG = "X3D Parser Script";
  private GVRContext mGVRContext = null;

  public GVRAnimationEngine mAnimationEngine;
  public List<GVRAnimation> mAnimations = new ArrayList<GVRAnimation>();

  X3Dobject x3dObject = null;
  GVRScene scene = null;

  public GVRSceneObject currentPickedObject = null;
  public boolean tappedObject = false;

  public X3DparserScript(X3DparserActivity activity)
  {
  }

  public void onInit(GVRContext gvrContext)
  {
    mGVRContext = gvrContext;


    scene = gvrContext.getNextMainScene(new Runnable()
    {
      @Override
      public void run()
      {
        for (GVRAnimation animation : mAnimations)
        {
            animation.start(mAnimationEngine);
        }
      }
    });

    mAnimationEngine = gvrContext.getAnimationEngine();
    scene.getMainCameraRig().getLeftCamera().setBackgroundColor(Color.BLACK);
    scene.getMainCameraRig().getRightCamera().setBackgroundColor(Color.BLACK);

    GVRModelSceneObject model = new GVRModelSceneObject(mGVRContext);
    //String filename = "animation01.x3d";
    // String filename = "animation02.x3d";
    // String filename = "animation03.x3d";
    // String filename = "animation04.x3d";
    //String filename = "animation05.x3d";
    // String filename = "animation06.x3d";
    // String filename = "animation07.x3d";
    // String filename = "animation08.x3d";
    // String filename = "animation09.x3d";
    // String filename = "animation_center10.x3d";
    // String filename = "animation_scale01.x3d";
    // String filename = "backgroundtexturemap01.x3d";
    // String filename = "boxesspheres.x3d";
    // String filename = "conered.x3d";
     String filename = "cylinders.x3d";
    // String filename = "directionallight1.x3d";
    // String filename = "bscontact04.x3d";
     //String filename = "elevationgrid.x3d";
    // String filename = "emissivecolor.x3d";
    // String filename = "exponentcone.x3d";
    // String filename = "exponentplane.x3d";
    // String filename = "helloworldtext.x3d";
    // String filename = "inlinedemo01.x3d";
    // String filename = "levelofdetail01.x3d";
    // String filename = "levelofdetail02.x3d";
    // String filename = "levelofdetail03.x3d";
//     String filename = "navigationinfo.x3d";
    //String filename = "plane.x3d";
    //String filename = "planemore.x3d";
    // String filename = "planetexturexform.x3d";
    // String filename = "texturecoordinatetest.x3d";
    // String filename = "texturecoordinatetestsubset.x3d";
    // String filename = "texturecoordinatetestsubset2.x3d";
    // String filename = "text-lod-demo.x3d";
    // String filename = "lighttest1.x3d";
    // String filename = "pointlighttest.x3d";
    // String filename = "pointlightsimple.x3d";
    // String filename = "pointlightmultilights.x3d";
    // String filename = "spotlighttest1.x3d";
    // String filename = "spotlighttest2.x3d";
    // String filename = "spotlighttest3.x3d";
    // String filename = "spotlighttest4.x3d";
    // String filename = "multiviewpoints01.x3d";
    // String filename = "multiviewpoints02.x3d";
    // String filename = "multiviewpoints03.x3d";
    // String filename = "multiviewpoints04.x3d";
    // String filename = "touchSensor.x3d";
    //String filename = "teapottorusdirlights.x3d";
    // String filename = "teapottorus.x3d";
    // String filename = "pointlightattenuationtest.x3d";
    // String filename = "usedef01.x3d";
    // String filename = "usedef02.x3d";
    // String filename = "usedef03.x3d";
    // String filename = "usedef04.x3d";
    // String filename = "usedef05.x3d";
    // String filename = "usedef06.x3d";
    //String filename = "usedefCoordinate1.x3d";
    //String filename = "usedef1Normal.x3d";
    //String filename = "usedefTextureCoord1.x3d";
    // String filename = "usedefprimitive01.x3d";
    // String filename = "usedeftext01.x3d";
    //String filename = "usedeftransform01.x3d";
    //String filename = "usedefTransform02.x3d";
    //String filename = "usedefTransform03.x3d";
    //   String filename = "usedefGroup01.x3d";
    // String filename = "usedefdirlight.x3d";
    // String filename = "usedefpointlight.x3d";
    // String filename = "usedefspotlight.x3d";
    // String filename = "opacitytest01.x3d";
    // String filename = "levelofdetailusedef01.x3d";
    // String filename = "levelofdetailusedef02.x3d";
    // String filename = "levelofdetailusedef03.x3d";
    // String filename = "viewpointAnimation01.x3d";
  //String filename = "bsconstact01.x3d";
//   String filename = "bsconstact02.x3d";
   //String filename = "popart.x3d";
    try
    {
      GVRCameraRig mainCameraRig = scene.getMainCameraRig();
      
      model = gvrContext.getAssetLoader().loadModel(filename, scene);
      List<GVRAnimation> animations = model.getAnimations();
      mAnimations = animations;

      GVRTransform newtrans = model.getCameraRig().getTransform();
      mainCameraRig.getOwnerObject().getTransform().setModelMatrix(newtrans.getLocalModelMatrix4f());
      
      int backgroundColor = model.getCameraRig().getLeftCamera().getBackgroundColor();
      mainCameraRig.getLeftCamera().setBackgroundColor(backgroundColor);
      mainCameraRig.getRightCamera().setBackgroundColor(backgroundColor);
      
      // check if a headlight was attached to the camera rig
      //   during parsing.  NavigationInfo node specifies this.
      // If 4 objects are attached to the camera rig, one is a
      //    dirLight.  Thus attach a dirLight to the main camera
      if ( model.getCameraRig().getChildrenCount() > 3 ) {
        GVRSceneObject headlightSceneObject = new GVRSceneObject(gvrContext);
        headlightSceneObject.setName("headlightSceneObject");
        GVRDirectLight headLight = new GVRDirectLight(gvrContext);
        headlightSceneObject.attachLight(headLight);
        headLight.setDiffuseIntensity(1, 1, 1, 1);
        mainCameraRig.addChildObject(headlightSceneObject);
      }
      
    }
    catch (FileNotFoundException e)
    {
      Log.d(TAG, "ERROR: FileNotFoundException: " + filename);
    }
    catch (IOException e)
    {
      Log.d(TAG, "Error IOException = " + e);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  } // end onInit()

  // @Override
  public void onStep()
  {
    FPSCounter.tick();
  }

  private boolean lastScreenshotLeftFinished = true;
  private boolean lastScreenshotRightFinished = true;
  private boolean lastScreenshotCenterFinished = true;
  private boolean lastScreenshot3DFinished = true;

  // mode 0: center eye; mode 1: left eye; mode 2: right eye
  public void captureScreen(final int mode, final String filename)
  {
    Threads.spawn(new Runnable()
    {
      public void run()
      {

        switch (mode)
        {
          case 0:
            if (lastScreenshotCenterFinished)
            {
              mGVRContext
                  .captureScreenCenter(newScreenshotCallback(filename, 0));
              lastScreenshotCenterFinished = false;
            }
            break;
          case 1:
            if (lastScreenshotLeftFinished)
            {
              mGVRContext.captureScreenLeft(newScreenshotCallback(filename, 1));
              lastScreenshotLeftFinished = false;
            }
            break;
          case 2:
            if (lastScreenshotRightFinished)
            {
              mGVRContext
                  .captureScreenRight(newScreenshotCallback(filename, 2));
              lastScreenshotRightFinished = false;
            }
            break;
        }
      }
    });
  }

  public void captureScreen3D(String filename)
  {
    if (lastScreenshot3DFinished)
    {
      mGVRContext.captureScreen3D(newScreenshot3DCallback(filename));
      lastScreenshot3DFinished = false;
    }
  }

  private GVRScreenshotCallback newScreenshotCallback(final String filename,
      final int mode)
  {
    return new GVRScreenshotCallback()
    {

      @Override
      public void onScreenCaptured(Bitmap bitmap)
      {
        if (bitmap != null)
        {
          File file = new File(Environment.getExternalStorageDirectory(),
              filename + ".png");
          FileOutputStream outputStream = null;
          try
          {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
          }
          catch (FileNotFoundException e)
          {
            e.printStackTrace();
          }
          finally
          {
            try
            {
              outputStream.close();
            }
            catch (IOException e)
            {
              e.printStackTrace();
            }
          }
        }
        else
        {
          Log.e("SampleActivity", "Returned Bitmap is null");
        }

        // enable next screenshot
        switch (mode)
        {
          case 0:
            lastScreenshotCenterFinished = true;
            break;
          case 1:
            lastScreenshotLeftFinished = true;
            break;
          case 2:
            lastScreenshotRightFinished = true;
            break;
        }
      }
    };
  }

  private GVRScreenshot3DCallback newScreenshot3DCallback(final String filename)
  {
    return new GVRScreenshot3DCallback()
    {

      @Override
      public void onScreenCaptured(Bitmap[] bitmapArray)
      {
        Log.d("SampleActivity", "Length of bitmapList: " + bitmapArray.length);
        if (bitmapArray.length > 0)
        {
          for (int i = 0; i < bitmapArray.length; i++)
          {
            Bitmap bitmap = bitmapArray[i];
            File file = new File(Environment.getExternalStorageDirectory(),
                filename + "_" + i + ".png");
            FileOutputStream outputStream = null;
            try
            {
              outputStream = new FileOutputStream(file);
              bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
            catch (FileNotFoundException e)
            {
              e.printStackTrace();
            }
            finally
            {
              try
              {
                outputStream.close();
              }
              catch (IOException e)
              {
                e.printStackTrace();
              }
            }
          }
        }
        else
        {
          Log.e("SampleActivity", "Returned Bitmap List is empty");
        }

        // enable next screenshot
        lastScreenshot3DFinished = true;
      }
    };
  }
}

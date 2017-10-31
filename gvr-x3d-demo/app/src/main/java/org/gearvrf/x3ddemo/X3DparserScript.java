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
import java.util.List;

import org.gearvrf.FutureWrapper;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRCursorController;
import org.gearvrf.GVRDirectLight;

import org.gearvrf.GVRLightBase;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMesh;
//import org.gearvrf.GVRShader;
import org.gearvrf.io.GVRControllerType;
import org.gearvrf.io.GVRInputManager;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScreenshot3DCallback;
import org.gearvrf.GVRScreenshotCallback;
import org.gearvrf.utility.Threads;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

/**
 *
 * @author m1.williams
 * X3d Demo comes with test files in the assets directory:
 *         cylindersandplanes.x3d, helloworldtext.x3d, multipleanimations.x3d,
 *         multiplepointlights.x3d, navigationinfo.x3d, teapotandtorus.x3d
 *
 *         In the line: "String filename = ", change to one of the above file
 *         names or supply you own.
 *
 */

public class X3DparserScript extends GVRMain
{

  private static final String TAG = X3DparserScript.class.getSimpleName();
  private GVRContext mGVRContext = null;
  GVRScene scene = null;

  public X3DparserScript(X3DparserActivity activity)
  {
  }
  public X3DparserScript()
  {
  }

  public void onInit(GVRContext gvrContext)
  {
    mGVRContext = gvrContext;

    scene = gvrContext.getMainScene();
    scene.getMainCameraRig().getLeftCamera().setBackgroundColor(Color.BLACK);
    scene.getMainCameraRig().getRightCamera().setBackgroundColor(Color.BLACK);

    GVRModelSceneObject model = new GVRModelSceneObject(mGVRContext);
    // X3D test files should be in the assets directory.
    // Replace 'filename' to view another .x3d file
    String filename = "cylindersandplanes.x3d";
    filename = "textureCoordTests.x3d";

    filename = "JavaScript_PerFrame_01_ProceduralAnim.x3d";
    //filename = "JavaScript_PerFrame_02_LaunchSphere.x3d";
    //filename = "JavaScript_PerFrame_03_TimeStamp.x3d";
    //filename = "JavaScript_PerFrame_04_GrabAnObject.x3d";
    filename = "JavaScript_PerFrame_05_LightControls.x3d";

    filename = "JavaScriptLightsOn.x3d";
    filename = "JavaScriptTransform.x3d";

     filename = "JavaScriptMoveColorPointLights.x3d";

        /*

    filename = "touchSensor1.x3d";

    filename = "touchSensor4.x3d";
    filename = "touchSensor5.x3d";
    filename = "anchor_newX3DFile_viewpoints.x3d";
    filename = "anchor_viewpoint.x3d";
    filename = "anchor_webPages.x3d";


    filename = "animation01.x3d";

    filename = "animation09.x3d";

    filename = "inlinedemo01.x3d";

    filename = "teapotandtorus.x3d";
    filename = "usedef1Normal.x3d";
    filename = "usedefCoordinate1.x3d";
    filename = "usedefTextureCoord1.x3d";
    filename = "usedef02.x3d";

    filename = "usedef03.x3d";
    filename = "usedef04.x3d";
    filename = "usedef05.x3d";
    filename = "usedef06.x3d";
    filename = "usedefdirlight.x3d";
    filename = "usedefGroup01.x3d";
    filename = "usedefpointlight.x3d";
    filename = "usedefprimitive01.x3d";
    filename = "usedeftext01.x3d";

    filename = "font_multipleFonts.x3d";
    filename = "font_sizeStyleJustification.x3d";
    filename = "font_def_use.x3d";

    filename = "levelofdetail02.x3d";
    filename = "levelofdetail03.x3d";
    filename = "levelofdetailusedef01.x3d";
    filename = "levelofdetailusedef02.x3d";
    filename = "levelofdetailusedef03.x3d";
    filename = "levelofdetailusedef04.x3d";
    filename = "levelofdetail_circle01.x3d";
    filename = "levelofdetail_mult_obj_Group.x3d";
    filename = "levelofdetail_mult_obj_Shape.x3d";
    filename = "levelofdetail_mult_obj_Transform.x3d";
    filename = "levelofdetail_mult_children.x3d";
    filename = "LOD_TransformContainInline.x3d";

    filename = "lod_TouchSensor.x3d";
    */
    //filename = "emissivecolor.x3d";


    try
    {
      GVRCameraRig mainCameraRig = scene.getMainCameraRig();

      model = gvrContext.getAssetLoader().loadModel(filename, scene);

      GVRSceneObject cursor = new GVRSceneObject(mGVRContext, 1.0f, 1.0f,
              mGVRContext.getAssetLoader().loadTexture(new GVRAndroidResource(mGVRContext, R.raw.cursor)));
      cursor.getTransform().setPosition(0.0f, 0.0f, -10.0f);
      cursor.getRenderData().setDepthTest(false);
      cursor.getRenderData().setRenderingOrder(100000);

      mainCameraRig.addChildObject(cursor);

      // check if a headlight was attached to the model's camera rig
      // during parsing, as specified by the NavigationInfo node.
      // If 4 objects are attached to the camera rig, one must be the
      // directionalLight. Thus attach a dirLight to the main camera

      /*


      if (GVRShader.isVulkanInstance()) // remove light on Vulkan
      {
        List<GVRLightBase> lights = model.getAllComponents(GVRLightBase.getComponentType());
        for (GVRLightBase l : lights)
        {
          GVRSceneObject owner = l.getOwnerObject();
          owner.getParent().removeChildObject(owner);
        }
      }
      else if (model.getCameraRig().getChildrenCount() > 3)
      {
        GVRSceneObject headlightSceneObject = new GVRSceneObject(gvrContext);
        headlightSceneObject.setName("headlightSceneObject");
        GVRDirectLight headLight = new GVRDirectLight(gvrContext);
        headlightSceneObject.attachLight(headLight);
        headLight.setDiffuseIntensity(1, 1, 1, 1);
        mainCameraRig.addChildObject(headlightSceneObject);
      }

      */

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

    GVRInputManager inputManager = mGVRContext.getInputManager();
    for (GVRCursorController controller : inputManager.getCursorControllers()) {
      if (controller.getControllerType() == GVRControllerType.GAZE) {
        controller.setPosition(0.0f, 0.0f, -10.0f);
      }
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
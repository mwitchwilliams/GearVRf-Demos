package org.gearvrf.x3ddemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import android.os.Environment;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRComponent;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTransform;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.Set;
import java.util.Stack;

/**
 * Created by m1.williams on 8/22/2016.
 *
 * Prototype for the x3d exporter
 */
public class X3Dexporter {

  private static final String TAG = "X3D Exporter";
  private final int INDENT_AMOUNT = 3;
  private final String INDENT_CHAR = " ";
  private final String startX3Dnode = "<X3D>\r\n";
  private final String startScenenode = "<Scene>\r\n";
  private final String endX3Dnode = "</X3D>\r\n";
  private final String endScenenode = "</Scene>\r\n";

  // These are defined in X3DObject but we don't have access here.  Deinfed here just for prototype testing
  public static final String TRANSFORM_ROTATION_ = "_Transform_Rotation_";
  public static final String TRANSFORM_TRANSLATION_ = "_Transform_Translation_";
  public static final String TRANSFORM_SCALE_ = "_Transform_Scale_";


  private GVRSceneObject currentSceneObject = null;
  private GVRContext gvrContext = null;
  private Stack<StackedGVRSceneObject> gvrSceneObjectStack = null;

  private String output;
  private int indent = 0;

  //Saved on the stack for when we return
  class StackedGVRSceneObject {
    int childNumber;
    GVRSceneObject gvrSceneObject;

    public StackedGVRSceneObject(GVRSceneObject gvrSceneObject, int childNumber) {
      this.gvrSceneObject = gvrSceneObject;
      this.childNumber = childNumber;
    }
  }

  private void WriteLine(String line)
  {
    for (int i = 0; i < indent; i++ ) {
      output += INDENT_CHAR;
    }
    // include the carriage return, line feed
    output += line + "\r\n";
  }
  private void WriteCharArrayLine(String line, char[] ifs) {
    for (int i = 0; i < indent; i++ ) {
      output += INDENT_CHAR;
    }
    output += line;
    for (int i = 0; i < ifs.length; i++ ) {
      output += (int) ifs[i] + " ";
      if ( i%3 == 2) {
        output += "-1 "; // add the end of the 3 vertices that make the face
      }
    }
    output = output.substring(0, output.length()-1) + "'\r\n" ;
  }
  private void WriteFloatArrayLine(String line, float[] values) {
    for (int i = 0; i < indent; i++ ) {
      output += INDENT_CHAR;
    }
    output += line;
    for (int i = 0; i < values.length; i++ ) {
      output += values[i] + " ";
    }
    output = output.substring(0, output.length()-1) + "'/>\r\n" ;
  }

  private void WriteAppearance(GVRMaterial gvrMaterial) {
    WriteLine("<Appearance>");
    indent += INDENT_AMOUNT;
    String line = "<Material";
    float[] diffuseColor = gvrMaterial.getDiffuseColor();
    line += " diffuseColor='" + diffuseColor[0] + " " + diffuseColor[1] + " " + diffuseColor[2] + "'";
    //TODO:     public float getOpacity()
    line += "/>";
    WriteLine(line);

    GVRTexture gvrTexture = gvrMaterial.getTexture("diffuseTexture");
    if (gvrTexture != null) {
      line = "<ImageTexture url='";
      int textureID = gvrTexture.getId();
      // Searching for ways to determine which texture map this might be.
//    GVRAndroidResource gvrAndroidResource = new GVRAndroidResource(gvrContext, textureID);
      //AssetManager assetManager = new AssetManager();
      Set<String> mySet = gvrMaterial.getTextureNames();
      GVRTexture gvrMainTexture = gvrMaterial.getMainTexture();
      line += "'/>";
      WriteLine(line);
    }

    line = "<TextureTransform/>";
    WriteLine(line);
    indent -= INDENT_AMOUNT;
    WriteLine("</Appearance>");
  }  // end WriteAppearance


  private void WriteBeginTransform() {
    String line = "<Transform";
    String translation;
    String rotation;
    String scale;

    //translation
    GVRTransform gvrTransform = currentSceneObject.getTransform();
    translation = " translation='" + gvrTransform.getPositionX() + " " + gvrTransform.getPositionY() + " " + gvrTransform.getPositionZ() + "'";
    if (!currentSceneObject.getName().isEmpty()) {
      if ( currentSceneObject.getName().indexOf(TRANSFORM_TRANSLATION_) != -1) {
        currentSceneObject = currentSceneObject.getChildByIndex(0);
        gvrTransform = currentSceneObject.getTransform();
      }
    }

    // rotation
    Quaternionf quaternion = new Quaternionf(gvrTransform.getRotationX(), gvrTransform.getRotationY(), gvrTransform.getRotationZ(), gvrTransform.getRotationW());
    AxisAngle4f axisAngle = new AxisAngle4f(quaternion);
    rotation = " rotation='" + axisAngle.x + " " + axisAngle.y + " " + axisAngle.z + " " + axisAngle.angle + "'";
    Log.e(TAG, " quaternion (xyzw)='" + quaternion.x + " " + quaternion.y + " " + quaternion.z + " " + quaternion.w + "'");
    Log.e(TAG, "    axisAngle test='" + axisAngle.x + " " + axisAngle.y + " " + axisAngle.z + " " + axisAngle.angle + "'");

    if (!currentSceneObject.getName().isEmpty()) {
      if ( currentSceneObject.getName().indexOf(TRANSFORM_ROTATION_) != -1) {
        currentSceneObject = currentSceneObject.getChildByIndex(0);
        gvrTransform = currentSceneObject.getTransform();
      }
    }

    // scale
    scale = " scale='" + gvrTransform.getScaleX() + " " + gvrTransform.getScaleY() + " " + gvrTransform.getScaleZ() + "'";
    if (!currentSceneObject.getName().isEmpty()) {
      if ( currentSceneObject.getName().indexOf(TRANSFORM_SCALE_) != -1) {
        currentSceneObject = currentSceneObject.getChildByIndex(0);
        gvrTransform = currentSceneObject.getTransform();
      }
    }
    // should be the actual object
    if (!currentSceneObject.getName().isEmpty()) {
      line += " DEF='" + currentSceneObject.getName() + "'";
    }
    line += translation + rotation + scale + ">";
    WriteLine(line);
    indent += INDENT_AMOUNT;
  }  //  end WriteBeginTransform

  private void WriteEndTransform() {
    indent -= INDENT_AMOUNT;
    WriteLine("</Transform>");
  }  //  end WriteEndTransform


  private void WriteShape() {
    // Write the IndexedFaceSet including Coordinate, TextureCoord, Normal, Primitives
    GVRRenderData gvrRenderData = currentSceneObject.getRenderData();
    if (gvrRenderData != null ) {
      // This Transform has a <SHAPE> node child s indicated by a GVRRenderingData with GVRMesh
      WriteLine("<Shape>");
      indent += INDENT_AMOUNT;

      WriteLine("<IndexedFaceSet");
      indent += INDENT_AMOUNT;

      GVRMesh gvrMesh = gvrRenderData.getMesh();
      char[] ifs = gvrMesh.getIndices();
      WriteCharArrayLine("coordIndex='", ifs);

      indent -= INDENT_AMOUNT;
      WriteLine(">");  // ending ">" of IndexedFaceSet
      indent += INDENT_AMOUNT;

      float[] vertices = gvrMesh.getVertices();
      WriteFloatArrayLine("<Coordinate point='", vertices);
      float[] textureCoord = gvrMesh.getTexCoords();
      WriteFloatArrayLine("<TextureCoordinate point='", textureCoord);
      float[] normals = gvrMesh.getNormals();
      WriteFloatArrayLine("<Normal vector='", normals);

      indent -= INDENT_AMOUNT;
      WriteLine("</IndexedFaceSet>");

      // write the Appearance, ImageTexture, Material and TextureTransform nodes
      WriteAppearance(gvrRenderData.getMaterial());

      indent -= INDENT_AMOUNT;
      WriteLine("</Shape>");
    }  // end if GVRRenderData that would contain GVRMesh
  }  //  end WriteShape

  private void GetNextSceneObject(boolean writeTransform) {
    if (currentSceneObject.getChildrenCount() != 0) {
      StackedGVRSceneObject stackedGVRSceneObject = new StackedGVRSceneObject(currentSceneObject, 0);
      gvrSceneObjectStack.push(stackedGVRSceneObject);
      currentSceneObject = currentSceneObject.getChildByIndex(0);
    }
    else {
      // traverse up the stack to find the next object
      boolean done = false;
      while (!done) {
        if (!gvrSceneObjectStack.isEmpty()) {
          StackedGVRSceneObject poppedGVRSceneObject = gvrSceneObjectStack.pop();
          if (poppedGVRSceneObject.gvrSceneObject.getChildrenCount() > (poppedGVRSceneObject.childNumber + 1)) {
            currentSceneObject = poppedGVRSceneObject.gvrSceneObject.getChildByIndex(poppedGVRSceneObject.childNumber + 1);
            StackedGVRSceneObject stackedGVRSceneObject = new StackedGVRSceneObject(poppedGVRSceneObject.gvrSceneObject, poppedGVRSceneObject.childNumber + 1);
            gvrSceneObjectStack.push(stackedGVRSceneObject);
            done = true;
          }
          if (writeTransform) {
            WriteEndTransform();
          }
          writeTransform = true;
        }
        else {
          currentSceneObject = null;
          done = true;
          if (writeTransform) {
            WriteEndTransform();
          }
          writeTransform = true;
        }
      }
    }
  }


  private void TraverseSceneGraph() {
    // test of 0'd quaternion
    Quaternionf quaternion = new Quaternionf(0,0,0,1);
    AxisAngle4f axisAngle = new AxisAngle4f(quaternion);
    Log.e(TAG, " quaternion (0 0 0 1)=aa '" + axisAngle.x + " " + axisAngle.y + " " + axisAngle.z + " " + axisAngle.angle + "'");
    quaternion = new Quaternionf(1,0,0,0);
    axisAngle = new AxisAngle4f(quaternion);
    Log.e(TAG, " quaternion (1 0 0 0)=aa '" + axisAngle.x + " " + axisAngle.y + " " + axisAngle.z + " " + axisAngle.angle + "'");
    quaternion = new Quaternionf(0,1,0,1);
    axisAngle = new AxisAngle4f(quaternion);
    Log.e(TAG, " quaternion (1 0 1 0)=aa '" + axisAngle.x + " " + axisAngle.y + " " + axisAngle.z + " " + axisAngle.angle + "'");
    axisAngle = new AxisAngle4f(0, 1, 0, 0);
    quaternion = new Quaternionf(axisAngle);
    Log.e(TAG, " AxisAngle (0 1 0 0)=q '" + quaternion.x + " " + quaternion.y + " " + quaternion.z + " " + quaternion.w + "'");
    quaternion = new Quaternionf(0,0,0,1);
    axisAngle = new AxisAngle4f(quaternion);
    Log.e(TAG, " quaternion (0 0 0 1)=aa '" + axisAngle.x + " " + axisAngle.y + " " + axisAngle.z + " " + axisAngle.angle + "'");
    Log.e(TAG, " ");
    Log.e(TAG, "-----------");
    axisAngle = new AxisAngle4f(0, 0, 1, 0);
    quaternion = new Quaternionf(axisAngle);
    Log.e(TAG, " AxisAngle (0 0 1 0)=q '" + quaternion.x + " " + quaternion.y + " " + quaternion.z + " " + quaternion.w + "'");
    axisAngle = new AxisAngle4f(quaternion);
    Log.e(TAG, " quaternion (return)=aa '" + axisAngle.x + " " + axisAngle.y + " " + axisAngle.z + " " + axisAngle.angle + "'");



    while (currentSceneObject != null) {
      if ( currentSceneObject.getComponent(GVRPointLight.getComponentType()) != null)
      {
        GVRTransform transform = currentSceneObject.getTransform();
        GVRPointLight gvrPointLight = (GVRPointLight) currentSceneObject.getComponent(GVRPointLight.getComponentType());
        float[] color = gvrPointLight.getDiffuseIntensity();
        WriteLine("<PointLight location='" + transform.getPositionX() + " " + transform.getPositionY() + " " + transform.getPositionZ() + "' color='" + color[0] + " " + color[1] + " " + color[2] + "'/>");
        GetNextSceneObject(false); // attaching Light required a Transform, but we won't be writing any Transform
      }
      else if (currentSceneObject instanceof GVRCylinderSceneObject) {
        WriteLine("<Shape>");
        indent += INDENT_AMOUNT;
        WriteLine("<Cylinder></Cylinder>");
        final GVRCylinderSceneObject gvrCylinderSceneObject = (GVRCylinderSceneObject) currentSceneObject;
        //TODO: can't access parameters for primitives
        WriteAppearance(currentSceneObject.getRenderData().getMaterial());
        indent -= INDENT_AMOUNT;
        WriteLine("</Shape>");
        GetNextSceneObject(false);
      }
      else if (currentSceneObject instanceof GVRSceneObject) {
        WriteBeginTransform();
        WriteShape();
        GetNextSceneObject(true);
      }
    }
  }  //  end TraverseSceneGraph

  public X3Dexporter(GVRContext gvrContext, String filename, GVRModelSceneObject model)
  {
    this.gvrContext = gvrContext;
    currentSceneObject = model;
    FileOutputStream outputStream;

    try {
      if (isExternalStorageWritable()) {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, filename);
        path.mkdirs();

        outputStream = new FileOutputStream(file, false);

        // skip past the root and main camera nodes which were added
        //    by GVRf and not part of the original X3D file.
        //currentSceneObject = currentSceneObject.getChildByIndex(1);

        // begin by adding the <X3D> and <Scene> nodes
        output = startX3Dnode;
        for (int i = 0; i < INDENT_AMOUNT; i++) {
          output += INDENT_CHAR;
        }
        output += startScenenode;
        indent = 2 * INDENT_AMOUNT;

        gvrSceneObjectStack = new Stack<>();
        TraverseSceneGraph();

        // end the file with </Scene></X3D> nodes
        for (int i = 0; i < INDENT_AMOUNT; i++) {
          output += INDENT_CHAR;
        }
        output += endScenenode + endX3Dnode;

        outputStream.write(output.getBytes() );
        outputStream.flush();
        outputStream.close();

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(gvrContext.getContext(),
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                  public void onScanCompleted(String path, Uri uri) {
                    Log.i(TAG, "Scanned " + path + ":");
                    Log.i(TAG, "-> uri=" + uri);
                  }
                });
      }
      else Log.d(TAG, "External Storage (SD Card) not available");
    }
    catch (ArrayIndexOutOfBoundsException e) {
      Log.d(TAG, "ArrayIndexOutOfBoundsException");
    }
    catch (Exception e) {
      Log.d(TAG, "File writing error");
      e.printStackTrace();
    }
  }

  /* Checks if external storage is available for read and write */
  public boolean isExternalStorageWritable() {
    //Environment environment = new Environment();
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }
}
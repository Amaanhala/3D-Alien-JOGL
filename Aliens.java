import gmaths.*;

import java.nio.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

public class Aliens {

  // Camera and lighting for the Aliens model
  private Camera camera;
  private Light[] lights;

  // Different parts of the alien model, represented as spheres
  private Model sphere, sphere2, sphere3, sphere4, sphere5;

  // Scene graph root node for the aliens
  private SGNode aliensRoot;

  // Initial position of the alien
  private float xPosition = 0;
  private float yPosition = 0;
  private float zPosition = 0;

  // Transformation nodes for moving tthe aliens and rotating their heads
  private TransformNode aliensMoveTranslate;
  private TransformNode headRotate;

  // Constructor for Aliens class
  /*
   * public Aliens(GL3 gl, Camera cameraIn, Light[] lightsIn, Texture t1, Texture
   * t2, Texture t3, Texture t4, Texture t5,
   * Texture t6, float initialXPosition, float initialYPosition, float
   * initialZPosition) {
   * this.camera = cameraIn;
   * this.lights = lightsIn;
   * this.xPosition = initialXPosition;
   * this.yPosition = initialYPosition;
   * this.zPosition = initialZPosition;
   * 
   * // Initialize transformation nodes for alien movement and head rotation
   * aliensMoveTranslate = new TransformNode("alien transform",
   * Mat4Transform.translate(xPosition, yPosition, zPosition));
   * headRotate = new TransformNode("head rotate",
   * Mat4Transform.rotateAroundX(0));
   * 
   * // Create different parts of the alien using spheres
   * sphere = makeSphere(gl, t1, t2); // Head
   * sphere2 = makeSphere(gl, t2, t3); // Eyes
   * sphere3 = makeSphere(gl, t3, t4); // Arms, Antenna
   * sphere4 = makeSphere(gl, t4, t5); // Ears
   * sphere5 = makeSphere(gl, t5, t6); // Body
   */

  // Constructor for Aliens class
  public Aliens(GL3 gl, Camera cameraIn, Light[] lightsIn, Texture t1, Texture t2, Texture t3, Texture t4, Texture t5,
      Texture t6, Texture t7, Texture t8, Texture t9, Texture t10, float initialXPosition, float initialYPosition,
      float initialZPosition) {
    this.camera = cameraIn;
    this.lights = lightsIn;
    this.xPosition = initialXPosition;
    this.yPosition = initialYPosition;
    this.zPosition = initialZPosition;

    // Initialize transformation nodes for alien movement and head rotation
    aliensMoveTranslate = new TransformNode("alien transform",
        Mat4Transform.translate(xPosition, yPosition, zPosition));
    headRotate = new TransformNode("head rotate", Mat4Transform.rotateAroundX(0));

    // Create different parts of the alien using spheres
    sphere = makeSphere(gl, t1, t6); // Head
    sphere2 = makeSphere(gl, t2, t7); // Eyes
    sphere3 = makeSphere(gl, t3, t8); // Arms, Antenna
    sphere4 = makeSphere(gl, t4, t9); // Ears
    sphere5 = makeSphere(gl, t5, t10); // Body

    // Define scale and position values for various parts of the alien
    float bodyScale = 3.5f; // Scale of the alien's body
    float headScale = 2f; // Scale of the alien's head
    float armScale = 0.5f; // Scale of the alien's arm
    float armPositionY = (bodyScale - armScale); // Y position for the arm
    float headOffset = (bodyScale / 2) + (headScale / 2); // Offset for the head position
    float armAttachOffset = 0.12f; // Offset for attaching the arm
    float armLength = bodyScale; // Length of the arm
    float earScaleX = armScale / 3; // X scale for the ear
    float earScaleY = armLength / 4; // Y scale for the ear
    float earScaleZ = armScale; // Z scale for the ear
    float earInset = 0.1f; // Inset for the ear
    float earPositionX = 0.5f; // X position for the ear
    float earPositionY = headScale / 4; // Y position for the ear
    float earOffsetFromHeadCenterY = headScale / 2; // Y offset for the ear from the center of the head
    float earOffsetFromHeadCenterX = headScale / 2; // X offset for the ear from the center of the head
    float eyeScale = 0.25f; // Scale of the eye
    float eyeOffsetX = 0.1f * headScale; // X offset for the eye
    float eyeOffsetY = 0.0f; // Y offset for the eye
    float eyeOffsetZ = headScale * 0.23f; // Z offset for the eye

    // Define scales for the antenna parts
    float antennaShaftScaleX = 0.08f; // Width of the antenna shaft
    float antennaShaftScaleY = 0.5f; // Length of the antenna shaft
    float antennaShaftScaleZ = 0.08f; // Depth of the antenna shaft
    float antennaTipScale = 0.2f; // Size of the antenna tip
    float antennaBaseHeight = headOffset + (headScale * -1f); // Base height of the antenna

    // Root node for the alien scene graph
    aliensRoot = new NameNode("root");
    aliensMoveTranslate = new TransformNode("alien transform", Mat4Transform.translate(xPosition, 0, 0));

    // Node to translate the alien model upwards
    TransformNode aliensTranslate = new TransformNode("alien transform", Mat4Transform.translate(0, bodyScale, 0));

    // Body of the alien
    NameNode body = new NameNode("body");
    // Scale the body to the appropriate size
    TransformNode bodyTransform = new TransformNode("body transform",
        Mat4Transform.scale(bodyScale, bodyScale, bodyScale));
    // Model node for the body
    ModelNode bodyShape = new ModelNode("Sphere(body)", sphere5);

    // Now create the transformation for the head
    NameNode head = new NameNode("head");
    Mat4 m = Mat4Transform.translate(0, headOffset, 0);
    m = Mat4.multiply(m, Mat4Transform.scale(headScale, headScale, headScale));
    TransformNode headTransform = new TransformNode("head transform",
        Mat4.multiply(Mat4Transform.translate(0, headOffset, 0), Mat4Transform.scale(headScale, headScale, headScale)));
    ModelNode headShape = new ModelNode("Sphere(head)", sphere);

    // Left arm of the alien
    NameNode leftarm = new NameNode("left arm");
    // Positioning and scaling for the left arm
    Mat4 mLeftArm = Mat4Transform.translate(-(bodyScale / 2 + armAttachOffset + armScale / 2 - 0.02f),
        (bodyScale / 2) - armLength / 2 + 0.15f, 0.01f);
    mLeftArm = Mat4.multiply(mLeftArm, Mat4Transform.rotateAroundZ(30.0f)); // Initial rotation
    mLeftArm = Mat4.multiply(mLeftArm, Mat4Transform.scale(armScale / 2, armLength / 2, armScale / 2));
    TransformNode leftArmTransform = new TransformNode("leftarm transform", mLeftArm);
    // Model node for the left arm
    ModelNode leftArmShape = new ModelNode("Sphere(left arm)", sphere3);

    // Right arm of the alien
    NameNode rightarm = new NameNode("right arm");
    // Positioning and scaling for the right arm
    Mat4 mRightArm = Mat4Transform.translate(bodyScale / 2 + armAttachOffset + armScale / 2 - 0.02f,
        (bodyScale / 2) - armLength / 2 + 0.15f, 0.01f);
    mRightArm = Mat4.multiply(mRightArm, Mat4Transform.rotateAroundZ(-30.0f)); // Initial rotation
    mRightArm = Mat4.multiply(mRightArm, Mat4Transform.scale(armScale / 2, armLength / 2, armScale / 2));
    TransformNode rightArmTransform = new TransformNode("rightarm transform", mRightArm);
    // Model node for the right arm
    ModelNode rightArmShape = new ModelNode("Sphere(right arm)", sphere3);

    // Left ear of the alien
    NameNode leftEar = new NameNode("left ear");
    // Positioning and scaling for the left ear
    Mat4 mLeftEar = Mat4Transform.translate(-earPositionX, earPositionY, 0);
    mLeftEar = Mat4.multiply(mLeftEar, Mat4Transform.scale(earScaleX, earScaleY, earScaleZ));
    TransformNode leftEarTransform = new TransformNode("left ear transform", mLeftEar);
    // Model node for the left ear
    ModelNode leftEarShape = new ModelNode("Sphere(left ear)", sphere4);

    // Right ear of the alien
    NameNode rightEar = new NameNode("right ear");
    // Positioning and scaling for the right ear
    Mat4 mRightEar = Mat4Transform.translate(earPositionX, earPositionY, 0);
    mRightEar = Mat4.multiply(mRightEar, Mat4Transform.scale(earScaleX, earScaleY, earScaleZ));
    TransformNode rightEarTransform = new TransformNode("right ear transform", mRightEar);
    // Model node for the right ear
    ModelNode rightEarShape = new ModelNode("Sphere(right ear)", sphere4);

    // Create and transform the left eye of the alien
    NameNode leftEye = new NameNode("left eye");
    // Position and scale the left eye. The eye is translated to the left side of
    // the head and placed appropriately.
    Mat4 mLeftEye = Mat4.multiply(Mat4Transform.translate(-eyeOffsetX, eyeOffsetY, eyeOffsetZ),
        Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    TransformNode leftEyeTransform = new TransformNode("left eye transform", mLeftEye);
    // Add the model for the left eye, using the sphere mesh and textures
    ModelNode leftEyeShape = new ModelNode("Sphere(left eye)", sphere2);

    // Create and transform the right eye of the alien
    NameNode rightEye = new NameNode("right eye");
    // Position and scale the right eye. This eye is translated to the right side of
    // the head with the same scaling as the left.
    Mat4 mRightEye = Mat4.multiply(Mat4Transform.translate(eyeOffsetX, eyeOffsetY, eyeOffsetZ),
        Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    TransformNode rightEyeTransform = new TransformNode("right eye transform", mRightEye);
    // Add the model for the right eye, using the same approach as the left eye
    ModelNode rightEyeShape = new ModelNode("Sphere(right eye)", sphere2);

    // Create and transform the antenna shaft
    NameNode antennaShaft = new NameNode("antenna shaft");
    // Position the antenna shaft above the head and scale it to create an elongated
    // shape
    Mat4 mAntennaShaft = Mat4Transform.translate(0, antennaBaseHeight, 0);
    mAntennaShaft = Mat4.multiply(mAntennaShaft,
        Mat4Transform.scale(antennaShaftScaleX, antennaShaftScaleY, antennaShaftScaleZ));
    TransformNode antennaShaftTransform = new TransformNode("antenna shaft transform", mAntennaShaft);
    // Define the model for the antenna shaft
    ModelNode antennaShaftShape = new ModelNode("Sphere(antenna shaft)", sphere3);

    // Create and transform the antenna tip
    NameNode antennaTip = new NameNode("antenna tip");
    // Position the antenna tip at the top of the shaft and scale it to form a
    // sphere
    Mat4 mAntennaTip = Mat4Transform.translate(0, antennaBaseHeight + (antennaShaftScaleY * antennaShaftScaleY), 0);
    mAntennaTip = Mat4.multiply(mAntennaTip, Mat4Transform.scale(antennaTipScale, antennaTipScale, antennaTipScale));
    TransformNode antennaTipTransform = new TransformNode("antenna tip transform", mAntennaTip);
    // Define the model for the antenna tip
    ModelNode antennaTipShape = new ModelNode("Sphere(antenna tip)", sphere3);

    // Construct the scene graph for the alien
    // Root of the scene graph for the alien model
    aliensRoot.addChild(aliensMoveTranslate);

    // Node for translating the entire alien model
    aliensMoveTranslate.addChild(aliensTranslate);

    // Node for additional transformations of the alien
    aliensTranslate.addChild(body);

    // Adding the body node and its transformation
    body.addChild(bodyTransform);
    bodyTransform.addChild(bodyShape);

    // Adding the head node, including rotation and transformation nodes
    body.addChild(head);
    head.addChild(headRotate); // Node for rotating the head
    headRotate.addChild(headTransform);
    headTransform.addChild(headShape); // Node for the head's shape

    // Adding the left arm with its transformation and shape
    body.addChild(leftArmTransform);
    leftArmTransform.addChild(leftArmShape);

    // Adding the right arm with its transformation and shape
    body.addChild(rightArmTransform);
    rightArmTransform.addChild(rightArmShape);

    // Adding the left ear with its transformation and shape
    headTransform.addChild(leftEarTransform);
    leftEarTransform.addChild(leftEarShape);

    // Adding the right ear with its transformation and shape
    headTransform.addChild(rightEarTransform);
    rightEarTransform.addChild(rightEarShape);

    // Adding the left eye with its transformation and shape
    headTransform.addChild(leftEyeTransform);
    leftEyeTransform.addChild(leftEyeShape);

    // Adding the right eye with its transformation and shape
    headTransform.addChild(rightEyeTransform);
    rightEyeTransform.addChild(rightEyeShape);

    // Adding the antenna shaft with its transformation and shape
    headTransform.addChild(antennaShaftTransform);
    antennaShaftTransform.addChild(antennaShaftShape);

    // Adding the antenna tip with its transformation and shape
    // The antenna tip is attached to the antenna shaft node
    antennaShaftTransform.addChild(antennaTipTransform);
    headTransform.addChild(antennaTipTransform);
    antennaTipTransform.addChild(antennaTipShape);

    // Update the entire scene graph
    aliensRoot.update();
  }

  // Method to create a sphere model
  private Model makeSphere(GL3 gl, Texture t1, Texture t2) {
    String name = "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_2t.txt");
    shader.use(gl);

    // Set light intensity scale in shader (if needed)
    shader.setFloat(gl, "lightIntensityScale", 0.8f);

    // Material properties of the sphere
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f),
        new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

    // Assign diffuse and specular textures
    Texture diffuseTexture = t1;
    Texture specularTexture = t2;

    // Bind textures to their respective texture units
    gl.glActiveTexture(GL.GL_TEXTURE0);
    diffuseTexture.bind(gl);
    shader.setInt(gl, "first_texture", 0);

    gl.glActiveTexture(GL.GL_TEXTURE1);
    specularTexture.bind(gl);
    shader.setInt(gl, "second_texture", 1);

    // Create and return the sphere model with the specified textures and material
    Model sphere = new Model(name, mesh, Mat4Transform.scale(4, 4, 4),
        shader, material, lights, camera, diffuseTexture, specularTexture);

    return sphere;
  }

  // Method to render the scene
  public void render(GL3 gl) {
    Shader shader = sphere.getShader();
    shader.use(gl);
    shader.setFloat(gl, "lightIntensityScale", 1f); // Adjust light intensity scale for rendering
    lights[2].render(gl); // Render the spotlight

    // Draw the entire scene graph
    aliensRoot.draw(gl);
  }

  // Method to create diagonal movement for alien1
  public void diagonalMovement(double elapsedTime) {
    // Calculate a rocking motion angle based on elapsed time
    float angle = (float) Math.sin(elapsedTime) * 5;

    // Create a rotation matrix around the Z-axis
    Mat4 rotation = Mat4Transform.rotateAroundZ(angle);

    // Combine the rotation with the current position translation
    Mat4 combinedTransform = Mat4.multiply(Mat4Transform.translate(xPosition, yPosition, zPosition), rotation);

    // Apply the combined transformation to the alien movement node
    aliensMoveTranslate.setTransform(combinedTransform);
    aliensMoveTranslate.update();
  }

  // Initial position for alien2
  private Vec3 initialPosition = new Vec3(5.5f, 0.0f, 0f);

  // Method to create diagonal movement for alien2
  public void diagonalMovement2(double elapsedTime) {
    float angle = (float) Math.sin(elapsedTime) * 5; // Calculate rocking motion angle
    Mat4 rotation = Mat4Transform.rotateAroundZ(angle); // Rotation matrix around Z-axis
    Mat4 combinedTransform = Mat4.multiply(Mat4Transform.translate(initialPosition), rotation); // Combine rotation with
                                                                                                // position

    // Apply the combined transformation to the alien movement node
    aliensMoveTranslate.setTransform(combinedTransform);
    aliensMoveTranslate.update();
  }

  // Method to animate the head rotation
  public void headRotate(double elapsedTime) {
    float angle = (float) Math.sin(elapsedTime) * 8; // Calculate rotation angle
    Mat4 rotation = Mat4Transform.rotateAroundZ(angle); // Create rotation matrix around Z-axis

    // Apply the rotation to the head rotation node
    headRotate.setTransform(rotation);
    headRotate.update();
    aliensRoot.update(); // Update the entire scene graph to apply changes
  }

  // Dispose method to clean up resources
  public void dispose(GL3 gl) {
    sphere.dispose(gl);
    sphere2.dispose(gl);
    sphere3.dispose(gl);
    sphere4.dispose(gl);
    sphere5.dispose(gl); // Dispose all sphere models
  }
}

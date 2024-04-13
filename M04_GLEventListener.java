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

public class M04_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;
  private double startTime;

  // Models and their transformation nodes
  private Model newSphere;
  private TransformNode newSphereTransform;
  private Model topSphere;
  private TransformNode topSphereTransform;

  // Constructor: Initializes the camera position
  public M04_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f, 12f, 18f));
  }

  // Initialization method called when the GL context is first created
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set clear color for the drawing surface
    gl.glClearDepth(1.0f); // Set the depth value used when clearing the depth buffer
    gl.glEnable(GL.GL_DEPTH_TEST); // Enable depth testing
    gl.glDepthFunc(GL.GL_LESS); // Specify depth testing function
    gl.glFrontFace(GL.GL_CCW); // Define counter-clockwise polygons as front-facing
    gl.glEnable(GL.GL_CULL_FACE); // Enable face culling
    gl.glCullFace(GL.GL_BACK); // Cull back faces

    initialise(gl); // Initialize scene objects
    startTime = getSeconds(); // Record start time for animations
  }

  // Method called whenever the window is resized
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height); // Set the viewport size
    float aspect = (float) width / (float) height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect)); // Set the camera's perspective matrix
  }

  // Rendering method called for each frame
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();

    // Check and perform global animations
    if (globalAnimationActive) {
      headRotate();
      animateTopSphereRotation();
      diagonalMovement();
      diagonalMovement2();
    }
    updateSpotlightPosition(); // Update the position of the spotlight

    // Individual animations
    if (headRotationActive) {
      headRotate();
    }
    if (topSphereAnimationActive) {
      animateTopSphereRotation();
    }
    if (alien1AnimationActive) {
      diagonalMovement();
    }
    if (alien2AnimationActive) {
      diagonalMovement2();
    }

    render(gl); // Render the scene
  }

  // Method called when the GL context is destroyed
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    // Dispose resources
    lights[0].dispose(gl);
    lights[1].dispose(gl);
    lights[2].dispose(gl);
    backWall.dispose(gl);
    sideWall.dispose(gl);
    rightSideWall.dispose(gl);
    floor.dispose(gl);
    alien1.dispose(gl);
    alien2.dispose(gl);
  }

  // Flags for controlling animations
  private boolean globalAnimationActive = false;
  private boolean alien1AnimationActive = false;
  private boolean alien2AnimationActive = false;
  private boolean lightOffAnimation = false;
  private double lightOffStartTime;
  private boolean lightOffAnimation2 = false;
  private double lightOffStartTime2;
  private boolean lightOffAnimation3 = false;
  private double lightOffStartTime3;
  private boolean headRotationActive = false;
  private boolean topSphereAnimationActive = false;

  // Methods to start/stop animations
  public void startAlien1Animation() {
    alien1AnimationActive = true;
  }

  public void stopAlien1Animation() {
    alien1AnimationActive = false;
  }

  public void startAlien2Animation() {
    alien2AnimationActive = true;
  }

  public void stopAlien2Animation() {
    alien2AnimationActive = false;
  }

  public void startTopSphereAnimation() {
    topSphereAnimationActive = true;
  }

  public void stopTopSphereAnimation() {
    topSphereAnimationActive = false;
  }

  public void startHeadRotation() {
    headRotationActive = true;
  }

  public void stopHeadRotation() {
    headRotationActive = false;
  }

  /**
   * Toggles the global animation state. When enabled, all animations are started;
   * when disabled, all animations are stopped.
   */
  public void toggleGlobalAnimation() {
    globalAnimationActive = !globalAnimationActive;
    if (globalAnimationActive) {
      startAlien1Animation();
      startAlien2Animation();
      startTopSphereAnimation();
      startHeadRotation();
    } else {
      stopAlien1Animation();
      stopAlien2Animation();
      stopTopSphereAnimation();
      stopHeadRotation();
    }
  }

  /**
   * Starts the animation to turn off light 0.
   */
  public void startLightOffAnimation() {
    lightOffAnimation = true;
    lightOffStartTime = getSeconds();
  }

  /**
   * Starts the animation to immediately turn on light 0 to full intensity.
   */
  public void startLightOnAnimation() {
    lightOffAnimation = false;
    Material lightMaterial = lights[0].getMaterial();
    lightMaterial.setAmbient(0.5f, 0.5f, 0.5f);
    lightMaterial.setDiffuse(0.8f, 0.8f, 0.8f);
    lightMaterial.setSpecular(0.8f, 0.8f, 0.8f);
  }

  /**
   * Starts the animation to turn off light 1.
   */
  public void startLightOffAnimation2() {
    lightOffAnimation2 = true;
    lightOffStartTime2 = getSeconds();
  }

  /**
   * Starts the animation to immediately turn on light 1 to full intensity.
   */
  public void startLightOnAnimation2() {
    lightOffAnimation2 = false;
    Material lightMaterial2 = lights[1].getMaterial();
    lightMaterial2.setAmbient(0.5f, 0.5f, 0.5f);
    lightMaterial2.setDiffuse(0.8f, 0.8f, 0.8f);
    lightMaterial2.setSpecular(0.8f, 0.8f, 0.8f);
  }

  /**
   * Starts the animation to turn off light 2.
   */
  public void startLightOffAnimation3() {
    lightOffAnimation3 = true;
    lightOffStartTime3 = getSeconds();
  }

  /**
   * Starts the animation to immediately turn on light 2 to full intensity.
   */
  public void startLightOnAnimation3() {
    lightOffAnimation3 = false;
    Material lightMaterial3 = lights[2].getMaterial();
    lightMaterial3.setAmbient(0.5f, 0.5f, 0.5f);
    lightMaterial3.setDiffuse(0.8f, 0.8f, 0.8f);
    lightMaterial3.setSpecular(0.8f, 0.8f, 0.8f);
  }

  /**
   * Handles the diagonal movement animation for the first alien.
   */
  public void diagonalMovement() {
    double elapsedTime = getSeconds() - startTime;
    alien1.diagonalMovement(elapsedTime);
  }

  /**
   * Handles the diagonal movement animation for the second alien.
   */
  public void diagonalMovement2() {
    double elapsedTime = getSeconds() - startTime;
    alien2.diagonalMovement2(elapsedTime);
  }

  /**
   * Handles the head rotation animation for the aliens, if the head rotation
   * is active.
   */
  public void headRotate() {
    if (!headRotationActive)
      return;
    double elapsedTime = getSeconds() - startTime;
    alien1.headRotate(elapsedTime);
    alien2.headRotate(elapsedTime);
  }

  // Values for Spotlight creation

  // Initial angle for top sphere oscillation
  private double topSphereOscillationAngle = 0;
  // Defines the speed at which the top sphere oscillates
  private final double oscillationSpeed = 1.0;
  // Defines the range of oscillation in degrees
  private final double oscillationRange = 45;

  // Parameters defining the shape and position of the new elongated sphere
  float elongationScaleY = 10.0f; // Elongation factor along the y-axis
  float elongationScaleX = 1f; // Scale factor for x-axis (maintains original size)
  float elongationScaleZ = 1.0f; // Scale factor for z-axis (maintains original size)
  float elongatedSpherePosX = -10.0f; // X-axis position, to the left of the central scene
  float elongatedSpherePosY = 0.67f; // Y-axis position, at the base level
  float elongatedSpherePosZ = 10.0f; // Z-axis position, aligned with the central scene

  // Parameters for the top elongated sphere
  float topElongationScaleY = 3.8f; // Elongation factor along the y-axis
  float topElongationScaleX = 0.8f; // Scale factor for x-axis
  float topElongationScaleZ = 0.8f; // Scale factor for z-axis
  float topSphereRotationAngle = 70; // Initial rotation angle in degrees

  // Calculations to determine the height and position of the top elongated sphere
  float originalSphereHeight = 1.225f; // Original height of the sphere before scaling
  float actualElongatedSphereHeight = originalSphereHeight * elongationScaleY; // Height after scaling
  float topSphereBase = elongatedSpherePosY + actualElongatedSphereHeight * 0.5f; // Base position of the top sphere
  float actualTopElongatedSphereHeight = originalSphereHeight * topElongationScaleY; // Height of the top sphere after
                                                                                     // scaling

  float offset = 3f; // Offset to avoid intersection with other objects
  float topSpherePosY = topSphereBase + actualTopElongatedSphereHeight * 0.5f + offset; // Calculated Y position
  float topSpherePosX = elongatedSpherePosX; // X position same as the elongated sphere
  float topSpherePosZ = elongatedSpherePosZ; // Z position same as the elongated sphere

  /**
   * Animates the rotation of the top sphere.
   * It includes oscillation along the Y-axis and a pre-defined rotation along the
   * Z-axis.
   */
  public void animateTopSphereRotation() {
    if (!topSphereAnimationActive)
      return;

    double elapsedTime = getSeconds() - startTime;
    topSphereOscillationAngle = Math.sin(elapsedTime * oscillationSpeed) * oscillationRange;

    // Scaling transformation for the sphere
    Mat4 scale = Mat4Transform.scale(topElongationScaleX, topElongationScaleY, topElongationScaleZ);

    // Rotation transformations
    Mat4 existingRotation = Mat4Transform.rotateAroundZ(topSphereRotationAngle);
    Mat4 oscillationRotation = Mat4Transform.rotateAroundY((float) topSphereOscillationAngle);

    // Combining the rotations
    Mat4 combinedRotation = Mat4.multiply(oscillationRotation, existingRotation);

    // Translation transformation
    Mat4 translate = Mat4Transform.translate(topSpherePosX, topSpherePosY, topSpherePosZ);

    // Applying all transformations in order: scaling, rotation, translation
    Mat4 m = Mat4.multiply(translate, Mat4.multiply(combinedRotation, scale));

    topSphereTransform.setTransform(m);
    topSphereTransform.update();
  }

  // Texture and scene components
  private TextureLibrary textures; // Library for managing textures
  private Camera camera; // Camera for viewing the scene
  private Mat4 perspective; // Perspective matrix for camera view
  private Model floor; // Model for the floor
  private Aliens alien1, alien2; // Alien models
  private Light[] lights = new Light[3]; // Array to hold light sources
  private Model backWall, sideWall, rightSideWall; // Wall models

  private void initialise(GL3 gl) {

    createRandomNumbers();

    float spacing = 5.0f;

    textures = new TextureLibrary();
    textures.add(gl, "grey", "textures/grey.jpg");
    textures.add(gl, "jade_specular", "textures/jade_specular.jpg");
    textures.add(gl, "container_specular", "textures/container2_specular.jpg");
    textures.add(gl, "ear0xuu2", "textures/ear0xuu2.jpg");
    textures.add(gl, "surface_specular", "textures/surface_specular.jpg");
    textures.add(gl, "ear0xuu2_specular", "textures/ear0xuu2_specular.jpg");
    textures.add(gl, "darkgrey", "textures/darkgrey.jpg");
    textures.add(gl, "grey2", "textures/grey2.jpg");
    textures.add(gl, "darkgrey2", "textures/darkgrey2.jpg");
    textures.add(gl, "greyrock", "textures/greyrock.jpg");
    textures.add(gl, "grey3", "textures/grey3.jpg");
    textures.add(gl, "pastel", "textures/pastel.jpg");
    textures.add(gl, "snowbg1", "textures/snowbg1.png");
    textures.add(gl, "snowbg2", "textures/snowbg2.png");
    textures.add(gl, "snowfall", "textures/snow1.jpg");
    textures.add(gl, "snowwall", "textures/snowbg3.png");
    textures.add(gl, "snowwall2", "textures/snowbg4.png");
    textures.add(gl, "skin", "textures/skin.jpg");
    textures.add(gl, "dirty", "textures/dirty.jpg");
    textures.add(gl, "rust", "textures/rust.jpg");
    textures.add(gl, "melt", "textures/melt.jpg");

    gl.glActiveTexture(GL.GL_TEXTURE0);
    Texture textureId1 = textures.get("snowbg1");
    textureId1.bind(gl);
    System.out.println("Texture 'snowbg1' bound to GL_TEXTURE0.");

    gl.glActiveTexture(GL.GL_TEXTURE1);
    Texture textureId2 = textures.get("snowfall");
    if (textureId2 != null) {
      textureId2.bind(gl);
      System.out.println("Texture 'snowFalling' bound to GL_TEXTURE1.");
    } else {
      System.out.println("Error: Texture 'snowFalling' is null.");
    }

    // Initialize lights with spotlight functionality
    lights[0] = new Light(gl);
    lights[0].setCamera(camera);
    setSpotlightProperties(lights[0], 360); // Non-spotlight with wide angle

    lights[1] = new Light(gl);
    lights[1].setCamera(camera);
    setSpotlightProperties(lights[1], 360); // Non-spotlight with wide angle

    // Create models for the floor and walls
    Texture floorTexture1 = textures.get("snowbg2"); // First texture for the floor
    Texture floorTexture2 = textures.get("melt"); // Second texture for the floor
    floor = makefloor(gl, floorTexture1, floorTexture2); // Create the floor with two textures

    backWall = makeWall(gl);
    sideWall = makeSideWall(gl);
    rightSideWall = makeRightSideWall(gl);

    // Create alien models
    alien1 = makeAlien1(gl);
    alien2 = makeAlien2(gl);

    // Record the start time of the program
    startTime = getSeconds();

    // Create and configure the first elongated sphere
    newSphere = makeSphere(gl, textures.get("pastel"), textures.get("rust")); // Create sphere with pastel textures
    // Transformation node for positioning and scaling the new sphere
    newSphereTransform = new TransformNode("newSphereTransform",
        Mat4.multiply(Mat4Transform.scale(elongationScaleX, elongationScaleY, elongationScaleZ), // Scale transformation
            Mat4Transform.translate(elongatedSpherePosX, elongatedSpherePosY, elongatedSpherePosZ))); // Translation
                                                                                                      // transformation

    // Create and configure the top elongated sphere
    topSphere = makeSphere(gl, textures.get("pastel"), textures.get("rust")); // Create another sphere with pastel
                                                                              // textures
    // Transformation matrix for top sphere with initial rotation and scaling
    Mat4 mTopElongatedSphere = Mat4Transform.translate(topSpherePosX, topSpherePosY, topSpherePosZ); // Translation
                                                                                                     // transformation
    mTopElongatedSphere = Mat4.multiply(mTopElongatedSphere, Mat4Transform.rotateAroundZ(topSphereRotationAngle));
    mTopElongatedSphere = Mat4.multiply(mTopElongatedSphere,
        Mat4Transform.scale(topElongationScaleX, topElongationScaleY, topElongationScaleZ)); // Apply scaling
    topSphereTransform = new TransformNode("topSphereTransform", mTopElongatedSphere); // Transformation node for top
                                                                                       // sphere

    // Initialize and position the spotlight
    lights[2] = new Light(gl); // Create a new third light object - Spotlight
    lights[2].setCamera(camera); // Set the camera for the light
    setSpotlightProperties(lights[2], 20); // Configure light as a focused spotlight
    Vec3 spotlightRelativePosition = new Vec3(-11.2f, 11.5f, 0.0f); // Relative position of the spotlight
    lights[2].setPosition(spotlightRelativePosition); // Position the spotlight
  }

  private Model makefloor(GL3 gl, Texture texture1, Texture texture2) {
    String name = "floor";
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f),
        new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    float yOffset = 1.7f;
    float scaleX = 30;
    float scaleY = 1f;
    float scaleZ = 25;
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.translate(0, yOffset, 0),
        Mat4Transform.scale(scaleX, scaleY, scaleZ));
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_2t.txt");
    shader.use(gl);

    // Bind textures to their respective texture units
    gl.glActiveTexture(GL.GL_TEXTURE0);
    texture1.bind(gl);
    shader.setInt(gl, "first_texture", 0);

    gl.glActiveTexture(GL.GL_TEXTURE1);
    texture2.bind(gl);
    shader.setInt(gl, "second_texture", 1);

    Model floor = new Model(name, mesh, modelMatrix, shader, material, lights, camera, texture1, texture2);
    return floor;
  }

  // Method to create the back wall model
  private Model makeWall(GL3 gl) {
    // Load textures for the wall
    Texture wallTexture1 = textures.get("snowbg1");
    Texture wallTexture2 = textures.get("snowfall");

    String wallName = "backWall";
    // Define the material properties for the wall
    Material wallMaterial = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f),
        new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
    // Set the dimensions and position of the wall
    float scaleX = 30;
    float scaleZ = 25;
    float scaleY = scaleX;
    float yOffset = -12.5f;

    // Create a transformation matrix to position and scale the wall
    Mat4 wallModelMatrix = Mat4.multiply(Mat4Transform.translate(0f, yOffset, -14.2f),
        Mat4Transform.scale(scaleX, scaleY, scaleZ));
    // Rotate the wall to stand vertically
    wallModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), wallModelMatrix);
    // Generate a mesh using two triangles
    Mesh wallMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    // Apply shaders for rendering the wall
    Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_m_2t.txt");
    // Construct the back wall model with the specified attributes
    Model backWall = new Model(wallName, wallMesh, wallModelMatrix, shader, wallMaterial, lights, camera, wallTexture1,
        wallTexture2);
    return backWall; // Return the created back wall model
  }

  // Method to create the side wall model
  private Model makeSideWall(GL3 gl) {
    String name = "sideWall";
    // Define the material properties for the side wall
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f),
        new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    // Set position and dimensions for the side wall
    float zOffset = -15f;
    float scaleX = 25;
    float scaleY = scaleX;
    float scaleZ = 25;
    float xOffset = 0f;
    float yOffset = -14.2f;

    // Create transformation matrix for positioning and scaling the side wall
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.translate(xOffset, zOffset, yOffset),
        Mat4Transform.scale(scaleX, scaleY, scaleZ));
    // Rotate the wall to position it correctly
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix); // Vertical rotation
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix); // Inward facing rotation

    // Generate the mesh and apply the shader
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_m_1t.txt");
    // Create the side wall model
    Model sideWall = new Model(name, mesh, modelMatrix, shader, material, lights, camera, textures.get("snowwall2"));

    return sideWall; // Return the constructed side wall model
  }

  // Method to create the right side wall model
  private Model makeRightSideWall(GL3 gl) {
    String name = "rightSideWall";
    // Define the material properties for the right side wall
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f),
        new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    // Set position and dimensions for the right side wall
    float zOffset = -15f;
    float scaleX = 25;
    float scaleY = scaleX;
    float scaleZ = 25;
    float xOffset = 0f;
    float yOffset = -14.2f;

    // Create transformation matrix for positioning and scaling the right side wall
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.translate(xOffset, zOffset, yOffset),
        Mat4Transform.scale(scaleX, scaleY, scaleZ));
    // Rotate the wall to position it correctly
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix); // Vertical rotation
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-90), modelMatrix); // Inward facing rotation

    // Generate the mesh and apply the shader
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_m_1t.txt");
    // Create the right side wall model
    Model rightSideWall = new Model(name, mesh, modelMatrix, shader, material, lights, camera,
        textures.get("snowwall"));

    return rightSideWall; // Return the constructed right side wall model
  }

  /*
   * // Method to create the first alien model
   * private Aliens makeAlien1(GL3 gl) {
   * // Create a new Alien instance with specific textures and initial position
   * return new Aliens(gl, camera, lights,
   * textures.get("grey"), textures.get("jade_specular"),
   * textures.get("ear0xuu2_specular"), textures.get("darkgrey"),
   * textures.get("greyrock"), textures.get("ear0xuu2"),
   * -1.5f, 0.0f, 0.0f); // Position parameters for the alien
   * }
   * 
   * // Method to create the second alien model
   * private Aliens makeAlien2(GL3 gl) {
   * // Create a new Alien instance with different textures and initial position
   * return new Aliens(gl, camera, lights,
   * textures.get("surface_specular"), textures.get("grey2"),
   * textures.get("darkgrey2"), textures.get("container_specular"),
   * textures.get("grey3"), textures.get("ear0xuu2"),
   * 5.5f, 10.0f, 10f); // Position parameters for the alien
   * }
   */

  // Method to create the first alien model
  private Aliens makeAlien1(GL3 gl) {
    // Create a new Alien instance with specific textures and initial position
    // Assuming additional textures for the cube are available
    return new Aliens(gl, camera, lights,
        textures.get("grey"), textures.get("jade_specular"), // Head
        textures.get("ear0xuu2_specular"), textures.get("darkgrey"), // Eyes
        textures.get("greyrock"), textures.get("skin"), // Arms, Antenna
        textures.get("skin"), textures.get("skin"), // Ears
        textures.get("skin"), textures.get("dirty"), // Body
        -1.5f, 0.0f, 0.0f); // Position parameters for the alien
  }

  // Method to create the second alien model
  private Aliens makeAlien2(GL3 gl) {
    // Create a new Alien instance with different textures and initial position
    // Assuming additional textures for the cube are available
    return new Aliens(gl, camera, lights,
        textures.get("surface_specular"), textures.get("grey2"), // Head
        textures.get("darkgrey2"), textures.get("container_specular"), // Eyes
        textures.get("grey3"), textures.get("surface_specular"), // Arms, Antenna
        textures.get("grey2"), textures.get("darkgrey2"), // Ears
        textures.get("container_specular"), textures.get("grey2"), // Body
        5.5f, 10.0f, 10f); // Position parameters for the alien
  }

  // Method to render the scene
  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear color and depth buffers

    // Update and render each light
    lights[0].setPosition(getLight0Position()); // Update position of the first light
    lights[0].render(gl); // Render the first light

    lights[1].setPosition(getLight1Position()); // Update position of the second light
    lights[1].render(gl); // Render the second light

    // Set spotlight direction and intensity scale
    Vec3 spotlightDirection = new Vec3(0, 0, 0);
    lights[2].setDirection(spotlightDirection); // Update spotlight direction
    float lightIntensityScale = 0.4f; // Intensity scale for lighting

    // Render floor with adjusted light intensity
    Shader floorShader = floor.getShader();
    floorShader.use(gl);
    floorShader.setFloat(gl, "lightIntensityScale", lightIntensityScale);
    floor.render(gl); // Render the floor

    // Render back wall with adjusted light intensity and continuous snowfall effect
    Shader backWallShader = backWall.getShader();
    backWallShader.use(gl);
    backWallShader.setFloat(gl, "lightIntensityScale", lightIntensityScale);

    double elapsedTime = getSeconds() - startTime; // Calculate elapsed time
    float speed = 0.3f; // Speed of snowfall effect
    float offsetY = (float) (elapsedTime * speed) % 1.0f; // Y-offset for snowfall effect

    backWallShader.setVec2(gl, "offset", new Vec2(0.0f, offsetY)); // Set snowfall effect offset
    backWallShader.setInt(gl, "first_texture", 0); // Set first texture
    backWallShader.setInt(gl, "second_texture", 1); // Set second texture
    backWall.render(gl); // Render the back wall

    // Render side and right side walls with adjusted light intensity
    Shader sideWallShader = sideWall.getShader();
    sideWallShader.use(gl);
    sideWallShader.setFloat(gl, "lightIntensityScale", lightIntensityScale);
    sideWall.render(gl); // Render the side wall

    Shader rightSideWallShader = rightSideWall.getShader();
    rightSideWallShader.use(gl);
    rightSideWallShader.setFloat(gl, "lightIntensityScale", lightIntensityScale);
    rightSideWall.render(gl); // Render the right side wall

    // Render the alien models
    alien1.render(gl); // Render the first alien
    alien2.render(gl); // Render the second alien

    // Render the first sphere
    if (newSphere != null && newSphereTransform != null) {
      Mat4 sphereMatrix = newSphereTransform.getTransform(); // Get the transformation matrix of the sphere
      newSphere.render(gl, sphereMatrix); // Render the new sphere with its current transformation
    }

    // Render the top sphere
    if (topSphere != null && topSphereTransform != null) {
      Mat4 topSphereMatrix = topSphereTransform.getTransform(); // Get the transformation matrix of the top sphere
      topSphere.render(gl, topSphereMatrix); // Render the top sphere with its current transformation
    }

    // Light Off Animation for the first light
    if (lightOffAnimation) {

      float intensityFactor = Math.max(0, 1 - (float) elapsedTime); // Calculate intensity factor
      Material lightMaterial = lights[0].getMaterial(); // Get material of the first light
      // Adjust light material properties based on the intensity factor
      lightMaterial.setAmbient(intensityFactor * lightMaterial.getAmbient().x,
          intensityFactor * lightMaterial.getAmbient().y,
          intensityFactor * lightMaterial.getAmbient().z);
      lightMaterial.setDiffuse(intensityFactor * lightMaterial.getDiffuse().x,
          intensityFactor * lightMaterial.getDiffuse().y,
          intensityFactor * lightMaterial.getDiffuse().z);
      lightMaterial.setSpecular(intensityFactor * lightMaterial.getSpecular().x,
          intensityFactor * lightMaterial.getSpecular().y,
          intensityFactor * lightMaterial.getSpecular().z);

      if (elapsedTime > 5) { // Stop the animation after 5 seconds
        lightOffAnimation = false;
      }
    }
    if (lightOffAnimation2) {
      float intensityFactor = Math.max(0, 1 - (float) elapsedTime);

      Material lightMaterial2 = lights[1].getMaterial();
      lightMaterial2.setAmbient(intensityFactor * lightMaterial2.getAmbient().x,
          intensityFactor * lightMaterial2.getAmbient().y,
          intensityFactor * lightMaterial2.getAmbient().z);
      lightMaterial2.setDiffuse(intensityFactor * lightMaterial2.getDiffuse().x,
          intensityFactor * lightMaterial2.getDiffuse().y,
          intensityFactor * lightMaterial2.getDiffuse().z);
      lightMaterial2.setSpecular(intensityFactor * lightMaterial2.getSpecular().x,
          intensityFactor * lightMaterial2.getSpecular().y,
          intensityFactor * lightMaterial2.getSpecular().z);

      if (elapsedTime > 5) { // Stop the animation after 5 seconds
        lightOffAnimation2 = false;
      }
    }
    if (lightOffAnimation3) {
      float intensityFactor = Math.max(0, 1 - (float) elapsedTime);

      Material lightMaterial3 = lights[2].getMaterial();
      lightMaterial3.setAmbient(intensityFactor * lightMaterial3.getAmbient().x,
          intensityFactor * lightMaterial3.getAmbient().y,
          intensityFactor * lightMaterial3.getAmbient().z);
      lightMaterial3.setDiffuse(intensityFactor * lightMaterial3.getDiffuse().x,
          intensityFactor * lightMaterial3.getDiffuse().y,
          intensityFactor * lightMaterial3.getDiffuse().z);
      lightMaterial3.setSpecular(intensityFactor * lightMaterial3.getSpecular().x,
          intensityFactor * lightMaterial3.getSpecular().y,
          intensityFactor * lightMaterial3.getSpecular().z);

      if (elapsedTime > 5) { // Stop the animation after 5 seconds
        lightOffAnimation3 = false;
      }
    }
  }

  // Function to create a 3D sphere model with textures and shaders
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

  // Function to set spotlight properties for a light source
  private void setSpotlightProperties(Light light, float cutoffAngleDegrees) {
    Vec3 direction = new Vec3(0, -1, 0); // Spotlight direction
    float cutoff = (float) Math.cos(Math.toRadians(cutoffAngleDegrees)); // Inner cutoff angle
    float outerCutoff = (float) Math.cos(Math.toRadians(cutoffAngleDegrees + 5)); // Outer cutoff angle
    light.setAsSpotlight(direction, cutoff, outerCutoff); // Configure light as a spotlight
  }

  // Function to update the position of the spotlight based on top sphere's
  // transformation
  private void updateSpotlightPosition() {
    if (topSphereTransform != null) {
      Vec3 relativeSpotlightPosition = new Vec3(0f, -0.48f, 0f); // Relative position to the top sphere
      Mat4 topSphereMatrix = topSphereTransform.getTransform(); // Get top sphere's transformation matrix
      Vec4 transformedPosition = Mat4.multiply(topSphereMatrix, new Vec4(relativeSpotlightPosition, 1.0f)); // Transform
                                                                                                            // position

      // Convert Vec4 to Vec3 for the light position
      Vec3 lightPosition = new Vec3(transformedPosition.x, transformedPosition.y, transformedPosition.z);
      lights[2].setPosition(lightPosition); // Set spotlight position
    }
  }

  // Functions to get the positions of the first and second light sources
  private Vec3 getLight0Position() {
    return new Vec3(20.0f, 35f, -3.0f); // Position for light 0
  }

  private Vec3 getLight1Position() {
    return new Vec3(-10.0f, 35f, -15.0f); // Position for light 1
  }

  // Function to get the current time in seconds
  private double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }

  // Function to generate a set of random numbers
  private int NUM_RANDOMS = 1000;
  private float[] randoms;

  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i = 0; i < NUM_RANDOMS; ++i) {
      randoms[i] = (float) Math.random(); // Fill the array with random float values
    }
  }
}

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Light {

  private Material material; // Material properties of the light (ambient, diffuse, specular)
  private Vec3 position; // Position of the light in the scene
  private Mat4 model; // Model matrix for transformations
  public Shader shader; // Shader program for the light
  private Camera camera; // Camera reference for view-dependent calculations
  private Vec3 direction; // Direction vector for spotlight
  private float cutoff; // Cutoff angle for spotlight's cone
  private float outerCutoff; // Outer cutoff angle for soft edge of spotlight

  private boolean isSpotlight = false;

  // Constructor for Light object
  public Light(GL3 gl) {
    material = new Material();
    material.setAmbient(0.5f, 0.5f, 0.5f); // Default ambient light intensity
    material.setDiffuse(0.8f, 0.8f, 0.8f); // Default diffuse light intensity
    material.setSpecular(0.8f, 0.8f, 0.8f); // Default specular light intensity
    position = new Vec3(3f, 2f, 1f); // Default position of the light
    model = new Mat4(1); // Initialize model matrix
    shader = new Shader(gl, "shaders/vs_light_01.txt", "shaders/fs_light_01.txt"); // Load shader programs
    fillBuffers(gl); // Initialize and fill buffers
  }

  // Set position of the light
  public void setPosition(Vec3 v) {
    position.x = v.x;
    position.y = v.y;
    position.z = v.z;
  }

  // Overloaded method to set position using individual components
  public void setPosition(float x, float y, float z) {
    position.x = x;
    position.y = y;
    position.z = z;
  }

  // Get current position of the light
  public Vec3 getPosition() {
    return position;
  }

  // Set material properties of the light
  public void setMaterial(Material m) {
    material = m;
  }

  // Get material properties of the light
  public Material getMaterial() {
    return material;
  }

  // Set the camera associated with the light
  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  // Check if the light is a spotlight
  public boolean isSpotlight() {
    return isSpotlight;
  }

  // Set the direction of the spotlight
  public void setDirection(Vec3 v) {
    direction.x = v.x;
    direction.y = v.y;
    direction.z = v.z;
  }

  // Overloaded method to set direction using individual components
  public void setDirection(float x, float y, float z) {
    direction.x = x;
    direction.y = y;
    direction.z = z;
  }

  // Get the direction of the spotlight
  public Vec3 getDirection() {
    return direction;
  }

  // Get the cutoff angle of the spotlight
  public float getCutoff() {
    return cutoff;
  }

  // Get the outer cutoff angle of the spotlight
  public float getOuterCutoff() {
    return outerCutoff;
  }

  // Set the light as a spotlight with specified direction and cutoff angles
  public void setAsSpotlight(Vec3 direction, float cutoff, float outerCutoff) {
    this.isSpotlight = true; // Mark the light as a spotlight
    this.direction = direction; // Set the direction of the spotlight
    this.cutoff = cutoff; // Set the cutoff angle for the spotlight's inner cone
    this.outerCutoff = outerCutoff; // Set the outer cutoff angle for soft edges
  }

  // Render the light in the scene
  public void render(GL3 gl) {
    Mat4 model = new Mat4(1); // Initialize model matrix
    model = Mat4.multiply(Mat4Transform.scale(0.3f, 0.3f, 0.3f), model); // Scale the light representation
    model = Mat4.multiply(Mat4Transform.translate(position), model); // Translate the light to its position

    // Calculate the Model-View-Projection matrix
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), model));

    shader.use(gl); // Use the light shader
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL()); // Set the MVP matrix in the shader

    // If the light is a spotlight, set its properties in the shader
    if (isSpotlight) {
      shader.setVec3(gl, "spotlightDirection", direction.x, direction.y, direction.z);
      shader.setFloat(gl, "spotlightCutoff", cutoff);
      shader.setFloat(gl, "spotlightOuterCutoff", outerCutoff);
    }

    // Draw the light representation
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  // Clean up resources used by the light
  public void dispose(GL3 gl) {
    // Delete buffers and arrays used by the light
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

  public float[] vertices = new float[] { // x,y,z
      -0.5f, -0.5f, -0.5f, // 0
      -0.5f, -0.5f, 0.5f, // 1
      -0.5f, 0.5f, -0.5f, // 2
      -0.5f, 0.5f, 0.5f, // 3
      0.5f, -0.5f, -0.5f, // 4
      0.5f, -0.5f, 0.5f, // 5
      0.5f, 0.5f, -0.5f, // 6
      0.5f, 0.5f, 0.5f // 7
  };

  public int[] indices = new int[] {
      0, 1, 3, // x -ve
      3, 2, 0, // x -ve
      4, 6, 7, // x +ve
      7, 5, 4, // x +ve
      1, 5, 7, // z +ve
      7, 3, 1, // z +ve
      6, 4, 0, // z -ve
      0, 2, 6, // z -ve
      0, 4, 5, // y -ve
      5, 1, 0, // y -ve
      2, 3, 7, // y +ve
      7, 6, 2 // y +ve
  };

  private int vertexStride = 3;
  private int vertexXYZFloats = 3;

  private int[] vertexBufferId = new int[1];
  public int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];

  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);

    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);

    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride * Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);

    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    // gl.glBindVertexArray(0);
  }

}
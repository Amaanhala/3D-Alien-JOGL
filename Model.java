import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

public class Model {

  private String name;
  private Mesh mesh;
  private Mat4 modelMatrix;
  private Shader shader;
  private Material material;
  private Camera camera;
  private Light[] lights;
  private Texture diffuse;
  private Texture specular;

  public Model() {
    name = null;
    mesh = null;
    modelMatrix = null;
    material = null;
    camera = null;
    lights = null;
    shader = null;
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
      Camera camera, Texture diffuse, Texture specular) {
    this.name = name;
    this.mesh = mesh;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.material = material;
    this.lights = lights;
    this.camera = camera;
    this.diffuse = diffuse;
    this.specular = specular;
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
      Camera camera, Texture diffuse) {
    this(name, mesh, modelMatrix, shader, material, lights, camera, diffuse, null);
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
      Camera camera) {
    this(name, mesh, modelMatrix, shader, material, lights, camera, null, null);
  }

  public void setName(String s) {
    this.name = s;
  }

  public void setMesh(Mesh m) {
    this.mesh = m;
  }

  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public void setShader(Shader shader) {
    this.shader = shader;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void setLights(Light[] lights) {
    this.lights = lights;
  }

  public void setDiffuse(Texture t) {
    this.diffuse = t;
  }

  public void setSpecular(Texture t) {
    this.specular = t;
  }

  public void renderName(GL3 gl) {
    System.out.println("Name = " + name);
  }

  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }

  // second version of render is so that modelMatrix can be overriden with a new
  // parameter
  public void render(GL3 gl, Mat4 modelMatrix) {
    if (mesh_null()) {
      System.out.println("Error: null in model render");
      return;
    }

    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    Vec3 viewPos = camera.getPosition();
    shader.setVec3(gl, "viewPos", viewPos.x, viewPos.y, viewPos.z);

    shader.setInt(gl, "numLights", lights.length);

    for (int i = 0; i < lights.length; i++) {
      Vec3 lightPos = lights[i].getPosition();
      shader.setVec3(gl, "lights[" + i + "].position", lightPos.x, lightPos.y, lightPos.z);

      Vec3 ambient = lights[i].getMaterial().getAmbient();
      shader.setVec3(gl, "lights[" + i + "].ambient", ambient.x, ambient.y, ambient.z);

      Vec3 diffuse = lights[i].getMaterial().getDiffuse();
      shader.setVec3(gl, "lights[" + i + "].diffuse", diffuse.x, diffuse.y, diffuse.z);

      Vec3 specular = lights[i].getMaterial().getSpecular();
      shader.setVec3(gl, "lights[" + i + "].specular", specular.x, specular.y, specular.z);
    }

    Vec3 matAmbient = material.getAmbient();
    shader.setVec3(gl, "material.ambient", matAmbient.x, matAmbient.y, matAmbient.z);

    Vec3 matDiffuse = material.getDiffuse();
    shader.setVec3(gl, "material.diffuse", matDiffuse.x, matDiffuse.y, matDiffuse.z);

    Vec3 matSpecular = material.getSpecular();
    shader.setVec3(gl, "material.specular", matSpecular.x, matSpecular.y, matSpecular.z);

    shader.setFloat(gl, "material.shininess", material.getShininess());

    if (diffuse != null) {
      shader.setInt(gl, "material.diffuse", 0); // Setting texture unit to 0
      gl.glActiveTexture(GL.GL_TEXTURE0);
      diffuse.bind(gl);
    }
    if (specular != null) {
      shader.setInt(gl, "material.specular", 1); // Setting texture unit to 1
      gl.glActiveTexture(GL.GL_TEXTURE1);
      specular.bind(gl);
    }

    // then render the mesh
    mesh.render(gl);
  }

  public Shader getShader() {
    return shader;
  }

  private boolean mesh_null() {
    return (mesh == null);
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl); // only need to dispose of mesh
  }

}
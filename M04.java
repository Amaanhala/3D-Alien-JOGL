import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class M04 extends JFrame implements ActionListener {

  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private M04_GLEventListener glEventListener;
  private final FPSAnimator animator;
  private Camera camera;

  public static void main(String[] args) {
    M04 b1 = new M04("M04");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public M04(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new M04_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);

    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    JMenu fileMenu = new JMenu("File");
    JMenuItem quitItem = new JMenuItem("Quit");
    quitItem.addActionListener(this);
    fileMenu.add(quitItem);
    menuBar.add(fileMenu);

    JPanel p = new JPanel();
    JButton b = new JButton("camera X");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("camera Z");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Remix");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Rock Alien 1");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Roll Alien 1");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Rock Alien 2");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Roll Alien 2");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("turn off light 1");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("turn on light 1");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("turn off light 2");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("turn on light 2");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("turn off Spotlight");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("turn on Spotlight");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Start Head Rotation");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Stop Head Rotation");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Start Spotlight Rotate");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Stop Spotlight Rotate");
    b.addActionListener(this);
    p.add(b);

    this.add(p, BorderLayout.SOUTH);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("camera X")) {
      camera.setCamera(Camera.CameraType.X);
      canvas.requestFocusInWindow();
    } else if (e.getActionCommand().equalsIgnoreCase("camera Z")) {
      camera.setCamera(Camera.CameraType.Z);
      canvas.requestFocusInWindow();

    } else if (e.getActionCommand().equalsIgnoreCase("Remix")) {
      glEventListener.toggleGlobalAnimation();

    } else if (e.getActionCommand().equalsIgnoreCase("Rock Alien 1")) {
      glEventListener.startAlien1Animation();
    } else if (e.getActionCommand().equalsIgnoreCase("Roll Alien 1")) {
      glEventListener.stopAlien1Animation();

    } else if (e.getActionCommand().equalsIgnoreCase("Rock Alien 2")) {
      glEventListener.startAlien2Animation();
    } else if (e.getActionCommand().equalsIgnoreCase("Roll Alien 2")) {
      glEventListener.stopAlien2Animation();

    } else if (e.getActionCommand().equalsIgnoreCase("turn off light 1")) {
      glEventListener.startLightOffAnimation();
    } else if (e.getActionCommand().equalsIgnoreCase("turn on light 1")) {
      glEventListener.startLightOnAnimation();

    } else if (e.getActionCommand().equalsIgnoreCase("turn off light 2")) {
      glEventListener.startLightOffAnimation2();
    } else if (e.getActionCommand().equalsIgnoreCase("turn on light 2")) {
      glEventListener.startLightOnAnimation2();

    } else if (e.getActionCommand().equalsIgnoreCase("turn on Spotlight")) {
      glEventListener.startLightOnAnimation3();
    } else if (e.getActionCommand().equalsIgnoreCase("turn off Spotlight")) {
      glEventListener.startLightOffAnimation3();

    } else if (e.getActionCommand().equalsIgnoreCase("Start Head Rotation")) {
      glEventListener.startHeadRotation();
    } else if (e.getActionCommand().equalsIgnoreCase("Stop Head Rotation")) {
      glEventListener.stopHeadRotation();

    } else if (e.getActionCommand().equalsIgnoreCase("Start Spotlight Rotate")) {
      glEventListener.startTopSphereAnimation();
    } else if (e.getActionCommand().equalsIgnoreCase("Stop Spotlight Rotate")) {
      glEventListener.stopTopSphereAnimation();

    } else if (e.getActionCommand().equalsIgnoreCase("quit"))
      System.exit(0);
  }

}

class MyKeyboardInput extends KeyAdapter {
  private Camera camera;

  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }

  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        m = Camera.Movement.LEFT;
        break;
      case KeyEvent.VK_RIGHT:
        m = Camera.Movement.RIGHT;
        break;
      case KeyEvent.VK_UP:
        m = Camera.Movement.UP;
        break;
      case KeyEvent.VK_DOWN:
        m = Camera.Movement.DOWN;
        break;
      case KeyEvent.VK_A:
        m = Camera.Movement.FORWARD;
        break;
      case KeyEvent.VK_Z:
        m = Camera.Movement.BACK;
        break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;

  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }

  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx = (float) (ms.x - lastpoint.x) * sensitivity;
    float dy = (float) (ms.y - lastpoint.y) * sensitivity;
    // System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  public void mouseMoved(MouseEvent e) {
    lastpoint = e.getPoint();
  }
}
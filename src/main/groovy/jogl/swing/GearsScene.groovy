package jogl.swing

import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.awt.GLCanvas
import groovy.transform.CompileStatic

import javax.swing.JFrame
import java.awt.BorderLayout
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseEvent
import java.awt.Dimension

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.glu.GLU

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

@CompileStatic
public class GearsScene implements MouseListener, MouseMotionListener {

    private float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f
    private int gear1, gear2, gear3
    private float angle = 0.0f

    private int prevMouseX, prevMouseY
    private boolean mouseRButtonDown = false


    protected void init( GL2 gl2) {

        System.err.println("INIT GL IS: " + gl2.getClass().getName())

        gl2.setSwapInterval(1)

        float[] pos = [5.0f, 5.0f, 10.0f, 0.0f]
        float[] red = [0.8f, 0.1f, 0.0f, 1.0f]
        float[] green = [0.0f, 0.8f, 0.2f, 1.0f]
        float[] blue = [0.2f, 0.2f, 1.0f, 1.0f]

        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0)
        gl2.glEnable(GL2.GL_CULL_FACE)
        gl2.glEnable(GL2.GL_LIGHTING)
        gl2.glEnable(GL2.GL_LIGHT0)
        gl2.glEnable(GL2.GL_DEPTH_TEST)

        /* make the gears */
        gear1 = gl2.glGenLists(1)
        gl2.glNewList(gear1, GL2.GL_COMPILE)
        gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, red, 0)
        gear(gl2, 1.0f, 4.0f, 1.0f, 20, 0.7f)
        gl2.glEndList()

        gear2 = gl2.glGenLists(1)
        gl2.glNewList(gear2, GL2.GL_COMPILE)
        gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, green, 0)
        gear(gl2, 0.5f, 2.0f, 2.0f, 10, 0.7f)
        gl2.glEndList()

        gear3 = gl2.glGenLists(1)
        gl2.glNewList(gear3, GL2.GL_COMPILE)
        gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, blue, 0)
        gear(gl2, 1.3f, 2.0f, 0.5f, 10, 0.7f)
        gl2.glEndList()

        gl2.glEnable(GL2.GL_NORMALIZE)
    }

    protected void setup( GL2 gl2, int width, int height ) {

        float h = height / width

        gl2.glMatrixMode(GL2.GL_PROJECTION)

        //System.err.println("GL_VENDOR: " + gl2.glGetString(GL2.GL_VENDOR))
        //System.err.println("GL_RENDERER: " + gl2.glGetString(GL2.GL_RENDERER))
        //System.err.println("GL_VERSION: " + gl2.glGetString(GL2.GL_VERSION))
        gl2.glLoadIdentity()
        gl2.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f)
        gl2.glMatrixMode(GL2.GL_MODELVIEW)
        gl2.glLoadIdentity()
        gl2.glTranslatef(0.0f, 0.0f, -40.0f)
        gl2.glViewport( 0, 0, width, height )
    }

    protected void render( GL2 gl2, float width, float height ) {

        angle += 2.0f

        gl2.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT)

        gl2.glPushMatrix()
        gl2.glRotatef(view_rotx, 1.0f, 0.0f, 0.0f)
        gl2.glRotatef(view_roty, 0.0f, 1.0f, 0.0f)
        gl2.glRotatef(view_rotz, 0.0f, 0.0f, 1.0f)

        gl2.glPushMatrix()
        gl2.glTranslatef(-3.0f, -2.0f, 0.0f)
        gl2.glRotatef(angle, 0.0f, 0.0f, 1.0f)
        gl2.glCallList(gear1)
        gl2.glPopMatrix()

        gl2.glPushMatrix()
        gl2.glTranslatef(3.1f, -2.0f, 0.0f)
        glRotatef(gl2, -2.0f * angle - 9.0f, 0.0f, 0.0f, 1.0f)
        gl2.glCallList(gear2)
        gl2.glPopMatrix()

        gl2.glPushMatrix()
        gl2.glTranslatef(-3.1f, 4.2f, 0.0f)
        glRotatef(gl2, -2.0f * angle - 25.0f, 0.0f, 0.0f, 1.0f)
        gl2.glCallList(gear3)
        gl2.glPopMatrix()

        gl2.glPopMatrix()
    }

    private void gear(GL2 gl2,
                      float inner_radius,
                      float outer_radius,
                      float width,
                      int teeth,
                      float tooth_depth)  {
        int i
        float r0, r1, r2
        float angle, da
        float u, v, len

        r0 = inner_radius
        r1 =  (float) (outer_radius - tooth_depth / 2.0f)
        r2 =  (float) (outer_radius + tooth_depth / 2.0f)

        da =  2.0 *  (Math.PI / teeth / 4.0)

        gl2.glShadeModel(GL2.GL_FLAT)

        gl2.glNormal3f(0.0f, 0.0f, 1.0f)

        /* draw front face */
        gl2.glBegin(GL2.GL_QUAD_STRIP)
        for (i = 0; i <= teeth; i++)
        {
            angle = i * 2.0 *  ((float)Math.PI / teeth)
            glVertex3f(gl2,r0 * Math.cos(angle), r0 * Math.sin(angle), width * 0.5f)
            glVertex3f(gl2,r1 * Math.cos(angle), r1 * Math.sin(angle), width * 0.5f)
            if(i < teeth)
            {
                glVertex3f(gl2,r0 * Math.cos(angle), r0 * Math.sin(angle), width * 0.5f)
                glVertex3f(gl2,r1 * Math.cos(angle + 3.0f * da), r1 * Math.sin(angle + 3.0f * da), width * 0.5f)
            }
        }
        gl2.glEnd()

        /* draw front sides of teeth */
        gl2.glBegin(GL2.GL_QUADS)
        for (i = 0; i <= teeth; i++)
        {
            angle = i * 2.0 *  ((float)Math.PI / teeth)
            glVertex3f(gl2,r1 * Math.cos(angle), r1 * Math.sin(angle), width * 0.5f)
            glVertex3f(gl2,r2 * Math.cos(angle + da), r2 * Math.sin(angle + da), width * 0.5f)
            glVertex3f(gl2,r2 * Math.cos(angle + 2.0f * da), r2 * Math.sin(angle + 2.0f * da), width * 0.5f)
            glVertex3f(gl2,r1 * Math.cos(angle + 3.0f * da), r1 * Math.sin(angle + 3.0f * da), width * 0.5f)
        }
        gl2.glEnd()

        /* draw back face */
        gl2.glBegin(GL2.GL_QUAD_STRIP)
        for (i = 0; i <= teeth; i++)
        {
            angle = i * 2.0 *  ((float)Math.PI / teeth)
            glVertex3f(gl2,r1 * Math.cos(angle), r1 * Math.sin(angle), -width * 0.5f)
            glVertex3f(gl2,r0 * Math.cos(angle), r0 * Math.sin(angle), -width * 0.5f)
            glVertex3f(gl2,r1 * Math.cos(angle + 3 * da), r1 * Math.sin(angle + 3 * da), -width * 0.5f)
            glVertex3f(gl2,r0 * Math.cos(angle), r0 * Math.sin(angle), -width * 0.5f)
        }
        gl2.glEnd()

        /* draw back sides of teeth */
        gl2.glBegin(GL2.GL_QUADS)
        for (i = 0; i <= teeth; i++)
        {
            angle = i * 2.0 *  ((float)Math.PI / teeth)
            glVertex3f(gl2,r1 * Math.cos(angle + 3 * da), r1 * Math.sin(angle + 3 * da), -width * 0.5f)
            glVertex3f(gl2,r2 * Math.cos(angle + 2 * da), r2 * Math.sin(angle + 2 * da), -width * 0.5f)
            glVertex3f(gl2,r2 * Math.cos(angle + da), r2 * Math.sin(angle + da), -width * 0.5f)
            glVertex3f(gl2,r1 * Math.cos(angle), r1 * Math.sin(angle), -width * 0.5f)
        }
        gl2.glEnd()

        /* draw outward faces of teeth */
        gl2.glBegin(GL2.GL_QUAD_STRIP)
        for (i = 0; i <= teeth; i++)
        {
            angle = i * 2.0 *  ((float)Math.PI / teeth)
            glVertex3f(gl2,r1 * Math.cos(angle), r1 * Math.sin(angle), width * 0.5f)
            glVertex3f(gl2,r1 * Math.cos(angle), r1 * Math.sin(angle), -width * 0.5f)
            u = (float) (r2 * Math.cos(angle + da)) - (float) (r1 * Math.cos(angle))
            v = (float) (r2 * Math.sin(angle + da)) - (float) (r1 * Math.sin(angle))
            len = (float) Math.sqrt(u * u + v * v)
            u = (float) (u/len)
            v = (float) (v/len)
            gl2.glNormal3f(v, -u, 0.0f)
            glVertex3f(gl2,r2 * Math.cos(angle + da), r2 * Math.sin(angle + da), width * 0.5f)
            glVertex3f(gl2,r2 * Math.cos(angle + da), r2 * Math.sin(angle + da), -width * 0.5f)
            glNormal3f(gl2,Math.cos(angle), Math.sin(angle), 0.0f)
            glVertex3f(gl2,r2 * Math.cos(angle + 2.0 * da), r2 * Math.sin(angle + 2.0 * da), width * 0.5f)
            glVertex3f(gl2,r2 * Math.cos(angle + 2.0 * da), r2 * Math.sin(angle + 2.0 * da), -width * 0.5f)
            u = (float) ((float) r1 * Math.cos(angle + 3.0 * da) - ((float) r2 * Math.cos(angle + 2.0 * da)))
            v = (float) ((float) r1 * Math.sin(angle + 3.0 * da) - ((float) r2 * Math.sin(angle + 2.0 * da)))
            gl2.glNormal3f(v, -u, 0.0f)
            glVertex3f(gl2,r1 * Math.cos(angle + 3 * da), r1 * Math.sin(angle + 3.0 * da), width * 0.5f)
            glVertex3f(gl2,r1 * Math.cos(angle + 3 * da), r1 * Math.sin(angle + 3.0 * da), -width * 0.5f)
            glNormal3f(gl2,Math.cos(angle), Math.sin(angle), 0.0f)
        }
        glVertex3f(gl2, r1 * Math.cos(0), r1 * Math.sin(0), width * 0.5f)
        glVertex3f(gl2,r1 * Math.cos(0), r1 * Math.sin(0), -width * 0.5f)
        gl2.glEnd()

        gl2.glShadeModel(GL2.GL_SMOOTH)

        /* draw inside radius cylinder */
        gl2.glBegin(GL2.GL_QUAD_STRIP)
        for (i = 0; i <= teeth; i++)
        {
            angle = i * 2.0 *  Math.PI / teeth
            glNormal3f(gl2,-Math.cos(angle), -Math.sin(angle), 0.0f)
            glVertex3f(gl2,r0 * Math.cos(angle), r0 * Math.sin(angle), -width * 0.5f)
            glVertex3f(gl2,r0 * Math.cos(angle), r0 * Math.sin(angle), width * 0.5f)
        }
        gl2.glEnd()
    }


    //float conversion GL methods -> compile static checking
    private void glVertex3f(GL2 gl2, double x, double y, double z) {
        gl2.glVertex3f((float) x, (float) y, (float) z)
    }

    private void glNormal3f(GL2 gl2, double x, double y, double z) {
        gl2.glNormal3f((float) x, (float) y, (float) z)
    }

    private void glRotatef(GL2 gl2, double x, double y, double z, double w) {
        gl2.glRotatef((float) x, (float) y, (float) z, (float) w)
    }



    // Methods required for the implementation of MouseListener
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        prevMouseX = e.getX()
        prevMouseY = e.getY()
        if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
            mouseRButtonDown = true
        }
    }

    public void mouseReleased(MouseEvent e) {
        if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
            mouseRButtonDown = false
        }
    }

    public void mouseClicked(MouseEvent e) {}

    // Methods required for the implementation of MouseMotionListener
    public void mouseDragged(MouseEvent e) {
        int x = e.getX()
        int y = e.getY()
        Dimension size = e.getComponent().getSize()

        float thetaY = 360.0 * ((x-prevMouseX)/size.width)
        float thetaX = 360.0 * ((prevMouseY-y)/size.height)

        prevMouseX = x
        prevMouseY = y

        view_rotx += thetaX
        view_roty += thetaY
    }

    public void mouseMoved(MouseEvent e) {}
}
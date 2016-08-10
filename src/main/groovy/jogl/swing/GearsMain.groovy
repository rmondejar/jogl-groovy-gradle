package jogl.swing

import groovy.transform.CompileStatic

import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.JFrame

import java.awt.BorderLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

/**
 * A minimal program that draws with JOGL in a Swing JFrame using the AWT GLCanvas.
 *
 * @author Wade Walker
 */

@CompileStatic
public class GearsMain {

    public static void main( String [] args ) {

        GLProfile glprofile = GLProfile.default
        GLCapabilities glcapabilities = new GLCapabilities( glprofile )
        final GLCanvas glcanvas = new GLCanvas( glcapabilities )
        final FPSAnimator animator = new FPSAnimator(glcanvas, 60)
        final GearsScene scene = new GearsScene()


        glcanvas.addGLEventListener( new GLEventListener() {

            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                scene.setup( glautodrawable.getGL().getGL2(), width, height )
            }

            @Override
            public void init( GLAutoDrawable glautodrawable ) {
                glcanvas.addMouseListener(scene)
                glcanvas.addMouseMotionListener(scene)
                scene.init( glautodrawable.getGL().getGL2())
            }

            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
            }

            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                scene.render( glautodrawable.getGL().getGL2(), glautodrawable.surfaceWidth, glautodrawable.surfaceHeight )
            }
        })

        final JFrame jframe = new JFrame( "Gears Swing GLCanvas" )
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose()
                System.exit( 0 )
            }
        })

        jframe.contentPane.add( glcanvas, BorderLayout.CENTER )
        jframe.setSize( 640, 640 )
        jframe.visible = true

        animator.start()
    }
}
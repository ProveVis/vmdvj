package vmdv.ui;

import java.awt.Font;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import vmdv.config.ColorConfig;
import vmdv.model.AbstractNode;
//import vmdv.model.NodeProperty;
import vmdv.model.NodeProperty;

public class GLEventHandler implements GLEventListener {

	protected GLU glu;
	protected GLUT glut;

	private TextRenderer tr = new TextRenderer(new Font("SansSerif", Font.PLAIN, 30));

	private Viewer viewer;

	private int frames = 0;
	private long time;

	private float[] lightPosition = new float[4];
	private float[] whiteLight = new float[4];

	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void display(GLAutoDrawable gld) {
		// gld.setAutoSwapBufferMode(true);
		// GL3 gl = gld.getGL().getGL3();
		GL2 gl = (gld).getGL().getGL2();

		gl.glLoadIdentity();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		glu.gluLookAt(viewer.eyex, viewer.eyey, viewer.eyez, 0, 0, 0, 0, 1, 0);
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);

		viewer.graph.render(gl, glut, tr);

		// if (viewer.graph instanceof Tree) {
		// LinkedList<Node> painting = new LinkedList<Node>();
		// painting.addLast(viewer.graph.getStart());
		// int drawedNodes = 0;
		// while (!painting.isEmpty()) {
		// // System.out.println("painting node...");
		// Node tn = painting.removeFirst();
		// if (tn == null) {
		// return;
		// }
		// if (tn.isVisible()) {
		// RGBColor color = tn.getColor();
		// gl.glPushMatrix();
		// gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		// gl.glTranslated(tn.getX(), tn.getY(), tn.getZ());
		// glut.glutSolidSphere(tn.getSize(), 10, 10);
		// drawedNodes++;
		// gl.glColor3f(1, 1, 1);
		//
		// if (tn.isLableVisible()) {
		// tr.begin3DRendering();
		// tr.draw3D(tn.getLabel(), 0, 0, 0, 0.005f);
		// tr.flush();
		// tr.end3DRendering();
		// }
		// if (tn.showChildLabel) {
		// tr.begin3DRendering();
		// tr.draw3D(tn.childLabel, 0, 0, 0, 0.01f);
		// tr.flush();
		// tr.end3DRendering();
		// }
		// gl.glPopMatrix();
		//
		// gl.glDisable(GL2.GL_LIGHTING);
		// gl.glDisable(GL2.GL_LIGHT0);
		// gl.glEnable(GL2.GL_LIGHTING);
		// gl.glEnable(GL2.GL_LIGHT0);
		//
		// if (tn.isShowSubtree()) {
		// for (Edge e : viewer.graph.getPostEdges(tn)) {
		// Node ton = e.getTo();
		// if (ton.isVisible()) {
		// gl.glPushMatrix();
		// RGBColor edgeColor = e.getColor();
		// gl.glColor3f(edgeColor.getRed(), edgeColor.getGreen(),
		// edgeColor.getBlue());
		// gl.glLineWidth(e.getSize());
		// gl.glBegin(GL2.GL_LINES);
		// gl.glVertex3d(tn.getX(), tn.getY(), tn.getZ());
		// gl.glVertex3d(ton.getX(), ton.getY(), ton.getZ());
		// gl.glEnd();
		// gl.glPopMatrix();
		//
		// painting.addLast(ton);
		// }
		// }
		// }
		// }
		// }
		// } else {// painting regular graphs
		// int drawedNodes = 0;
		// for (Node sn : viewer.graph.getNodes()) {
		// if (!sn.isVisible()) {
		// continue;
		// }
		// gl.glPushMatrix();
		// gl.glTranslated(sn.getX(), sn.getY(), sn.getZ());
		// RGBColor color = sn.getColor();
		// gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		// glut.glutSolidSphere(sn.getSize(), 10, 10);
		// drawedNodes++;
		// // if(sn.isLableVisible()) {
		// // tr.begin3DRendering();
		// // tr.draw3D(sn.getLabel(), 0, 0, 0, 0.005f);
		// // tr.flush();
		// // tr.end3DRendering();
		// // }
		// // if(sn.isPicked()) {
		// // tr.begin3DRendering();
		// // tr.draw3D(sn.getLabel(), 0, 0, 0, 0.005f);
		// // tr.flush();
		// // tr.end3DRendering();
		// // }
		// // if(sn.isPicked()) {
		// // tr.beginRendering(gld.getSurfaceWidth(),
		// // gld.getSurfaceHeight());
		// // Point p = this.getScreenPoint(gld, sn.getX(), sn.getY(),
		// // sn.getZ());
		// // if(p != null) {
		// // tr.setColor(0, 0, 0, 1);
		// // tr.draw(sn.getLabel(), p.x, p.y);
		// // System.out.println("Showing label of sn: "+sn.getLabel()+" in
		// // position: "+String.valueOf(p.x)+","+String.valueOf(p.y));
		// // }
		// // tr.endRendering();
		// // }
		//
		// gl.glPopMatrix();
		// for (Edge se : viewer.graph.getPostEdges(sn)) {
		// Node psn = se.getTo();
		// if (!psn.isVisible()) {
		// continue;
		// }
		// gl.glPushMatrix();
		// // RGBColor snc = new
		// // RGBColor(178.0f/255,178.0f/255,178.0f/255);
		// RGBColor snc = new RGBColor(0, 0, 0);
		// if (psn.isPicked()) {
		// snc = sn.getColor();
		// se.setSize(1.0f);
		// }
		// gl.glColor3f(snc.getRed(), snc.getGreen(), snc.getBlue());
		// gl.glLineWidth(se.getSize());
		// gl.glBegin(GL2.GL_LINES);
		// gl.glVertex3d(sn.getX(), sn.getY(), sn.getZ());
		// gl.glVertex3d(psn.getX(), psn.getY(), psn.getZ());
		// gl.glColor3f(0, 0, 0);
		//
		// double dx = psn.getX() - sn.getX();
		// double dy = psn.getY() - sn.getY();
		// double dz = psn.getZ() - sn.getZ();
		// double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz,
		// 2));
		// double x = sn.getX() + dx * ((d - 0.2) / d);
		// double y = sn.getY() + dy * ((d - 0.2) / d);
		// double z = sn.getZ() + dz * ((d - 0.2) / d);
		// // gl.glTranslated(x, y, z);
		// // gl.glColor3f(0, 1, 1);
		// // glut.glutSolidSphere(0.03, 10, 10);
		// // gl.glColor3f(1, 1, 1);
		// double x1 = sn.getX() + dx * ((d - 0.3) / d);
		// double y1 = sn.getY() + dy * ((d - 0.3) / d);
		// double z1 = sn.getZ() + dz * ((d - 0.3) / d);
		//
		// // arrow
		// gl.glVertex3d(x, y, z);
		// gl.glVertex3d(x1 + 0.04, y1, z1);
		//
		// gl.glVertex3d(x, y, z);
		// gl.glVertex3d(x1 - 0.04, y1, z1);
		//
		// gl.glVertex3d(x, y, z);
		// gl.glVertex3d(x1, y1, z1 - 0.04);
		// gl.glEnd();
		// gl.glPopMatrix();
		// }
		//
		// }
		// }

		// tr.beginRendering(gld.getSurfaceWidth(), gld.getSurfaceHeight());
		// tr.setColor(0, 0, 0, 1);
		// tr.draw(String.valueOf(drawedNodes), 10, 10);
		// tr.endRendering();

		// gl.glColorMaterial(GL2.GL_FRONT_AND_BACK,
		// GL2.GL_AMBIENT_AND_DIFFUSE);
		// gl.glEnable(GL2.GL_COLOR_MATERIAL);
		// gl.glShadeModel(GL2.GL_SMOOTH);
		// gl.glEnable(GL2.GL_LIGHTING);
		// gl.glEnable(GL2.GL_LIGHT0);
		// gl.glEnable(GL2.GL_LINE_SMOOTH);
		// gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		// gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		// gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		// gl.glEnable(GL2.GL_BLEND);
		// gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glEnable(GL2.GL_DEPTH_TEST);
		//
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);

		if (!viewer.popupShowed) {
			AbstractNode sn = viewer.selectNode(gl, viewer.mousePosition);
			if (sn != null) {
				if (!sn.picked) {
					AbstractNode oldHoverNode = viewer.hoverNode;
					if (oldHoverNode != null) {
						oldHoverNode.color = viewer.hoverNodeState.getColor();
						oldHoverNode.size = viewer.hoverNodeState.getSize();
					}
					viewer.hoverNode = sn;
					NodeProperty ns = new NodeProperty(sn.color, sn.size);
					viewer.hoverNodeState = ns;
					sn.color = (ColorConfig.hoverColor);
					sn.size = (ns.getSize() * 1.2);
				}
			} else {
				AbstractNode oldHoverNode = viewer.hoverNode;
				if (oldHoverNode != null && !oldHoverNode.picked) {
					oldHoverNode.color = viewer.hoverNodeState.getColor();
					oldHoverNode.size = viewer.hoverNodeState.getSize();
				}
				viewer.hoverNode = sn;
			}
		}

		// show color, label or else
		if (!viewer.affect.isEmpty()) {
			viewer.affect.removeFirst().affect(viewer.session);
		}

		viewer.layout.updateLayout(viewer.graph);

		// gl.glLoadIdentity();
		// gl.glColor3f(0, 0, 0);
		gl.glFlush();

		setCamera(gld);
		// System.out.println("displayed...");

		long currentTime = System.currentTimeMillis();
		long timeDiff = currentTime - time;
		if (timeDiff > 1000) {
			float fps = frames * 1000 / timeDiff;
			System.out.println("FPS of " + viewer.session.getSid() + ": " + fps);
			time = currentTime;
			frames = 0;
		} else {
			frames++;
		}

	}

	public void setCamera(GLAutoDrawable gld) {
		GL2 gl = gld.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(viewer.eyex, viewer.eyey, viewer.eyez, 0, 0, 0, 0, 1, 0);
		viewer.refreshGLPanel();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable gld) {
		tr.setColor(0, 0, 0, 1);
		lightPosition[0] = 10.0f;
		lightPosition[1] = 10.0f;
		lightPosition[2] = 10.0f;
		lightPosition[3] = 0.0f;
		whiteLight[0] = 0.8f;
		whiteLight[1] = 0.8f;
		whiteLight[2] = 0.8f;
		whiteLight[3] = 1.0f;
		GL2 gl = gld.getGL().getGL2();

		// GLUT glut = new GLUT();
		gld.setAutoSwapBufferMode(true);
		glu.gluLookAt(viewer.eyex, viewer.eyey, viewer.eyez, 0, 0, 0, 0, 1, 0);
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		// gl.glClearColor(238.0f / 255.0f, 226.0f / 255.0f, 185.0f / 255.0f,
		// 0.0f);
		// gl.glClearColor(227.0f/255, 237.0f/255, 205.0f/255, 0);
		gl.glClearColor(1, 1, 1, 0);
		gl.glClearDepth(1.0);
		// gl.glShadeModel(GL2.GL_SMOOTH);
		// gl.glEnable(GL2.GL_LIGHTING);
		// gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_DEPTH_TEST);

		gl.glViewport(0, 0, gld.getSurfaceWidth(), gld.getSurfaceHeight());
		gl.glMatrixMode(GL2.GL_PROJECTION);
		int width = gld.getSurfaceWidth();
		int height = gld.getSurfaceHeight();
		if (width == 0 || height == 0) {
			glu.gluPerspective(60.0f, 1, 1.0f, 10000.0f);
		} else {
			glu.gluPerspective(60.0f, width / height, 1.0f, 10000.0f);
		}
		// glu.gluPerspective(60.0f, gld.getSurfaceWidth() /
		// gld.getSurfaceHeight(), 1.0f, 10000.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// gl.glLightfv(arg0, arg1, arg2, arg3);
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);
		// glu.gluOrtho2D(0.0, 500.0, 0.0, 300.0);
		gld.swapBuffers();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setCamera(gld);

		this.time = System.currentTimeMillis();
	}

	@Override
	public void reshape(GLAutoDrawable gld, int x, int y, int width, int height) {
		// GL2 gl = gld.getGL().getGL2();
		// gl.glViewport(x, y, width, height);
		// gl.glMatrixMode(GL2.GL_PROJECTION);
		// gl.glLoadIdentity();
		// if(width == 0 || height == 0) {
		// glu.gluPerspective(60.0f, 1, 1.0f, 10000.0f);
		// } else {
		// glu.gluPerspective(60.0f, width / height, 1.0f, 10000.0f);
		// }
		//
		// gl.glMatrixMode(GL2.GL_MODELVIEW);
		//
		// glu.gluLookAt(viewer.eyex, viewer.eyey, viewer.eyez, 0, 0, 0, 0, 1,
		// 0);

		if (height == 0) {
			height = 1;
		}
		GL2 gl2 = gld.getGL().getGL2();
		gl2.glViewport(0, 0, width, height);

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		if (glu == null) {
			glu = new GLU();
		}
		glu.gluPerspective(45.0f, (float) ((float) width / (float) height), 0.1f, 100.0f);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

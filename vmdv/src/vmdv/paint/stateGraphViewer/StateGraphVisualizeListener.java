package vmdv.paint.stateGraphViewer;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import vmdv.network.NetAgent;
import vmdv.paint.graph.Edge;
import vmdv.paint.graph.RGBColor;
import vmdv.paint.graph.StateEdge;
import vmdv.paint.graph.StateNode;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

public class StateGraphVisualizeListener implements GLEventListener,MouseListener, MouseWheelListener, MouseMotionListener {
	public static final RGBColor red = new RGBColor(1,0,0);
	public static final RGBColor green = new RGBColor(0,1,0);
	public static final RGBColor blue = new RGBColor(0,0,1);
	public static final RGBColor highlight = new RGBColor(1,0,0);
	public static final RGBColor rootColor = new RGBColor(0,0,1);
//	public static final RGBColor oriColor = new RGBColor(0,0,0);
	public static final RGBColor hoverColor = new RGBColor(1,1,0);
	public static final RGBColor stepColor = new RGBColor(1,0,0);
//	public static final RGBColor oriColor = new RGBColor(12.0f/255,50.0f/255,146.0f/255);
	public static final RGBColor oriColor = new RGBColor(225.0f/255,220.0f/255,22.0f/255);
	
	private StateGraphVisualizer visual;
	protected GLU glu = new GLU();
	private GLUT glut = new GLUT();
	private double eyex = 0;
	private double eyey = 0;
	private double eyez = 5;
	
	private double phi = 0; // 0--360
	private double theta = 90; // 0--180
	private int dragStartX = 0;
	private int dragStartY = 0;
	
	private IntBuffer viewport = IntBuffer.allocate(4);
	private FloatBuffer modelview = FloatBuffer.allocate(4);
	private FloatBuffer projection = FloatBuffer.allocate(4);
	private FloatBuffer winPos = FloatBuffer.allocate(3);
	
	private float[] lightPosition = new float[4];
	private float[] whiteLight = new float[4];

	
	private StateNode nodeSelected = null;
	private Point mousePosition = null;
	private TextRenderer tr = new TextRenderer(new Font("SansSerif", Font.PLAIN, 30));
	public NetAgent na;
	
	public StateGraphVisualizeListener(StateGraphVisualizer visual) {
		this.visual = visual;
	}
	
	@Override
	public void display(GLAutoDrawable gld) {
		/*
		if(na.added_sg) {
			for(StateNode sn : na.newStateNodes) {
				visual.sg.addNode(sn);
			}
			for(Edge se : na.newStateEdges) {
				visual.sg.addEdge(new StateEdge(visual.sg.getNodeById(se.from), visual.sg.getNodeById(se.to)));
			}
			na.newStateNodes.clear();
			na.added_sg = false;
		}
		*/
		
		if(na.added_high) {
			for(String str : na.highlightedState) {
				StateNode sn = visual.sg.getNodeByLabel(str);
				sn.setPicked(true);
				sn.setColor(highlight.getRed(), highlight.getGreen(), highlight.getBlue());

			}
			na.highlightedState.clear();
			na.added_high = false;
		}
		
		if(na.added_unhi) {
			for(String str : na.unHighlightedState) {
				StateNode sn = visual.sg.getNodeByLabel(str);
				sn.setPicked(false);
				sn.clearColor();
			}
			na.unHighlightedState.clear();
			na.added_unhi = false;
		}
		
		
		GL2 gl = (gld).getGL().getGL2();
		gl.glLoadIdentity();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		
		/**
		 * painting code
		 */
		int drawedNodes = 0;
		for(StateNode sn : visual.sg.getNodes()) {
			if(!sn.isVisible()) {
				continue;
			}
			gl.glPushMatrix();
			gl.glTranslated(sn.getX(), sn.getY(), sn.getZ());
			RGBColor color = sn.getColor();
			gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
			glut.glutSolidSphere(sn.getSize(), 10, 10);
			drawedNodes ++;
//			if(sn.isLableVisible()) {
//				tr.begin3DRendering();
//				tr.draw3D(sn.getLabel(), 0, 0, 0, 0.005f);
//				tr.flush();
//				tr.end3DRendering();
//			}
//			if(sn.isPicked()) {
//				tr.begin3DRendering();
//				tr.draw3D(sn.getLabel(), 0, 0, 0, 0.005f);
//				tr.flush();
//				tr.end3DRendering();
//			}
//			if(sn.isPicked()) {
//				tr.beginRendering(gld.getSurfaceWidth(), gld.getSurfaceHeight());
//				Point p = this.getScreenPoint(gld, sn.getX(), sn.getY(), sn.getZ());
//				if(p != null) {
//					tr.setColor(0, 0, 0, 1);
//					tr.draw(sn.getLabel(), p.x, p.y);
//					System.out.println("Showing label of sn: "+sn.getLabel()+" in position: "+String.valueOf(p.x)+","+String.valueOf(p.y));
//				}
//				tr.endRendering();
//			}
			
			gl.glPopMatrix();
			for(StateEdge se : visual.sg.getPostEdges(sn)) {
				StateNode psn = se.getTo();
				if(!psn.isVisible()) {
					continue;
				}
				gl.glPushMatrix();
//				RGBColor snc = new RGBColor(178.0f/255,178.0f/255,178.0f/255);
				RGBColor snc = new RGBColor(0,0,0);
				if(psn.isPicked()) {
					snc = sn.getColor();
					se.setSize(1.0f);
				}
				gl.glColor3f(snc.getRed(), snc.getGreen(), snc.getBlue());
				gl.glLineWidth(se.getSize());
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3d(sn.getX(), sn.getY(), sn.getZ());
				gl.glVertex3d(psn.getX(), psn.getY(), psn.getZ());
				gl.glColor3f(0, 0, 0);

				double dx = psn.getX() - sn.getX();
				double dy = psn.getY() - sn.getY();
				double dz = psn.getZ() - sn.getZ();
				double d = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2)+Math.pow(dz, 2));
				double x = sn.getX()+dx*((d-0.2)/d);
				double y = sn.getY()+dy*((d-0.2)/d);
				double z = sn.getZ()+dz*((d-0.2)/d);
//				gl.glTranslated(x, y, z);
//				gl.glColor3f(0, 1, 1);
//				glut.glutSolidSphere(0.03, 10, 10);
//				gl.glColor3f(1, 1, 1);
				double x1 = sn.getX()+dx*((d-0.3)/d);
	            double y1 = sn.getY()+dy*((d-0.3)/d);
	            double z1 = sn.getZ()+dz*((d-0.3)/d);
	            
	            //arrow
				gl.glVertex3d(x, y, z);
				gl.glVertex3d(x1 + 0.04, y1, z1);
				
				gl.glVertex3d(x, y, z);
				gl.glVertex3d(x1 - 0.04, y1, z1);
				
				gl.glVertex3d(x, y, z);
				gl.glVertex3d(x1, y1, z1 - 0.04);
				gl.glEnd();
				gl.glPopMatrix();
			}
			
		}
//		tr.beginRendering(gld.getSurfaceWidth(), gld.getSurfaceHeight());
//		tr.setColor(0, 0, 0, 1);
//		tr.draw(String.valueOf(drawedNodes), 10, 10);
//		tr.endRendering();
		
		gl.glFlush();
		selectNode(gl, mousePosition);
		mousePosition = null;
		setCamera(gld);
	}
	
	public Point getScreenPoint(GLAutoDrawable gld, double x, double y, double z) {
//		this.modelview.clear();
//		this.projection.clear();
//		this.viewport.clear();
//		this.winPos.clear();
		 IntBuffer viewport = IntBuffer.allocate(4);
		 FloatBuffer modelview = FloatBuffer.allocate(16);
		 FloatBuffer projection = FloatBuffer.allocate(16);
		 FloatBuffer winPos = FloatBuffer.allocate(3);
		 
		 GL2 gl = (GL2) gld.getGL();
		 
		 gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview);
		 gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection);
		 gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);

		
		boolean succeed = glu.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, winPos);
//		glu.gluProject(x, y, z, arg3, arg4, arg5, arg6)
		System.out.println("get screen point: "+winPos.get(0)+","+winPos.get(1)+","+winPos.get(2));
		if(succeed) {
			return new Point((int)(winPos.get(0)), viewport.get(3)-(int)(winPos.get(1)));
		} else {
			return null;
		}
	}
	
	private void selectNode(GL2 gl, Point p) {
		if (p == null) {
			return;
		}
		FloatBuffer projection = FloatBuffer.allocate(16);
		FloatBuffer modelview = FloatBuffer.allocate(16);
		IntBuffer viewport = IntBuffer.allocate(4);
		FloatBuffer bz = FloatBuffer.allocate(1);
		FloatBuffer objxyz = FloatBuffer.allocate(3);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
		// float x = visibleEvent.getX();
		int x = (int) p.getX();
		int y = (int) (viewport.get(3) - p.getY());
		gl.glReadPixels(x, y, 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, bz);
		
		float z = bz.get(0);
		glu.gluUnProject(x, y, z, modelview, projection, viewport, objxyz);
		// System.out.println("x, y, z:"+objxyz.get(0)+", "+objxyz.get(1)+",
		// "+objxyz.get(2));
		StateNode n = visual.sg.getNearestNode(objxyz.get(0), objxyz.get(1), objxyz.get(2));
		if(n == null) {
			if(nodeSelected  != null) {
//				nodeSelected.resetSize();
				nodeSelected.clearColor();
				nodeSelected = null;
			}
		} else {
			if(nodeSelected != null) {
				if(!n.getId().equals(nodeSelected.getId())) {
//					nodeSelected.resetSize();
					nodeSelected.clearColor();
					nodeSelected = n;
//					nodeSelected.setSize(0.15);
					nodeSelected.setColor(new RGBColor(0,1,0));
				}
			} else {
				nodeSelected = n;
//				nodeSelected.setSize(0.15);
				nodeSelected.setColor(new RGBColor(0,1,0));
			}
		}
	}
	

	
	public void setCamera(GLAutoDrawable gld) {
		GL2 gl = gld.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		visual.showPanel.repaint();
	}

	@Override
	public void dispose(GLAutoDrawable gld) {
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
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
//		gl.glClearColor(238.0f / 255.0f, 226.0f / 255.0f, 185.0f / 255.0f, 0.0f);
		gl.glClearColor(1, 1, 1, 0);
//		gl.glClearColor(227.0f/255, 237.0f/255, 205.0f/255, 0);
		
		gl.glClearDepth(1.0);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
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
		if(width == 0 || height == 0) {
			glu.gluPerspective(60.0f, 1, 1.0f, 10000.0f);
		} else {
			glu.gluPerspective(60.0f, width / height, 1.0f, 10000.0f);
		}
//		glu.gluPerspective(60.0f, gld.getSurfaceWidth() / gld.getSurfaceHeight(), 1.0f, 10000.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// gl.glLightfv(arg0, arg1, arg2, arg3);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);
		 glu.gluOrtho2D(0.0, 500.0, 0.0, 300.0);
		 
		 //Enable Fog
			gl.glEnable(GL2.GL_FOG);
//			gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
			gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP2);
			FloatBuffer fb = FloatBuffer.allocate(4);
			fb.put(0, 1.0f);
			fb.put(1, 1.0f);
			fb.put(2, 1.0f);
			fb.put(3, 1.0f);
			gl.glFogfv(GL2.GL_FOG_COLOR, fb);
			gl.glFogf(GL2.GL_FOG_DENSITY, 0.03f);
			gl.glHint(GL2.GL_FOG_HINT, GL2.GL_DONT_CARE);
			gl.glFogf(GL2.GL_FOG_START, 1.0f);
			gl.glFogf(GL2.GL_FOG_END, 5.0f);
		 
		gld.swapBuffers();
	}

	@Override
	public void reshape(GLAutoDrawable gld, int x, int y, int width, int height) {
		GL2 gl = gld.getGL().getGL2();
		
		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60.0f, width / height, 1.0f, 10000.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);

		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dragedX = e.getX() - dragStartX;
		int dragedY = e.getY() - dragStartY;
//		System.out.println("dragedX: "+dragedX+", dragedY: "+dragedY);
		this.phi = ((dragedX/10)+phi) % 360;
		this.theta = ((dragedY/10)+theta) % 360;
		double r = Math.sqrt(Math.pow(eyex,2)+Math.pow(eyey, 2)+Math.pow(eyez,2));
		eyez = r*Math.sin(theta*Math.PI/180)*Math.cos(phi*Math.PI/180);
		eyex = r*Math.sin(theta*Math.PI/180)*Math.sin(phi*Math.PI/180);
		eyey = r*Math.cos(theta*Math.PI/180);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int step = e.getWheelRotation();
		double current = Math.sqrt(Math.pow(eyex, 2) + Math.pow(eyey, 2) + Math.pow(eyez, 2));
		eyex += eyex / current * step;
		eyey += eyey / current * step;
		eyez += eyez / current * step;
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.mousePosition = e.getPoint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.dragStartX = e.getX();
		this.dragStartY = e.getY();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}

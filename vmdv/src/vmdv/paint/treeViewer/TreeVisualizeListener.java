package vmdv.paint.treeViewer;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import vmdv.network.NetAgent;
import vmdv.paint.graph.RGBColor;
import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeEdge;
import vmdv.paint.graph.TreeNode;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

public class TreeVisualizeListener
		implements GLEventListener, KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {
	public static final RGBColor red = new RGBColor(1,0,0);
	public static final RGBColor green = new RGBColor(0,1,0);
	public static final RGBColor blue = new RGBColor(0,0,1);
//	public static final RGBColor highlight = new RGBColor(198.0f/255,145.0f/255,69.0f/255);
	public static final RGBColor highlight = new RGBColor(1,0,0);
//	public static final RGBColor rootColor = new RGBColor(56.0f/255,94.0f/255,15.0f/255);
	public static final RGBColor rootColor = new RGBColor(0,0,0);
	public static final RGBColor oriColor = new RGBColor(0,0,0);
	public static final RGBColor hoverColor = new RGBColor(186.0f/255,52.0f/255,10.0f/255);
	public static final RGBColor stepColor = new RGBColor(1,0,0);
//	public static final RGBColor fromColor = new RGBColor(12.0f/255,22.0f/255,146.0f/255);
	public static final RGBColor fromColor = new RGBColor(44.0f/255,82.0f/255,68.0f/255);
	public static final RGBColor toColor = new RGBColor(0,1,0);
	public static final RGBColor same_subtree_color = new RGBColor(199.0f/255, 134.0f/255, 5.0f/255);
	public static final RGBColor childColor = new RGBColor(183.0f/255,143.0f/255,71.0f/255);
	
	private TreeVisualizer visual;

	private float[] lightPosition = new float[4];
	private float[] whiteLight = new float[4];
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
	private FloatBuffer screenPoint = FloatBuffer.allocate(3);

	protected TreeNode nodeSelected = null;
	private Point mousePosition = null;
	protected boolean showingPop = false;
	private TextRenderer tr = new TextRenderer(new Font("SansSerif", Font.PLAIN, 30));
	// private RGBColor preColor = null;

	private LinkedList<AssistAffect> affect = new LinkedList<AssistAffect>();
/*
	public ArrayList<TreeNode> newTreeNodes = new ArrayList<TreeNode>();
	public ArrayList<Edge> newTreeEdges = new ArrayList<Edge>();
	public ArrayList<StateNode> newStateNodes = new ArrayList<StateNode>();
	public ArrayList<Edge> newStateEdges = new ArrayList<Edge>();
	public ArrayList<String> highlightedState = new ArrayList<String>();
	public ArrayList<String> unHighlightedState = new ArrayList<String>();
	public boolean added = false;
*/
	public NetAgent na;
	
	public TreeVisualizeListener(TreeVisualizer visual) {
		this.visual = visual;
	}

	public void setEye(double x, double y, double z) {
		eyex = x;
		eyey = y;
		eyez = z;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mousePosition = e.getPoint();
		if (e.isMetaDown()) {
			showingPop = true;
			if (nodeSelected == null) {
				System.out.println("show back pop");
				visual.backPop.show(e.getComponent(), e.getX(), e.getY());
			} else {
				visual.nodePop.show(e.getComponent(), e.getX(), e.getY());
			}
			// visual.backPop.setVisible(true);
		} else {
			showingPop = false;
			if (e.getClickCount() == 2) {
				// hide or show subtree
			} else {
				// pick or unpick node
				if (nodeSelected != null) {
					if (nodeSelected.isPicked()) {
//						visual.subListener.localTree = new Tree();
						this.addAffect(new UnPickNodeAffect(nodeSelected));
						visual.operateListener.unHightLightState(nodeSelected.getId());
					} else {
						/*
						visual.subListener.localTree = new Tree();
						Tree t = visual.subListener.localTree;
//						t.addNode(nodeSelected);
						
						LinkedList<TreeNode> tmpNodes = new LinkedList<TreeNode>();
						TreeNode tn = new TreeNode(nodeSelected.getPaintId(), nodeSelected.getId(), nodeSelected.getLabel());
						tn.setColor(nodeSelected.getColor());
						tn.setSize(nodeSelected.getSize());
						tn.setOriColor(nodeSelected.getOriColor());
						tn.setOriSize(nodeSelected.getOriSize());
						double ox = nodeSelected.getX();
						double oy = nodeSelected.getY();
						double oz = nodeSelected.getZ();
						tn.setXYZ(ox, oy, oz);
						tmpNodes.addFirst(tn);
						t.addNode(tn);
						System.out.println("added to tmpNodes: "+tn.getPaintId());
						
						TreeNode tmpNode = nodeSelected;
						while(tmpNode != null) {
							TreeNode ttn = visual.tree.getPreNode(tmpNode);

							if(ttn != null) {
								TreeNode nttn = new TreeNode(ttn.getPaintId(), ttn.getId(),ttn.getLabel());
								nttn.setColor(ttn.getColor());
								nttn.setSize(ttn.getSize());
								nttn.setOriColor(ttn.getOriColor());
								nttn.setOriSize(ttn.getOriSize());
								ox = ttn.getX();
								oy = ttn.getY();
								oz = ttn.getZ();
								nttn.setXYZ(ox, oy, oz);
								tmpNodes.addFirst(nttn);
								t.addNode(nttn);
								System.out.println("added to tmpNodes: "+nttn.getPaintId());
								tmpNode = ttn;
							} else {
								tmpNode = null;
							}
						}
						
						TreeNode tttn = tmpNodes.removeFirst();
						t.setRoot(tttn);
						for(TreeNode ttttn : tmpNodes) {
							t.addEdge(tttn.getPaintId(), ttttn.getPaintId());
							tttn = ttttn;
						}
						
//						TreeNode tn = new TreeNode(nodeSelected.getPaintId(), nodeSelected.getId(), nodeSelected.getLabel());
//
//						t.addNode(tn);
//						RGBColor c = nodeSelected.getColor();
//						tn.setColor(c.getRed(), c.getGreen(), c.getBlue());
//						tn.setSize(nodeSelected.getSize());
//						double ox = nodeSelected.getX();
//						double oy = nodeSelected.getY();
//						double oz = nodeSelected.getZ();
//						tn.setXYZ(0,0,0);
//						tn.setOriColor(nodeSelected.getOriColor());
//						tn.setOriSize(nodeSelected.getOriSize());
//						t.setRoot(tn);
//						for(TreeEdge te : visual.tree.getEdges(nodeSelected)) {
//							
////							t.addNode(te.getTo());
//							TreeNode tno = te.getTo();
//							TreeNode to = new TreeNode(tno.getPaintId(), tno.getId(), tno.getLabel());
//							RGBColor cto = tno.getColor();
//							to.setColor(cto.getRed(), cto.getGreen(), cto.getBlue());
//							t.addNode(to);
//							to.setSize(tno.getSize());
//							to.setXYZ(tno.getX()-ox, tno.getY()-oy, tno.getZ()-oz);
//							to.setOriSize(tno.getOriSize());
//							to.setOriColor(tno.getOriColor());
//							t.addEdge(nodeSelected.getPaintId(), te.getTo().getPaintId());
//						}
						
//						this.addAffect(new PickNodeAffect(nodeSelected, new RGBColor(0,1,0)));
 */
						this.addAffect(new PickNodeAffect(visual,nodeSelected, TreeVisualizeListener.highlight));
						visual.operateListener.hightLightState(nodeSelected.getId());
					}
				} else {
//					visual.subListener.localTree = new Tree();
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.dragStartX = e.getX();
		this.dragStartY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.isControlDown()) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_S:
				visual.searchByIdDlg.setVisible(true);
				break;
			case KeyEvent.VK_F:
				visual.searchByTextDlg.setVisible(true);
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			// this.rotateYZ = ((this.rotateYZ+5) % 36)*Math.PI/18;
			eyey++;
			break;
		case KeyEvent.VK_DOWN:
			// this.rotateYZ = ((this.rotateYZ-5) % 36)*Math.PI/18;
			eyey--;
			break;
		case KeyEvent.VK_LEFT:
			// this.rotateXZ = ((this.rotateXZ-5) % 36)*Math.PI/18;
			eyex--;
			break;
		case KeyEvent.VK_RIGHT:
			// this.rotateXZ = ((this.rotateXZ+5) % 36)*Math.PI/18;
			eyex++;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable gld) {
//		gld.setAutoSwapBufferMode(true);
//		GL3 gl = gld.getGL().getGL3();
		GL2 gl = (gld).getGL().getGL2();
		
	
		
		gl.glLoadIdentity();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		
		
		

		LinkedList<TreeNode> painting = new LinkedList<TreeNode>();
		painting.addLast(visual.tree.getRoot());
		int drawedNodes = 0;
		while (!painting.isEmpty()) {
//			System.out.println("painting node...");
			TreeNode tn = painting.removeFirst();
			if(tn == null) {
				return;
			}
			if (tn.isVisible()) {
				RGBColor color = tn.getColor();
				gl.glPushMatrix();
				gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
				gl.glTranslated(tn.getX(), tn.getY(), tn.getZ());
				glut.glutSolidSphere(tn.getSize(), 10, 10);
				drawedNodes ++;
				gl.glColor3f(1, 1, 1);
				
				if(tn.isLableVisible()) {
					tr.begin3DRendering();
					tr.draw3D(tn.getLabel(), 0, 0, 0, 0.005f);
					tr.flush();
					tr.end3DRendering();
				}
				if(tn.showChildLabel) {
					tr.begin3DRendering();
					tr.draw3D(tn.childLabel, 0, 0, 0, 0.01f);
					tr.flush();
					tr.end3DRendering();
				}
				gl.glPopMatrix();
				
				gl.glDisable(GL2.GL_LIGHTING);
				gl.glDisable(GL2.GL_LIGHT0);
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glEnable(GL2.GL_LIGHT0);

				if(tn.isShowSubtree()) {
					for (TreeEdge e : visual.tree.getEdges(tn)) {
						TreeNode ton = e.getTo();
						if (ton.isVisible()) {
							gl.glPushMatrix();
							RGBColor edgeColor = e.getColor();
							gl.glColor3f(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue());
							gl.glLineWidth(e.getSize());
							gl.glBegin(GL2.GL_LINES);
							gl.glVertex3d(tn.getX(), tn.getY(), tn.getZ());
							gl.glVertex3d(ton.getX(), ton.getY(), ton.getZ());
							gl.glEnd();
							gl.glPopMatrix();

							painting.addLast(ton);
						}
					}
				}
			}
		}
		
		
//		tr.beginRendering(gld.getSurfaceWidth(), gld.getSurfaceHeight());
//		tr.setColor(0, 0, 0, 1);
//		tr.draw(String.valueOf(drawedNodes), 10, 10);
//		tr.endRendering();
		
//		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
//		gl.glEnable(GL2.GL_COLOR_MATERIAL);
//		gl.glShadeModel(GL2.GL_SMOOTH);
//		gl.glEnable(GL2.GL_LIGHTING);
//		gl.glEnable(GL2.GL_LIGHT0);
//		gl.glEnable(GL2.GL_LINE_SMOOTH);
//		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
//		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
//		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
//		gl.glEnable(GL2.GL_BLEND);
//		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
//		gl.glEnable(GL2.GL_DEPTH_TEST);
//
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);


		if(!showingPop) {
			selectNode(gl, mousePosition);
			mousePosition = null;
		}

		// show color, label or else
		if (!affect.isEmpty()) {
			affect.removeFirst().affect(gld);
		}

//		gl.glLoadIdentity();
//		gl.glColor3f(0, 0, 0);
		gl.glFlush();
		
		setCamera(gld);
//		System.out.println("displayed...");
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
		TreeNode n = visual.tree.getNearestNode(objxyz.get(0), objxyz.get(1), objxyz.get(2));
		if(n == null) {
			if(nodeSelected  != null) {
				nodeSelected.resetSize();
				nodeSelected.clearColor();
				nodeSelected = null;
			}
		} else {
			if(nodeSelected != null) {
				if(!n.getId().equals(nodeSelected.getId())) {
					nodeSelected.resetSize();
					nodeSelected = n;
					nodeSelected.setSize(0.3);
					nodeSelected.setColor(hoverColor);
				}
			} else {
				nodeSelected = n;
				nodeSelected.setSize(0.3);
				nodeSelected.setColor(hoverColor);
			}
		}
	}

	public void addAffect(AssistAffect aa) {
		affect.addLast(aa);
	}

	public void setCamera(GLAutoDrawable gld) {
		GL2 gl = gld.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		
		visual.showPanel.repaint();
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
//		whiteLight[0] = 0.8f;
//		whiteLight[1] = 0.8f;
//		whiteLight[2] = 0.8f;
//		whiteLight[3] = 1.0f;
		
		whiteLight[0] = 1.0f;
		whiteLight[1] = 1.0f;
		whiteLight[2] = 1.0f;
		whiteLight[3] = 1.0f;
		GL2 gl = gld.getGL().getGL2();

		// GLUT glut = new GLUT();
		gld.setAutoSwapBufferMode(true);
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
//		gl.glClearColor(238.0f / 255.0f, 226.0f / 255.0f, 185.0f / 255.0f, 0.0f);
//		gl.glClearColor(227.0f/255, 237.0f/255, 205.0f/255, 0);
		gl.glClearColor(1, 1, 1, 0);
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
		
		FloatBuffer fba = FloatBuffer.allocate(4);
		fba.put(0, 0.7f);
		fba.put(1, 0.7f);
		fba.put(2, 0.7f);
		fba.put(3, 1.0f);
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, fba);
//		glu.gluOrtho2D(0.0, 500.0, 0.0, 300.0);
		
		//Fog
		gl.glEnable(GL2.GL_FOG);
//		gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
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
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setCamera(gld);
	}

	@Override
	public void reshape(GLAutoDrawable gld, int x, int y, int width, int height) {
		GL2 gl = gld.getGL().getGL2();
		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if(width == 0 || height == 0) {
			glu.gluPerspective(60.0f, 1, 1.0f, 10000.0f);
		} else {
			glu.gluPerspective(60.0f, width / height, 1.0f, 10000.0f);
		}
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);

		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int step = e.getWheelRotation();
		double current = Math.sqrt(Math.pow(eyex, 2) + Math.pow(eyey, 2) + Math.pow(eyez, 2));
		if(current < 1.0 && step < 0) {
			return;
		}
		eyex += eyex / current * step;
		eyey += eyey / current * step;
		eyez += eyez / current * step;
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
		mousePosition = e.getPoint();
		// System.out.println("Mouse moving: " + mousePosition.getX() + "," +
		// mousePosition.getY());
	}

}

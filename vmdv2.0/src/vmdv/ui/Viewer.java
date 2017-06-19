package vmdv.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import vmdv.control.GraphLayout;
import vmdv.control.Session;
import vmdv.dev.AssistAffect;
import vmdv.dev.PopupItem;
import vmdv.model.AbstractGraph;
import vmdv.model.AbstractNode;
//import vmdv.model.NodeProperty;
import vmdv.model.NodeProperty;

public class Viewer extends JFrame {
	private GLU glu = new GLU();
	private GLUT glut = new GLUT();
	
	private GLJPanel renderPanel;
	protected JPopupMenu backPop, nodePop;
	private GLEventListener glistener;
	private KeyListener keyListener;
	private MouseListener mouseListener;
	private MouseWheelListener mwListener;
	private MouseMotionListener mmListener;
	
	protected AbstractGraph graph;
	
	protected double eyex = 0;
	protected double eyey = 0;
	protected double eyez = 5;
	protected double phi = 0; // 0--360
	protected double theta = 90; // 0--180
	protected int dragStartX = 0;
	protected int dragStartY = 0;
	public LinkedList<AssistAffect> affect = new LinkedList<AssistAffect>();
	protected boolean popupShowed = false;
	protected Point mousePosition = null;
	protected boolean singleSelection = true;
	
	
	protected AbstractNode hoverNode = null;
	protected NodeProperty hoverNodeState = null;
//	protected AbstractNode nodeSelected = null;
//	protected NodeProperty hoverNodeState = null;
	protected Set<AbstractNode> nodesSelected = new HashSet<AbstractNode>();
//	protected AbstractNode nodeFocused = null;
//	protected LinkedList<NodeProperty> multiNodesSelectedState = new LinkedList<NodeProperty>();
	protected GraphLayout layout;
	protected Session session;
	
//	protected boolean multiSelection = false;
	
	public Viewer(String title) {
		this.setTitle(title);
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities glcaps = new GLCapabilities(glp);
		glcaps.setDoubleBuffered(true);
		this.renderPanel = new GLJPanel(glcaps);
		this.add(renderPanel);
		this.backPop = new JPopupMenu();
		this.nodePop = new JPopupMenu();
//		glistener.
//		this.renderPanel.addm
	}
	
	public void addBackgroundPopup(PopupItem pop) {
		if(pop != null) {
			JMenuItem item = new JMenuItem(pop.getLabel());
			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					pop.action(Viewer.this.session);
				}
				
			});
			this.backPop.add(item);
		}
	}
	
	public void addNodePopup(PopupItem pop) {
		if(pop != null) {
			JMenuItem item = new JMenuItem(pop.getLabel());
			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					pop.action(Viewer.this.session);
				}
				
			});
			this.nodePop.add(item);
		}
	}
	
	public Set<AbstractNode> getSelectedNode() {
		return nodesSelected;
	}
	
	public void setGraphLayout(GraphLayout layout) {
		this.layout = layout;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	public void setEye(double x, double y, double z) {
		eyex = x;
		eyey = y;
		eyez = z;
	}
	
	public AbstractGraph getGraph() {
		return graph;
	}
	public void setGraph(AbstractGraph graph) {
		this.graph = graph;
	}

	public void refreshGLPanel() {
		renderPanel.repaint();
	}
	
	public void registerGLHandler(GLEventHandler glistener) {
//		glistener.setGraph(this.getGraph());
		glistener.glu = glu;
		glistener.glut = glut;
		glistener.setViewer(this);
		this.glistener = glistener;
		renderPanel.addGLEventListener(glistener);
	}
	
	public void registerKeyHandler(KeyHandler keyHandler) {
		keyHandler.setViewer(this);
		this.keyListener = keyHandler;
		renderPanel.addKeyListener(keyHandler);
	}
	
	public void registerMouseHandler(MouseHandler mouseHandler) {
		mouseHandler.setViewer(this);
		this.mouseListener = mouseHandler;
		renderPanel.addMouseListener(mouseHandler);
	}
	
	public void registerMouseMotionHandler(MouseMotionHandler mmHandler) {
		mmHandler.setViewer(this);
		this.mmListener = mmHandler;
		renderPanel.addMouseMotionListener(mmHandler);
	}
	
	public void registerMouseWheelHandler(MouseWheelHandler mwHandler) {
		mwHandler.setViewer(this);
		this.mwListener = mwHandler;
		renderPanel.addMouseWheelListener(mwHandler);
	}
	
	protected AbstractNode selectNode(GL2 gl, Point p) {
		if (p == null) {
			return null;
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
		AbstractNode n = graph.getNearestNode(objxyz.get(0), objxyz.get(1), objxyz.get(2));
		return n;
	}
	
	public void showView() {
		this.setVisible(true);
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



}

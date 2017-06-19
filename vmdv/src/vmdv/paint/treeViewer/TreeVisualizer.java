package vmdv.paint.treeViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import vmdv.dialogs.SearchByIdDlg;
import vmdv.dialogs.SearchByTextDlg;
import vmdv.dialogs.ShowTextDlg;
import vmdv.paint.graph.Tree;
import vmdv.paint.graph.TreeNode;
import vmdv.paint.listener.ProofEdgeInfo;
import vmdv.paint.listener.ProofNodeInfo;
import vmdv.paint.listener.TreeGenerationListener;
import vmdv.paint.listener.TreeVisualOperateListener;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

public class TreeVisualizer implements TreeGenerationListener {
//	private int nodeCount = 0;
	protected JDialog searchByIdDlg, searchByTextDlg, showTextDlg;
	public JFrame mainFrame;
	protected GLJPanel showPanel;
	//subShowPanel;
	protected JPanel labelPanel;
	protected JLabel textLabel;
	protected JPopupMenu backPop;
	protected JPopupMenu nodePop;
	public TreeVisualizeListener listener;
	public SubTreeVisualizeListener subListener;
	public Tree tree;
	protected TreeNode root;
	protected TreeControlPanel tcp;
	protected TreeVisualOperateListener operateListener;
	
	public TreeVisualizer(String title) {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities glcaps = new GLCapabilities(glp);
		glcaps.setDoubleBuffered(true);
		showPanel = new GLJPanel(glcaps);
//		subShowPanel = new GLJPanel(glcaps);
		labelPanel = new JPanel();
		textLabel = new JLabel();
//		textLabel.setBackground(Color.RED);
//		textLabel.setHorizontalAlignment(JLabel.LEFT);
//		labelPanel.setLayout(null);
		labelPanel.add(textLabel, BorderLayout.CENTER);
		textLabel.setMinimumSize(new Dimension(700,300));
		iniBackPop();
		iniNodePop();
		tree = new Tree();
		
		mainFrame = new JFrame(title);	
		tcp = new TreeControlPanel();
		tcp.setMinimumSize(new Dimension(300, 600));
		showPanel.setMinimumSize(new Dimension(600,600));
		GridLayout gl = new GridLayout();
		gl.setColumns(2);
		gl.setRows(1);
//		mainFrame.getContentPane().setLayout(gl);
//		mainFrame.getContentPane().add(tcp);
//		mainFrame.getContentPane().add(showPanel);
		
//		JPanel jp = new JPanel();
		mainFrame.getContentPane().setLayout(null);
		mainFrame.getContentPane().add(showPanel);
//		mainFrame.getContentPane().add(subShowPanel);
		mainFrame.getContentPane().add(labelPanel);
		showPanel.setBounds(new Rectangle(0,0,1000,700));
//		subShowPanel.setBounds(new Rectangle(700,700,300,300));
		labelPanel.setBounds(new Rectangle(0,700,1000,300));
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				listener = new TreeVisualizeListener(TreeVisualizer.this);
				subListener = new SubTreeVisualizeListener(TreeVisualizer.this);
				showPanel.addGLEventListener(listener);
				showPanel.addMouseListener(listener);
				showPanel.addKeyListener(listener);
				showPanel.addMouseMotionListener(listener);
				showPanel.addMouseWheelListener(listener);
				
//				subShowPanel.addGLEventListener(subListener);
//				subShowPanel.addMouseListener(subListener);
//				subShowPanel.addKeyListener(subListener);
//				subShowPanel.addMouseMotionListener(subListener);
//				subShowPanel.addMouseWheelListener(subListener);
//				mainFrame.getContentPane().add(tcp, BorderLayout.WEST);
			}
			
		});

		
//		mainFrame.getContentPane().add(jp, BorderLayout.CENTER);
//		jp.setLayout(new BorderLayout(0, 0));
//		jp.add(showPanel);
		iniDlgs();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public void setTextLabel(final String str) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				textLabel.setText(str);
			}
			
		});
		
	}
	
private void iniDlgs() {
//		searchByIdDlg = new JDialog(mainFrame, "Hightlight Node by ID" ,true);
		this.showTextDlg = new ShowTextDlg(mainFrame, true);
		showTextDlg.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		showTextDlg.setVisible(false);
		searchByIdDlg = new SearchByIdDlg(this, true);
		searchByIdDlg.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		searchByIdDlg.setVisible(false);
		searchByTextDlg = new SearchByTextDlg(this, true);
		searchByTextDlg.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		searchByTextDlg.setVisible(false);
//		showTextDlg.set
	}

	public void show() {
		mainFrame.setSize(1000, 1000);
		
		mainFrame.setLocation(200, 200);
		mainFrame.setVisible(true);
	}
	
	public void addBackPopup(final PopupItem pi) {
		JMenuItem jmi = new JMenuItem(pi.getLabel());
		jmi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pi.action(TreeVisualizer.this);
			}
			
		});
		backPop.add(jmi);
	}
	
	private void iniBackPop() {
		backPop = new JPopupMenu();
		final ResetEyePopup rep = new ResetEyePopup("Reset Eye Coord");
		JMenuItem resetEye = new JMenuItem(rep.getLabel());
		resetEye.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				listener.setEye(0, 0, 5);
				rep.action(TreeVisualizer.this);
//				listener.showingPop = false;
			}
		});
		JMenuItem clearColor = new JMenuItem("Clear Color");
		clearColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new ClearColorAffect(TreeVisualizer.this, tree));
//				listener.showingPop = false;
			}
		});
//		JMenuItem resetTree = new JMenuItem("Reset Tree");
		JMenuItem resetRoot = new JMenuItem("Reset Root");
		resetRoot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new ResetRootAffect(tree, root));
			}
		});
		backPop.add(clearColor);
		backPop.add(resetEye);
		backPop.add(resetRoot);
//		backPop.add(resetTree);
	}
	private void iniNodePop() {
		nodePop = new JPopupMenu();
		
		JMenuItem showLabel = new JMenuItem("Show Label");
		showLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new ShowHideLabelAffect(listener.nodeSelected));
//				listener.showingPop = false;
			}
		});
		JMenuItem setRoot = new JMenuItem("Set as Root");
		setRoot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new SetAsRootAffect(tree, listener.nodeSelected));
//				listener.showingPop = false;
			}
		});
		JMenuItem hideShowSubTree = new JMenuItem("Hide/Show Subtree");
		hideShowSubTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new HideExpandSubTreeAffect(listener.nodeSelected));
//				listener.showingPop = false;
			}
		});
		
		JMenuItem stepForward = new JMenuItem("One Step Forward");
		stepForward.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				listener.addAffect(new OneStepForwardAffect(TreeVisualizer.this, listener.nodeSelected));
			}
			
		});
		
		JMenuItem highlightSubtree = new JMenuItem("Highlight Subtree");
		highlightSubtree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				listener.addAffect(new HighlightSubtreeAffect(TreeVisualizer.this.tree, listener.nodeSelected));
			}
			
		});
		
		JMenuItem highlightAncestors = new JMenuItem("Highlight Ancestors");
		highlightAncestors.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				listener.addAffect(new HighlightAncestorsAffect(TreeVisualizer.this.tree, listener.nodeSelected));
			}
			
		});
		
		
		nodePop.add(showLabel);
		nodePop.add(setRoot);
		nodePop.add(hideShowSubTree);
		nodePop.add(stepForward);
		nodePop.add(highlightSubtree);
		nodePop.add(highlightAncestors);
	}
	
	public void setOperateListener(TreeVisualOperateListener operateListener) {
		this.operateListener = operateListener;
	}

	@Override
	public void addNode(ProofNodeInfo pni) {
		if(pni == null) {
			System.out.println("add null node");
		}
		String str = "NULL";
		if (pni.getLable() != null) {
			str = pni.getLable();
		}
		TreeNode n = new TreeNode(pni.getPaintId(), pni.getId(), str);
//		n.setPaintId(String.valueOf(nodeCount++));
		n.setOriColor(TreeVisualizeListener.oriColor);
		n.setOriSize(0.2);
		n.clearColor();
		n.resetSize();
		tree.addNode(n);
//		System.out.println("node "+pni.getPaintId()+","+pni.getId()+" added...");
		if(pni.isRoot()) {
			root = n;
			tree.setRoot(n);
			n.setOriColor(TreeVisualizeListener.rootColor);
			n.setOriSize(0.25);
			n.clearColor();
			n.resetSize();
//			System.out.println("Root added.");
		}
	}

	@Override
	public void removeNode(ProofNodeInfo pni) {
		tree.deleteNode(pni.getId());
	}

	@Override
	public void addEdge(ProofEdgeInfo pei) {
		
		tree.addEdge(pei.getFrom(), pei.getTo());
//		System.out.println("TreeEdge adding: "+pei.getFrom()+"--"+pei.getTo());
	}

	@Override
	public void removeEdge(ProofEdgeInfo pei) {
		tree.deleteEdge(pei.getFrom(), pei.getTo());
	}
	
	@Override
	public void updateLayout() {
		tree.layout();
		new Thread(new UpdateLayoutThread()).start();
	}
	
//	public void layoutAgain() {
//		new Thread(new UpdateLayoutThread()).start();
//	}
	
	private class UpdateLayoutThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				tree.updateLayout(10);
			}
		}
		
	}
	
	public static void main(String[] args) {
		TreeVisualizer tv = new TreeVisualizer("test");
	}
}

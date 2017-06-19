package vmdv.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

import vmdv.communicate.Messenger;

public class VMDV {
	private Messenger messenger;
	private HashMap<String, Session> sessions = new HashMap<String, Session>();
	protected BlockingQueue<JSONObject> requests = new LinkedBlockingQueue<JSONObject>();
	
	public VMDV() {
		try {
			ServerSocket ss = new ServerSocket(3333);
			Socket s = ss.accept();
			
			BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter output = new PrintWriter(s.getOutputStream());
			this.messenger = new Messenger(input, output, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startMessenger() {
		messenger.startSendingReceiving();
	}
	
	public void addSession(String vid, Session session) {
		session.setVMDV(this);
		sessions.put(vid, session);
		session.getViewer().setVisible(true);
//		session.setVisible(true);
	}
	
	public void removeSession(String vid) {
//		sessions.get(vid).dispose();
		sessions.remove(vid);
	}
	
	public JSONObject takeRequestMsg() {
		try {
			return requests.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Session getSession(String sid) {
		return sessions.get(sid);
	}

	public static void main(String[] args) {
		VMDV vmdv = new VMDV();
//		Viewer treeViewer = new Viewer("Proof Tree");
//		GLEventHandler glh = new GLEventHandler();
//		treeViewer.registerGLHandler(glh);
//		KeyHandler kh = new KeyHandler();
//		treeViewer.registerKeyHandler(kh);
//		MouseHandler mh = new MouseHandler();
//		treeViewer.registerMouseHandler(mh);
//		MouseMotionHandler mmh = new MouseMotionHandler();
//		treeViewer.registerMouseMotionHandler(mmh);
//		MouseWheelHandler mwh = new MouseWheelHandler();
//		treeViewer.registerMouseWheelHandler(mwh);
//		Tree tree = new Tree();
////		treeViewer.setGraph(tree);
//		Session session = new Session("s1", tree, treeViewer, new ForceAtlas2Layout());
////		Messenger messenger = new Messenger(null, null);
//		vmdv.addSession("s1", session);
//		session.start();
//		
//		Viewer stateViewer = new Viewer("State Graph");
//		stateViewer.registerGLHandler(new GLEventHandler());
//		stateViewer.registerKeyHandler(new KeyHandler());
//		stateViewer.registerMouseHandler(new MouseHandler());
//		stateViewer.registerMouseMotionHandler(new MouseMotionHandler());
//		stateViewer.registerMouseWheelHandler(new MouseWheelHandler());
//		DiGraph graph = new DiGraph();
//		Session session2 = new Session("s2", graph, stateViewer, new ForceAtlas2Layout());
//		vmdv.addSession("s2", session2);
//		session2.start();
		
		vmdv.startMessenger();
		
	}

}

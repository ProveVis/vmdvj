package vmdv.communicate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

import vmdv.control.ForceAtlas2Layout;
import vmdv.control.Session;
import vmdv.control.VMDV;
import vmdv.dev.popup.ClearColorPopup;
import vmdv.dev.popup.HideAllLabelsPopup;
import vmdv.dev.popup.ResetEyePopup;
import vmdv.dev.popup.ShowAllLabelsPopup;
import vmdv.model.DiGraph;
import vmdv.model.Tree;
import vmdv.ui.GLEventHandler;
import vmdv.ui.KeyHandler;
import vmdv.ui.MouseHandler;
import vmdv.ui.MouseMotionHandler;
import vmdv.ui.MouseWheelHandler;
import vmdv.ui.Viewer;

public class Messenger {
	private BufferedReader input;
	private PrintWriter output;
	private VMDV vmdv;

	public Messenger(BufferedReader br, PrintWriter pw, VMDV vmdv) {
		this.input = br;
		this.output = pw;
		this.vmdv = vmdv;
	}

	public void startSendingReceiving() {
		new Thread(new SendingThread()).start();
		new Thread(new ReceivingThread()).start();
	}

	private class SendingThread implements Runnable {
		private boolean running = true;

		@Override
		public void run() {
			while (running) {
//				for (String sid : sessions.keySet()) {
//					JSONObject json = sessions.get(sid).takeRequestMsg();
//					if (json != null) {
//						output.println(json.toString());
//						output.flush();
//					}
//				}
				JSONObject json = vmdv.takeRequestMsg();
				if(json != null) {
					System.out.println("Sending: "+json.toString());
					output.println(json.toString());
					output.flush();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
					running = false;
				}
			}
		}

	}

	private class ReceivingThread implements Runnable {
		private boolean running = true;

		@Override
		public void run() {
			while (running) {
				// for (String sid : sessions.keySet()) {
				try {
					
					String str = input.readLine();
					System.out.println("JSON received: "+str+"---");
					JSONObject json = new JSONObject(str);
					switch(json.getString("type")) {
					case "create_session":
						String graphType = json.getString("graph_type");
						switch(graphType) {
						case "Tree":
							Viewer treeViewer = new Viewer(json.getString("session_descr"));
							
							treeViewer.addBackgroundPopup(new ClearColorPopup("Clear Color"));
							treeViewer.addBackgroundPopup(new ResetEyePopup("Reset Eye Position"));
							treeViewer.addBackgroundPopup(new ShowAllLabelsPopup("Show All Labels"));
							treeViewer.addBackgroundPopup(new HideAllLabelsPopup("Hide All Labels"));
							
							GLEventHandler glh = new GLEventHandler();
							treeViewer.registerGLHandler(glh);
							KeyHandler kh = new KeyHandler();
							treeViewer.registerKeyHandler(kh);
							MouseHandler mh = new MouseHandler();
							treeViewer.registerMouseHandler(mh);
							MouseMotionHandler mmh = new MouseMotionHandler();
							treeViewer.registerMouseMotionHandler(mmh);
							MouseWheelHandler mwh = new MouseWheelHandler();
							treeViewer.registerMouseWheelHandler(mwh);
							Tree tree = new Tree();
//							treeViewer.setGraph(tree);
							Session session = new Session(json.getString("session_id"), tree, treeViewer, new ForceAtlas2Layout());
//							Messenger messenger = new Messenger(null, null);
							vmdv.addSession(json.getString("session_id"), session);
							session.start();
							break;
						case "DiGraph":
							Viewer stateViewer = new Viewer(json.getString("session_descr"));
							stateViewer.addBackgroundPopup(new ClearColorPopup("Clear Color"));
							stateViewer.registerGLHandler(new GLEventHandler());
							stateViewer.registerKeyHandler(new KeyHandler());
							stateViewer.registerMouseHandler(new MouseHandler());
							stateViewer.registerMouseMotionHandler(new MouseMotionHandler());
							stateViewer.registerMouseWheelHandler(new MouseWheelHandler());
							DiGraph graph = new DiGraph();
							Session session2 = new Session(json.getString("session_id"), graph, stateViewer, new ForceAtlas2Layout());
							vmdv.addSession(json.getString("session_id"), session2);
							session2.start();
							break;
						default:
							System.out.println("Can not recognize graph type: "+graphType);
						}
						break;
					case "remove_session": {
							String sid = json.getString("session_id");
							vmdv.removeSession(sid);
							break;
						}
					default: {
							String sid = json.getString("session_id");
							System.out.println("Session "+sid+" parsing message.");
							Session session = vmdv.getSession(sid);
							session.parseResponseMsg(json);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// }
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

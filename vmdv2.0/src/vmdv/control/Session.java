package vmdv.control;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

import vmdv.communicate.RequestMsg;
import vmdv.dev.AssistAffect;
import vmdv.model.AbstractGraph;
import vmdv.ui.Viewer;

public class Session {
	private String sid;
	private Viewer viewer;
	private AbstractGraph graph;
	private GraphLayout layout;
	private BlockingQueue<JSONObject> requests = new LinkedBlockingQueue<JSONObject>();
	private VMDV vmdv;
//	private Queue<ResponseMsg> responses = new ConcurrentLinkedQueue<ResponseMsg>();

	public Session(String sid, AbstractGraph graph, Viewer viewer, GraphLayout layout) {
		this.sid = sid;
		this.graph = graph;
		this.viewer = viewer;
		this.viewer.setGraph(graph);
		this.viewer.setSession(this);
		this.layout = layout;
		this.viewer.setGraphLayout(layout);
//		new Thread(new LayoutThread()).start();
	}
	
	public void start() {
		viewer.setLocation(300, 300);
		viewer.setSize(1000, 1000);
		viewer.showView();
	}
	
	public void setVMDV(VMDV vmdv) {
		this.vmdv = vmdv;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Viewer getViewer() {
		return viewer;
	}

	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	public AbstractGraph getGraph() {
		return graph;
	}

//	private class LayoutThread implements Runnable {
//		private boolean running = true;
//
//		@Override
//		public void run() {
//			while (running) {
//				while (!responses.isEmpty()) {
//					ResponseMsg rmsg = responses.poll();
//					if (rmsg != null) {
//						AssistAffect affect = rmsg.parse();
//						if (affect != null) {
//							viewer.affect.addLast(affect);
//						}
//					}
//				}
//				layout.updateLayout(graph);
//			}
//		}
//
//	}

	public void parseResponseMsg(JSONObject json) {
//		if (rmsg != null) {
//			responses.add(rmsg);
//		}
		switch(json.getString("type")) {
		case "feedback":
			switch(json.getString("status")) {
			case "OK":
				System.out.println("feedback received: OK");
				break;
			case "Fail":
				System.out.println("feedback received: Fail \n"+(json.getString("error_msg")));
			default:
				System.out.println("unknown feedback received");
			}
		default:	
			AssistAffect affect = graph.parseJSON(json);
			if (affect != null) {
				System.out.println("Adding an affect in session "+this.sid);
				viewer.affect.addLast(affect);
			}
		}
		
	}



	public void addRequestMsg(RequestMsg rmsg) {
		if (rmsg != null) {
			JSONObject json = rmsg.to_json();
			json.remove("session_id");
			json.accumulate("session_id", this.sid);
			vmdv.requests.add(json);
		}
	}

}

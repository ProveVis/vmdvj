package vmdv.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import vmdv.paint.graph.Edge;
import vmdv.paint.graph.StateNode;
import vmdv.paint.graph.TreeNode;

public class NetAgent {
	private ServerSocket ss = null;	
	private Socket s = null;
	BufferedReader is = null;
	PrintWriter pw = null;
//	private HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct = new HashMap<TreeNode, ArrayList<TreeEdge>>();
	public ArrayList<TreeNode> newTreeNodes = new ArrayList<TreeNode>();
	public ArrayList<Edge> newTreeEdges = new ArrayList<Edge>();
	public ArrayList<StateNode> newStateNodes = new ArrayList<StateNode>();
	public ArrayList<Edge> newStateEdges = new ArrayList<Edge>();
	public ArrayList<String> highlightedState = new ArrayList<String>();
	public ArrayList<String> unHighlightedState = new ArrayList<String>();
	
	int uid = 0;
	
	public boolean added_tree = false;
	public boolean added_sg = false;
	public boolean added_high = false;
	public boolean added_unhi = false;
	
	private VisualizeAgent va;
	
	public NetAgent(VisualizeAgent va) {
		this.va = va;
		try {
			ss = new ServerSocket(3333);
			this.startListen();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startListen() throws IOException {
		s = ss.accept();
		System.out.println("accept one client.");
		is = new BufferedReader(new InputStreamReader(s.getInputStream()));
		pw = new PrintWriter(s.getOutputStream());
		new Thread(new ReceiveThread()).start();
	}
	
	public void sendMsg(String str) {
		pw.println(str);
		pw.flush();
	}
	
	private class ReceiveThread implements Runnable {
		private boolean flag = true;
		@Override
		public void run() {
			while (flag) {
				try {
					String str = is.readLine();
//					System.out.println("receiving: "+str);
					if (str.startsWith("proof_node:")) {
//						System.out.println("receive message: "+str);
//						while(added_tree) {
//							Thread.sleep(1);
//						}
						String state[] = str.substring(11, str.length()).split("-->");
//						TreeNode tn = new TreeNode(String.valueOf(uid++), state[0], state[1]);
//						tn.clearColor();
//						newTreeNodes.add(tn);
						va.addTreeNode(state[0], state[0], state[1]);
						added_tree = true;
					} else if(str.startsWith("proof_edge:")) {
//						while(added_tree) {
//							Thread.sleep(1);
//						}
						String edge[] = str.substring(11, str.length()).split("-->");
//						newTreeEdges.add(new Edge(edge[0], edge[1]));
						va.addTreeEdge(edge[0], edge[1]);
						added_tree = true;
					} else if(str.startsWith("state_node:")) {
//						while(added_sg) {
//							Thread.sleep(1);
//						}
						String state[] = str.substring(11, str.length()).split("-->");
//						newStateNodes.add(new StateNode(state[0], state[1]));
						boolean start = false;
						if (state[0].equals("1")) {
							start = true;
						}
						va.addState(state[0], state[1], start);
						added_sg = true;
					} else if(str.startsWith("state_edge:")) {
//						while(added_sg) {
//							Thread.sleep(1);
//						}
						String edge[] = str.substring(11, str.length()).split("-->");
//						System.out.println("receiving new edge: "+str.substring(11, str.length()));
//						newStateEdges.add(new Edge(edge[0], edge[1]));
						va.addEdge(edge[0], edge[1]);
						added_sg = true;
					} else if(str.startsWith("high_state:")) {
						while(added_high) {
							Thread.sleep(1);
						}
						String edge = str.substring(11, str.length());
						if(!edge.equals("Nothing")) {
							highlightedState.add(edge);
						}
						added_high = true;
					} else if(str.startsWith("unhi_state:")) {
						while(added_unhi) {
							Thread.sleep(1);
						}
						String edge = str.substring(11, str.length());
						if(!edge.equals("Nothing")) {
							unHighlightedState.add(edge);
						}
						added_unhi = true;
					} else if(str.equals("end_adding")) {
						va.endAddings();
						System.out.println("Done receiving new nodes and edges.");
					}
					
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					flag = false;
				}
				
			}
		}
		
	}
	
}

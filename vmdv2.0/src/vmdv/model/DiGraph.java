package vmdv.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import vmdv.config.ColorConfig;
import vmdv.config.GraphConfig.GraphType;
import vmdv.dev.AssistAffect;
import vmdv.dev.affects.AddEdgeAffect;
import vmdv.dev.affects.AddNodeAffect;
import vmdv.dev.affects.ClearColorAffect;
import vmdv.dev.affects.HighlightNodeAffect;
import vmdv.dev.affects.RemoveEdgeAffect;
import vmdv.dev.affects.RemoveNodeAffect;
import vmdv.dev.affects.UnHighlightNodeAffect;

public class DiGraph extends AbstractGraph {
	private HashMap<DiNode, DiEdges> struct = new HashMap<DiNode, DiEdges>();
	private DiNode start = null;
	private Random random = new Random();
	
	@Override
	public AbstractNode getNode(String id) {
		if(id == null) {
			return null;
		}
		for(DiNode dn: struct.keySet()) {
			if(id.equals(dn.id)) {
				return dn;
			}
		}
		return null;
	}

	@Override
	public void addNode(String id, String label) {
		if(getNode(id) != null) {
			return;
		}
		DiNode dn = new DiNode(id, label);
		DiEdges des = new DiEdges();
		struct.put(dn, des);
		if(start == null) {
			start = dn;
		}
		dn.oriColor = ColorConfig.oriColor;
		dn.setXYZ(random.nextDouble(), random.nextDouble(), random.nextDouble());
		
	}
	
	private void removeNode(DiNode dn) {
		DiEdges des = struct.get(dn);
		for(DiEdge de: des.pres) {
			DiNode from = de.from;
			struct.get(from).posts.remove(de);
			if(isIsolated(from)) {
				removeNode(from);
			}
		}
		for(DiEdge de: des.posts) {
			DiNode to = de.to;
			struct.get(to).pres.remove(de);
			if(isIsolated(to)) {
				removeNode(to);
			}
		}
	}

	@Override
	public void removeNode(String id) {
		DiNode dn = (DiNode) getNode(id);
		if(dn == null) {
			return;
		} else {
			removeNode(dn);
		}
	}

	@Override
	public void addEdge(String fromId, String toId) {
		DiNode from = (DiNode) getNode(fromId);
		DiNode to = (DiNode) getNode(toId);
		assert(from != null && to != null);
		DiEdge de = new DiEdge(from, to);
		struct.get(from).posts.add(de);
		struct.get(to).pres.add(de);
	}

	@Override
	public void removeEdge(String fromId, String toId) {
		DiNode from = (DiNode) getNode(fromId);
		DiNode to = (DiNode) getNode(toId);
		assert(from != null && to != null);
		struct.get(from).removePostTo(to);
		struct.get(to).removePreFrom(from);
		if(isIsolated(from)) {
			removeNode(from);
		}
		if(isIsolated(to)) {
			removeNode(to);
		}
	}

	@Override
	public AbstractNode getNearestNode(double x, double y, double z) {
		double dist = 0.25;
		DiNode rn = null;

		for (DiNode n : struct.keySet()) {
			double tmp_dist = Math.sqrt(
					Math.pow(n.xyz.getX() - x, 2) + Math.pow(n.xyz.getY() - y, 2) + Math.pow(n.xyz.getZ() - z, 2));
			if (tmp_dist <= dist) {
				rn = n;
				break;
			}
		}

		return rn;
	}

	@Override
	public void clearColor() {
		for(DiNode dn: struct.keySet()) {
			dn.clearColor();
		}
	}
	
	private boolean isIsolated(DiNode dn) {
		DiEdges des = struct.get(dn);
		return des.pres.size() == 0 && des.posts.size() == 0;
	}

	@Override
	public void render(GL2 gl, GLUT glut, TextRenderer tr) {
		int drawedNodes = 0;
		for (DiNode sn : getDiNodes()) {
			if (!sn.visible) {
				continue;
			}
			gl.glPushMatrix();
			gl.glTranslated(sn.xyz.getX(), sn.xyz.getY(), sn.xyz.getZ());
			RGBColor color = sn.color;
			gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
			glut.glutSolidSphere(sn.size, 10, 10);
			gl.glColor3f(0, 0, 0);
			drawedNodes++;
			// if(sn.isLableVisible()) {
			// tr.begin3DRendering();
			// tr.draw3D(sn.getLabel(), 0, 0, 0, 0.005f);
			// tr.flush();
			// tr.end3DRendering();
			// }
			// if(sn.isPicked()) {
			// tr.begin3DRendering();
			// tr.draw3D(sn.getLabel(), 0, 0, 0, 0.005f);
			// tr.flush();
			// tr.end3DRendering();
			// }
			// if(sn.isPicked()) {
			// tr.beginRendering(gld.getSurfaceWidth(),
			// gld.getSurfaceHeight());
			// Point p = this.getScreenPoint(gld, sn.getX(), sn.getY(),
			// sn.getZ());
			// if(p != null) {
			// tr.setColor(0, 0, 0, 1);
			// tr.draw(sn.getLabel(), p.x, p.y);
			// System.out.println("Showing label of sn: "+sn.getLabel()+" in
			// position: "+String.valueOf(p.x)+","+String.valueOf(p.y));
			// }
			// tr.endRendering();
			// }

			gl.glPopMatrix();
			for (DiEdge se : struct.get(sn).posts) {
				DiNode psn = se.to;
				if (!psn.visible) {
					continue;
				}
				gl.glPushMatrix();
				// RGBColor snc = new
				// RGBColor(178.0f/255,178.0f/255,178.0f/255);
				RGBColor snc = new RGBColor(0, 0, 0);
				if (psn.picked) {
					snc = sn.color;
					se.size = 1.0f;
				}
				gl.glColor3f(snc.getRed(), snc.getGreen(), snc.getBlue());
				gl.glLineWidth(se.size);
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3d(sn.xyz.getX(), sn.xyz.getY(), sn.xyz.getZ());
				gl.glVertex3d(psn.xyz.getX(), psn.xyz.getY(), psn.xyz.getZ());
				gl.glColor3f(0, 0, 0);

				double dx = psn.xyz.getX() - sn.xyz.getX();
				double dy = psn.xyz.getY() - sn.xyz.getY();
				double dz = psn.xyz.getZ() - sn.xyz.getZ();
				double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
				double x = sn.xyz.getX() + dx * ((d - 0.2) / d);
				double y = sn.xyz.getY() + dy * ((d - 0.2) / d);
				double z = sn.xyz.getZ() + dz * ((d - 0.2) / d);
				// gl.glTranslated(x, y, z);
				// gl.glColor3f(0, 1, 1);
				// glut.glutSolidSphere(0.03, 10, 10);
				// gl.glColor3f(1, 1, 1);
				double x1 = sn.xyz.getX() + dx * ((d - 0.3) / d);
				double y1 = sn.xyz.getY() + dy * ((d - 0.3) / d);
				double z1 = sn.xyz.getZ() + dz * ((d - 0.3) / d);

				// arrow
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

	}

	@Override
	public AbstractNode getStart() {
		return start;
	}

	@Override
	public Set<AbstractNode> getSuccessors(String id) {
		DiNode dn = (DiNode) getNode(id);
//		assert(dn != null);
		return getSuccessors(dn);
	}

	@Override
	public Set<AbstractNode> getPredecessors(String id) {
		DiNode dn = (DiNode) getNode(id);
		return getPredecessors(dn);
	}

	public Set<DiNode> getDiNodes() {
		return struct.keySet();
	}
	
	@Override
	public Set<AbstractNode> getNodes() {
		Set<AbstractNode> nodes = new HashSet<AbstractNode>();
		for(DiNode tn: getDiNodes()) {
			nodes.add(tn);
		}
		return nodes;
	}

	@Override
	public Set<AbstractNode> getSuccessors(AbstractNode an) {
		assert(an != null);
		Set<AbstractNode> succs = new HashSet<AbstractNode>();
		for(DiEdge de: struct.get(an).posts) {
			succs.add(de.to);
		}
		return succs;
	}

	@Override
	public Set<AbstractNode> getPredecessors(AbstractNode an) {
		assert(an != null);
		Set<AbstractNode> preds = new HashSet<AbstractNode>();
		for(DiEdge de: struct.get(an).pres) {
			preds.add(de.from);
		}
		return preds;
	}

	@Override
	public GraphType getType() {
		return GraphType.DIGRAPH;
	}

	@Override
	public AssistAffect parseJSON(JSONObject json) {
		switch (json.getString("type")) {
		case "add_node": {
			JSONObject json_node = json.getJSONObject("node");
			return new AddNodeAffect(json_node.getString("id"), json_node.getString("label"), null);
//			break;
		}
		case "remove_node": 
			return new RemoveNodeAffect(json.getString("node_id"));
		case "add_edge": {
			return new AddEdgeAffect(json.getString("from_id"), json.getString("to_id"), null);
		}
		case "remove_edge":
			return new RemoveEdgeAffect(json.getString("from_id"), json.getString("to_id"));
		case "highlight_node": {
			return new HighlightNodeAffect(getNode(json.getString("node_id")));
		}
		case "unhighlight_node":
			return new UnHighlightNodeAffect(getNode(json.getString("node_id")));
		case "clear_color":
			return new ClearColorAffect();
		default:
			System.out.println("Message type not known: "+json.getString("type"));
		}
		return null;
	
	}

}

class DiEdges {
	public Set<DiEdge> pres = new HashSet<DiEdge>();
	public Set<DiEdge> posts = new HashSet<DiEdge>();
	public void removePostTo(DiNode to) {
		for(DiEdge de:posts) {
			if(de.to.equals(to)) {
				posts.remove(de);
				return;
			}
		}
	}
	
	public void removePreFrom(DiNode from) {
		for(DiEdge de: pres) {
			if(de.from.equals(from)) {
				posts.remove(from);
				return;
			}
		}
	}
	
}

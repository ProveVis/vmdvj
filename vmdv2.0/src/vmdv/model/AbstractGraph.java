package vmdv.model;

import java.util.Set;

import org.json.JSONObject;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import vmdv.config.GraphConfig.GraphType;
import vmdv.dev.AssistAffect;

public abstract class AbstractGraph {
	public abstract GraphType getType();
	public abstract AbstractNode getStart();
	public abstract Set<AbstractNode> getSuccessors(String id);
	public abstract Set<AbstractNode> getSuccessors(AbstractNode an);
	public abstract Set<AbstractNode> getPredecessors(String id);
	public abstract Set<AbstractNode> getPredecessors(AbstractNode an);
	
	public abstract Set<AbstractNode> getNodes();
	public abstract AbstractNode getNode(String id);
	public abstract void addNode(String id, String label);
	public abstract void removeNode(String id);
	public abstract void addEdge(String fromId, String toId);
	public abstract void removeEdge(String fromId, String toId);
	public abstract AbstractNode getNearestNode(double x, double y, double z);
	public abstract void clearColor();
	public abstract void render(GL2 gl, GLUT glut, TextRenderer tr);
	public abstract AssistAffect parseJSON(JSONObject json);

}

package vmdv.paint.listener;

public interface StateGenerationListener {
	public void addState(String id, String state, boolean start);
	public void removeState(String id);
	public void addEdge(String from, String to);
	public void removeEdge(String from, String to);
	public void updateLayout();
}

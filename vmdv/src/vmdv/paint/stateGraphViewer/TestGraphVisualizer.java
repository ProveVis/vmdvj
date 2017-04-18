package vmdv.paint.stateGraphViewer;

public class TestGraphVisualizer {

	public static void main(String[] args) {
		StateGraphVisualizer sgv = new StateGraphVisualizer("test graph");
		sgv.addState("root", "root", true);
		sgv.addState("1", "1", false);
		sgv.addState("2", "2", false);
		sgv.addState("3", "3", false);
		sgv.addState("4", "4", false);
		sgv.addState("5", "5", false);
		sgv.addState("6", "6", false);
		sgv.addState("7", "7", false);
		sgv.addEdge("root", "1");
		sgv.addEdge("root", "2");
		sgv.addEdge("2", "3");
		sgv.addEdge("2", "4");
		sgv.addEdge("3", "5");
		sgv.addEdge("3", "6");
		sgv.addEdge("6", "7");
		sgv.show();
		sgv.updateLayout();
	}
}

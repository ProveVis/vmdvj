package vmdv.paint.listener;

public interface TreeVisualOperateListener {
	public void hightLightState(String fmlId);
	public void unHightLightState(String fmlId);
	public void oneStepForward(String paintId, String fmlId);
	public void clearProofTree();
}

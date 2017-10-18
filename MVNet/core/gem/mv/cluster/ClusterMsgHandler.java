package gem.mv.cluster;

public interface ClusterMsgHandler {
	public boolean onClusterMsg(int fromServerId, Object obj);
}

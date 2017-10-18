package gem.mv.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;

import v.Destoryable;
import v.Initializable;
import v.common.helper.FileUtil;
import v.common.helper.StrUtil;

public class ZKER implements Initializable, Destoryable {
	public static final String DEF_ZK_SERVERS = "127.0.0.1:2181";
	public static final int DEF_SESSION_TIME = 3000;
	private String zkServers = DEF_ZK_SERVERS;
	private int sessionTime = DEF_SESSION_TIME;
	private ZkClient client;

	public ZKER() {
	}

	public String getZkServers() {
		return zkServers;
	}

	public void setZkServers(String zkServers) {
		this.zkServers = zkServers == null ? DEF_ZK_SERVERS : zkServers;
	}

	public int getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(int sessionTime) {
		this.sessionTime = sessionTime;
	}

	@Override
	public void init() {
		client = new ZkClient(zkServers, sessionTime, sessionTime << 1);
	}

	@Override
	public void destory() {
		if (client != null)
			client.close();
		client = null;
	}

	public void subscribeState(IZkStateListener listener) {
		client.subscribeStateChanges(listener);
	}

	public void subscribeData(String parent, String key, IZkDataListener listener) {
		parent = getPath(parent);
		client.subscribeDataChanges(buildPath(parent, key), listener);
	}

	public void subscribeData(String path, IZkDataListener listener) {
		client.subscribeDataChanges(getPath(path), listener);
	}

	public void subscribeChild(String parent, String key, IZkChildListener listener) {
		parent = getPath(parent);
		client.subscribeChildChanges(buildPath(parent, key), listener);
	}

	public void subscribeChild(String path, IZkChildListener listener) {
		client.subscribeChildChanges(getPath(path), listener);
	}

	public String get(String path) {
		return client.readData(getPath(path), true);
	}

	public String get(String parent, String key) {
		parent = getPath(parent);
		return client.readData(buildPath(parent, key), true);
	}

	public Map<String, String> gets(String parent, Collection<String> keys) {
		parent = getPath(parent);
		Map<String, String> map = new HashMap<>();
		for (String e : keys) {
			String value = client.readData(buildPath(parent, e), true);
			if (value != null)
				map.put(e, value);
		}
		return map;
	}

	public Map<String, String> gets(Collection<String> paths) {
		Map<String, String> map = new HashMap<>();
		for (String e : paths) {
			String value = client.readData(getPath(e), true);
			if (value != null)
				map.put(e, value);
		}
		return map;
	}

	public Map<String, String> getChildren(String parent, String key) {
		return getChildren(buildPath(getPath(parent), key));
	}

	public Map<String, String> getChildren(String path) {
		path = getPath(path);
		if (!client.exists(path))
			return Collections.emptyMap();
		List<String> list = client.getChildren(path);
		if (list == null || list.isEmpty())
			return Collections.emptyMap();
		return gets(path, list);
	}

	public void set(String parent, String key, String value) {
		parent = getPath(parent);
		String path = buildPath(parent, key);
		if (client.exists(path))
			client.writeData(path, value);
		else {
			createPath(parent);
			client.createPersistent(path, value);
		}
	}

	public void set(String path, String value) {
		path = getPath(path);
		if (client.exists(path))
			client.writeData(path, value);
		else {
			checkParent(path);
			client.createPersistent(path, value);
		}
	}

	public void setTemp(String parent, String key, String value) {
		parent = getPath(parent);
		String path = buildPath(parent, key);
		try {
			if (client.exists(path))
				client.writeData(path, value);
			else {
				createPath(parent);
				client.createEphemeral(path, value);
			}
		} finally {

		}
	}

	public void setTemp(String path, String value) {
		path = getPath(path);
		if (client.exists(path))
			client.writeData(path, value);
		else {
			checkParent(path);
			client.createEphemeral(path, value);
		}
	}

	public boolean setLock(String parent, String key, String value) {
		parent = getPath(parent);
		String path = buildPath(parent, key);
		if (client.exists(path)) {
			String v = client.readData(path);
			return value.equals(v);
		}
		try {
			createPath(parent);
			client.createEphemeral(path, value);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public boolean setLock(String path, String value) {
		path = getPath(path);
		if (client.exists(path))
			return false;
		try {
			checkParent(path);
			client.createEphemeral(path, value);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	private void checkParent(String path) {
		createPath(FileUtil.getParent(path));
	}

	private void createPath(String path) {
		if (path != null && !client.exists(path))
			client.createPersistent(path, true);
	}

	private static final String buildPath(String parent, String key) {
		int klen = key.length();
		StringBuilder sb = new StringBuilder(parent.length() + klen + 1);
		sb.append(parent);
		if (key.charAt(0) != '/')
			sb.append('/');
		if (key.charAt(klen - 1) == '/')
			sb.append(key, 0, klen - 1);
		else
			sb.append(key);
		return StrUtil.sbToString(sb);
	}

	private static final String getPath(String path) {
		int len = path.length();
		if (len == 0)
			return "/";
		boolean b0 = path.charAt(0) == '/';
		boolean b1 = path.charAt(len - 1) != '/';
		if (b0 && b1)
			return path;
		if (!b0)
			++len;
		if (b1)
			--len;
		StringBuilder sb = new StringBuilder(len);
		if (!b0)
			sb.append('/');
		if (b1)
			sb.append(path);
		else
			StrUtil.appendValue(sb, path, 0, len - 1);
		return StrUtil.sbToString(sb);
	}

}

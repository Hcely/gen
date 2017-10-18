package test;

import gem.mv.DefMVFramewokerBuilder;
import gem.mv.MVFramework;
import gem.mv.bean.RouteInfo;
import gem.mv.plugin.route.RedisRouteTablePlugin;
import gem.mv.plugin.zkcluster.ZKClusterConfigPlugin;

public class Test01 {

	public static void main(String[] args) throws InterruptedException {
		DefMVFramewokerBuilder builder = new DefMVFramewokerBuilder();
		builder.addPlugin(ZKClusterConfigPlugin.class);
		builder.addPlugin(RedisRouteTablePlugin.class);
		builder.setServerId((int) (System.currentTimeMillis() % 1000000));
		MVFramework framework = builder.build();
		framework.init();
		RedisRouteTablePlugin plugin = framework.getPlugin(RedisRouteTablePlugin.class);
		plugin.set(new RouteInfo("1234", "aa", System.currentTimeMillis(), 1, "aaaaaadd"));
		Thread.sleep(100000);
	}

}

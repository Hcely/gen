package test;

import gem.mv.DefMVFramewokerBuilder;
import gem.mv.MVFramework;
import gem.mv.plugin.zkcluster.ZKClusterConfigPlugin;
import gem.mv.util.MVUtil;

public class Test01 {

	public static void main(String[] args) throws InterruptedException {
		System.out.println(MVUtil.getRandomClientId());
		System.out.println(MVUtil.getRandomServiceId());
	}

}

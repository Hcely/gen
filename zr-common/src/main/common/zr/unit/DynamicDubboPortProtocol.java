package zr.unit;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.ProtocolConfig;

public class DynamicDubboPortProtocol extends ProtocolConfig {
	private static final long serialVersionUID = 1L;

	public DynamicDubboPortProtocol() {
		this.setName("dubbo");
		this.setPort(NetUtils.getAvailablePort());
	}

}

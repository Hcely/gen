package zr.spring.bean;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.ProtocolConfig;

@Component
public class DynamicDubboPortProtocol extends ProtocolConfig {
	private static final long serialVersionUID = 1L;

	public DynamicDubboPortProtocol() {
		this.setName("dubbo");
		this.setPort(NetUtils.getAvailablePort());
	}

}

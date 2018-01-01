package zr.util;

import java.util.HashMap;
import java.util.Map;

import v.common.helper.NumberHelper;
import v.common.helper.StrUtil;
import v.server.helper.NetUtil;

public class StrIdUtil {
	private static final char[] CHAR_64 = NumberHelper.CHAR_64;
	private static final char[] CHARS = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
			'0' };
	private static final int MACHINE_CODE = NetUtil.getMachineCode() & ((1 << 10) - 1);
	private static final int MASK = (1 << 15) - 1;
	private static final Map<String, IdFactory> factoryMap = new HashMap<>();

	public static final String getId(String idType) {
		return getFactory(idType).nextId();
	}

	public static final long getTime(String id) {
		long time = NumberHelper.parse32Str(id, 2, 11);
		time >>>= 10;
		return time;
	}

	public static final boolean checkType(String idType, String id) {
		IdFactory f = getFactory(idType);
		return f.checkType(id);
	}

	private static final IdFactory getFactory(String idType) {
		IdFactory f = factoryMap.get(idType);
		if (f == null)
			synchronized (factoryMap) {
				if ((f = factoryMap.get(idType)) == null)
					factoryMap.put(idType, f = new IdFactory(idType));
			}
		return f;
	}

	private static final class IdFactory {
		protected final char[] idType;
		protected final char[] charArray;
		protected long lastTime;
		protected int incNum;

		public IdFactory(String idType) {
			this.idType = new char[] { '0', '0' };
			this.charArray = CHARS.clone();
			this.lastTime = 0;
			this.incNum = 0;
			if (idType.length() > 1) {
				this.idType[0] = charArray[0] = idType.charAt(0);
				this.idType[1] = charArray[1] = idType.charAt(1);
			} else if (idType.length() > 0)
				this.idType[0] = charArray[0] = idType.charAt(0);
		}

		public final boolean checkType(String id) {
			if (idType[0] == id.charAt(0) && idType[1] == id.charAt(1))
				return true;
			return false;
		}

		public final String nextId() {
			long time = System.currentTimeMillis();
			int num = 0;
			synchronized (this) {
				if (lastTime > time)
					throw new RuntimeException(String.format(
							"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTime - time));
				else if (lastTime == time) {
					num = (++incNum) & MASK;
					if (num == 0)
						time = nextTime(time);
				} else
					incNum = 0;
				lastTime = time;
			}
			time <<= 10;
			time |= MACHINE_CODE;
			char[] ch = charArray.clone();
			for (int i = 13; i > 2;) {
				ch[--i] = CHAR_64[(int) (time & 31)];
				time >>>= 5;
			}
			ch[13] = CHAR_64[(num >>> 10) & 31];
			ch[14] = CHAR_64[(num >>> 5) & 31];
			ch[15] = CHAR_64[num & 31];
			return StrUtil.newStr(ch);
		}

		private static final long nextTime(final long time) {
			do {
				long curTime = System.currentTimeMillis();
				if (curTime > time)
					return curTime;
				Thread.yield();
			} while (true);
		}
	}

}

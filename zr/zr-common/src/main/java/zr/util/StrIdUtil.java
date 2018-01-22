package zr.util;

import java.util.HashMap;
import java.util.Map;

import v.common.helper.NumberHelper;
import v.common.helper.StrUtil;
import v.server.helper.NetUtil;

public class StrIdUtil {
	private static final char[] CHARS = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
			'0' };
	private static final int WORKER_MASK = (1 << 10) - 1;
	private static final int WORKER_ID = NetUtil.getMachineCode() & WORKER_MASK;
	private static final int NUM_MASK = (1 << 15) - 1;
	private static final Map<String, IdFactory> factoryMap = new HashMap<>();

	public static final String nextId(String idType) {
		return getFactory(idType).nextId();
	}

	public static final String maxId(String idType, long time) {
		return getFactory(idType).maxId(time);
	}

	public static final String mixId(String idType, long time) {
		return getFactory(idType).minId(time);
	}

	public static final long getTime(String id) {
		long time = Long.parseLong(id.substring(2, 13), 32);
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
					num = (++incNum) & NUM_MASK;
					if (num == 0)
						time = nextTime(time);
				} else
					incNum = 0;
				lastTime = time;
			}
			return getId(charArray, time, WORKER_ID, num);
		}

		public final String maxId(long time) {
			return getId(charArray, time, WORKER_MASK, NUM_MASK);
		}

		public final String minId(long time) {
			return getId(charArray, time, 0, 0);
		}
	}

	private static final String getId(char[] charArray, long time, int machineCode, int num) {
		time <<= 10;
		time |= machineCode;
		char[] ch = charArray.clone();
		char[] char64 = NumberHelper.CHAR_64;
		for (int i = 13; i > 2;) {
			ch[--i] = char64[(int) (time & 31)];
			time >>>= 5;
		}
		ch[13] = char64[(num >>> 10) & 31];
		ch[14] = char64[(num >>> 5) & 31];
		ch[15] = char64[num & 31];
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

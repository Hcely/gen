package zr;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppContext {
	protected static boolean debug = false;

	public static boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		AppContext.debug = debug;
	}

	public final static class lv {
		private lv() {
		}

		public static final Level _0 = Level.forName("LV0", 301);
		public static final Level _1 = Level.forName("LV1", 302);
		public static final Level _2 = Level.forName("LV2", 303);
		public static final Level _3 = Level.forName("LV3", 304);
		public static final Level _4 = Level.forName("LV4", 305);
		public static final Level _5 = Level.forName("LV5", 306);
		public static final Level _6 = Level.forName("LV6", 307);
		public static final Level _7 = Level.forName("LV7", 308);
		public static final Level _8 = Level.forName("LV8", 309);
		public static final Level _9 = Level.forName("LV9", 310);

	}

	public static final Logger logger = LogManager.getLogger(AppContext.class);

}

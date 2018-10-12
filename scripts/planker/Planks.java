package scripts.planker;

public class Planks {

	public final static int MASTER_PLANK_INTERFACE = 403;

	public enum PLANK {

		WOOD(MASTER_PLANK_INTERFACE, 93, 100, "Logs", 1511, "Plank", 960),

		OAK(MASTER_PLANK_INTERFACE, 94, 250, "Oak logs", 1521, "Oak plank", 8778),

		TEAK(MASTER_PLANK_INTERFACE, 95, 500, "Teak logs", 6333, "Teak plank", 8780),

		MAHOGANY(MASTER_PLANK_INTERFACE, 96, 1500, "Mahogany logs", 6332, "Mahogany plank", 8782);

		private final int MASTER_INTERFACE;
		private final int PLANK_INTERFACE;
		private final int COST_PER_LOG;
		private final String LOG_NAME;
		private final int LOG_ID;
		private final String PLANK_NAME;
		private final int PLANK_ID;

		PLANK(int MASTER_INTERFACE, int PLANK_INTERFACE, int COST_PER_LOG, String LOG_NAME, int LOG_ID,
				String PLANK_NAME, int PLANK_ID) {
			this.MASTER_INTERFACE = MASTER_INTERFACE;
			this.PLANK_INTERFACE = PLANK_INTERFACE;
			this.COST_PER_LOG = COST_PER_LOG;
			this.LOG_NAME = LOG_NAME;
			this.LOG_ID = LOG_ID;
			this.PLANK_NAME = PLANK_NAME;
			this.PLANK_ID = PLANK_ID;
		}

		public int getMaster() {
			return this.MASTER_INTERFACE;
		}

		public int getInterface() {
			return this.PLANK_INTERFACE;
		}

		public int getCostPerLog() {
			return this.COST_PER_LOG;
		}

		public String getLogName() {
			return this.LOG_NAME;
		}

		public int getLogID() {
			return this.LOG_ID;
		}

		public String getPlankName() {
			return this.PLANK_NAME;
		}

		public int getPlankID() {
			return this.PLANK_ID;
		}

	}

}

package scripts.usa.api2007.enums;

import org.apache.commons.lang3.ArrayUtils;

public enum Rocks {

	CLAY(new int[] { 7454, 7487 }, new int[] { 7454, 7487 });

	private final int[] valid;
	private final int[] empty;

	Rocks(int[] valid, int[] empty) {
		this.valid = valid;
		this.empty = empty;
	}

	public int[] getID(RockStatus rockStatus) {
		switch (rockStatus) {
			case VALID:
				return this.valid;
			case EMPTY:
				return this.empty;
			case EITHER:
				return ArrayUtils.addAll(this.valid, this.empty);
		}
		return new int[0];
	}

	public enum RockStatus {
		VALID,
		EMPTY,
		EITHER;
	}
}

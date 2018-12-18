package scripts.agility.data.courses.rooftops;

import org.tribot.api2007.types.RSTile;

public enum Obstacle {

	/**
	 * DRAYNOR ROOFTOP
	 */
	DRAYNOR_ROUGH_WALL("Climb", "Rough wall", new RSTile(3103, 3279, 0), new RSTile(3102, 3279, 3), 90),

	DRAYNOR_TIGHTROPE_1("Cross", "Tightrope", new RSTile(3099, 3277, 3), new RSTile(3090, 3276, 3), 180),

	DRAYNOR_TIGHTROPE_2("Cross", "Tightrope", new RSTile(3091, 3276, 3), new RSTile(3092, 3266, 3), 180),

	DRAYNOR_NARROW_WALL("Balance", "Narrow wall", new RSTile(3089, 3265, 3), new RSTile(3088, 3261, 3), 180),

	DRAYNOR_WALL("Jump-up", "Wall", new RSTile(3088, 3257, 3), new RSTile(3088, 3254, 3), 300),

	DRAYNOR_GAP("Jump", "Gap", new RSTile(3094, 3255, 3), new RSTile(3096, 3256, 3), 300),

	DRAYNOR_CRATE("Climb-down", "Crate", new RSTile(3101, 3261, 3), new RSTile(3103, 3261, 0), 0),

	/**
	 * AL-KHARID ROOFTOP
	 */
	AL_KHARID_ROUGH_WALL("Climb", "Rough wall", new RSTile(3273, 3195, 0), new RSTile(3273, 3192, 3), 180),

	AL_KHARID_TIGHTROPE_1("Cross", "Tightrope", new RSTile(3272, 3182, 3), new RSTile(3272, 3173, 3), 180),

	AL_KHARID_CABLE("Swing-across", "Cable", new RSTile(3269, 3168, 3), new RSTile(3284, 3166, 3), 270),

	AL_KHARID_ZIP_LINE("Teeth-grip", "Zip Line", new RSTile(3301, 3163, 3), new RSTile(3315, 3163, 340)),

	AL_KHARID_TROPICAL_TREE("Swing-across", "Tropical tree", new RSTile(3318, 3165, 1), new RSTile(3317, 3174, 2), 340),

	AL_KHARID_ROOF_TOP_BEAMS("Climb", "Roof top beams", new RSTile(3316, 3179, 2), new RSTile(3316, 3181, 3), 50),

	AL_KHARID_TIGHTROPE_2("Cross", "Tightrope", new RSTile(3314, 3186, 3), new RSTile(3302, 3188, 3), 50),

	AL_KHARID_GAP("Jump", "Gap", new RSTile(3300, 3192, 3), new RSTile(3299, 3194, 0), 180),

	/**
	 * VARROCK ROOFTOP
	 */
	VARROCK_ROUGH_WALL("Climb", "Rough wall", new RSTile(3222, 3414, 0), new RSTile(3218, 3414, 3), 90),

	VARROCK_CLOTHES_LINE("Cross", "Clothes line", new RSTile(3214, 3414, 3), new RSTile(3208, 3414, 3), 90),

	VARROCK_GAP_1("Leap", "Gap", new RSTile(3202, 3416, 3), new RSTile(3197, 3416, 1), 90),

	VARROCK_WALL("Balance", "Wall", new RSTile(3194, 3416, 1), new RSTile(3190, 3407, 1), 180),

	VARROCK_GAP_2("Leap", "Gap", new RSTile(3193, 3402, 3), new RSTile(3193, 3398, 3), 270),

	VARROCK_GAP_3("Leap", "Gap", new RSTile(3208, 3397, 3), new RSTile(3218, 3399, 3), 270),

	VARROCK_GAP_4("Leap", "Gap", new RSTile(3232, 3402, 3), new RSTile(3236, 3403, 3), 330),

	VARROCK_LEDGE("Hurdle", "Ledge", new RSTile(3236, 3407, 3), new RSTile(3236, 3411, 3), 330),

	VARROCK_EDGE("Jump-off", "Edge", new RSTile(3236, 3415, 3), new RSTile(3236, 3418, 0), 90),

	/**
	 * FALADOR ROOFTOP
	 */
	FALADOR_WALL("Climb", "Rough wall", new RSTile(3036, 3341, 0), new RSTile(3036, 3343, 3), 330),

	FALADOR_TIGHTROPE_1("Cross", "Tightrope", new RSTile(3039, 3343, 3), new RSTile(3047, 3344, 3), 330),

	FALADOR_HAND_HOLDS("Cross", "Hand holds", new RSTile(3050, 3349, 3), new RSTile(3050, 3357, 3), 30),

	FALADOR_GAP_1("Jump", "Gap", new RSTile(3048, 3358, 3), new RSTile(3048, 3361, 3), 90),

	FALADOR_GAP_2("Jump", "Gap", new RSTile(3045, 3361, 3), new RSTile(3041, 3361, 3), 90),

	FALADOR_TIGHTROPE_2("Cross", "Tightrope", new RSTile(3035, 3361, 3), new RSTile(3028, 3354, 3), 90),

	FALADOR_TIGHTROPE_3("Cross", "Tightrope", new RSTile(3027, 3353, 3), new RSTile(3020, 3353, 3), 180),

	FALADOR_GAP_3("Jump", "Gap", new RSTile(3018, 3353, 3), new RSTile(3018, 3349, 3), 180),

	FALADOR_LEDGE_1("Jump", "Ledge", new RSTile(3016, 3346, 3), new RSTile(3014, 3346, 3), 180),

	FALADOR_LEDGE_2("Jump", "Ledge", new RSTile(3013, 3344, 3), new RSTile(3013, 3341, 3), 180),

	FALADOR_LEDGE_3("Jump", "Ledge", new RSTile(3013, 3335, 3), new RSTile(3013, 3333, 3), 270),

	FALADOR_LEDGE_4("Jump", "Ledge", new RSTile(3016, 3332, 3), new RSTile(3020, 3333, 3), 270),

	FALADOR_EDGE("Jump", "Edge", new RSTile(3024, 3334, 3), new RSTile(3029, 3333, 0), 330),

	/**
	 * CANIFIS ROOFTOP
	 */
	CANIFIS_TREE("Climb", "Tall tree", new RSTile(3508, 3488, 0), new RSTile(3506, 3492, 2), 60),

	CANIFIS_GAP_1("Jump", "Gap", new RSTile(3505, 3497, 2), new RSTile(3502, 3504, 2), 90),

	CANIFIS_GAP_2("Jump", "Gap", new RSTile(3498, 3504, 2), new RSTile(3491, 3504, 2), 90),

	CANIFIS_GAP_3("Jump", "Gap", new RSTile(3487, 3499, 2), new RSTile(3479, 3499, 3), 180),

	CANIFIS_GAP_4("Jump", "Gap", new RSTile(3478, 3493, 3), new RSTile(3478, 3486, 2), 270),

	CANIFIS_POLE_VAULT("Vault", "Pole-vault", new RSTile(3480, 3484, 2), new RSTile(3487, 3476, 3), 270),

	CANIFIS_GAP_5("Jump", "Gap", new RSTile(3502, 3476, 3), new RSTile(3510, 3476, 2), 330),

	CANIFIS_GAP_6("Jump", "Gap", new RSTile(3510, 3482, 2), new RSTile(3510, 3485, 0), 330),

	/**
	 * SEERS ROOFTOP
	 */
	SEERS_WALL("Climb-up", "Wall", new RSTile(2729, 3487, 0), new RSTile(2729, 3491, 3), 80),

	SEERS_GAP_1("Jump", "Gap", new RSTile(2723, 3493, 3), new RSTile(2713, 3494, 2), 80),

	SEERS_TIGHTROPE("Cross", "Tightrope", new RSTile(2710, 3490, 2), new RSTile(2710, 3480, 2), 140),

	SEERS_GAP_2("Jump", "Gap", new RSTile(2710, 3477, 2), new RSTile(2710, 3472, 3), 140),

	SEERS_GAP_3("Jump", "Gap", new RSTile(2704, 3471, 3), new RSTile(2702, 3465, 2), 140),

	SEERS_EDGE("Jump", "Edge", new RSTile(2702, 3465, 2), new RSTile(2705, 3464, 0), 340);

	private final String action;
	private final String name;
	private final RSTile start;
	private final RSTile end;
	private final int nextRotation;

	Obstacle(String action, String name, RSTile start, RSTile end, int nextRotation) {
		this.action = action;
		this.name = name;
		this.start = start;
		this.end = end;
		this.nextRotation = nextRotation;
	}

	Obstacle(String action, String name, RSTile start, RSTile end) {
		this(action, name, start, end, -1);
	}

	public String getAction() {
		return this.action;
	}

	public String getName() {
		return this.name;
	}

	public RSTile getStartTile() {
		return this.start;
	}

	public RSTile getEndTile() {
		return this.end;
	}

	public int getNextRotation() {
		return this.nextRotation;
	}
}

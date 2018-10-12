package scripts.agility;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

public enum OBSTACLE {

	/*
	 * OBSTACLE(String action, String name, RSArea area, RSTile start, RSTile
	 * end, int angle, int rotation, boolean move)
	 * 
	 */

	/**
	 * ARDOUGNE ROOFTOP
	 */
	ARDOUGNE_WOODEN_BEAMS("Climb-up", "Wooden Beams", new RSArea(new RSTile(2668, 3295, 0), new RSTile(2675, 3298, 0)),
			new RSTile(2673, 3298, 0), new RSTile(2671, 3299, 3), 100, 330, false, false),

	ARDOUGNE_GAP_1("Jump", "Gap", new RSArea(new RSTile(2671, 3299, 3), new RSTile(2671, 3309, 3)),
			new RSTile(2671, 3309, 3), new RSTile(2665, 3318, 3), 40, 30, false, false),

	ARDOUGNE_PLANK("Walk-on", "Plank", new RSArea(new RSTile(2662, 3318, 3), new RSTile(2665, 3318, 3)),
			new RSTile(2662, 3318, 3), new RSTile(2656, 3318, 3), 40, 30, false, false),

	ARDOUGNE_GAP_2("Jump", "Gap", new RSArea(new RSTile(2654, 3318, 3), new RSTile(2656, 3318, 3)),
			new RSTile(2654, 3318, 3), new RSTile(2653, 3314, 3), 40, 90, false, false),

	ARDOUGNE_GAP_3("Jump", "Gap", new RSArea(new RSTile(2653, 3310, 3), new RSTile(2653, 3314, 3)),
			new RSTile(2653, 3310, 3), new RSTile(2651, 3309, 3), 40, 180, false, false),

	ARDOUGNE_STEEP_ROOF("Balance-across", "Steep roof",
			new RSArea(new RSTile(2651, 3300, 3), new RSTile(2653, 3309, 3)), new RSTile(2653, 3300, 3),
			new RSTile(2656, 3297, 3), 40, 180, false, true),

	ARDOUGNE_GAP_4("Jump", "Gap", new RSArea(new RSTile(2654, 3297, 3), new RSTile(2657, 3299, 3)),
			new RSTile(2657, 3297, 3), new RSTile(2668, 3297, 0), 100, 330, false, false),

	/**
	 * RELLEKKA ROOFTOP
	 */
	RELLEKKA_WALL("Climb", "Rough wall", new RSArea(new RSTile(2619, 3675, 0), new RSTile(2631, 3682, 0)),
			new RSTile(2625, 3678, 0), new RSTile(2625, 3676, 3), 100, 200, false, false),

	RELLEKKA_GAP_1("Leap", "Gap", new RSArea(new RSTile(2622, 3672, 3), new RSTile(2626, 3676, 3)),
			new RSTile(2622, 3672, 3), new RSTile(2622, 3668, 3), 100, 200, false, false),

	RELLEKKA_TIGHTROPE_1("Cross", "Tightrope", new RSArea(new RSTile(2615, 3658, 3), new RSTile(2622, 3668, 3)),
			new RSTile(2622, 3658, 3), new RSTile(2627, 3654, 3), 100, 200, true, false),

	RELLEKKA_GAP_2("Leap", "Gap", new RSArea(new RSTile(2626, 3651, 3), new RSTile(2630, 3655, 3)),
			new RSTile(2629, 3655, 3), new RSTile(2639, 3653, 3), 100, 330, false, false),

	RELLEKKA_GAP_3("Hurdle", "Gap", new RSArea(new RSTile(2639, 3649, 3), new RSTile(2644, 3653, 3)),
			new RSTile(2643, 3653, 3), new RSTile(2643, 3657, 3), 100, 330, false, false),

	RELLEKKA_TIGHTROPE_2("Cross", "Tightrope", new RSArea(new RSTile(2643, 3657, 3), new RSTile(2650, 3662, 3)),
			new RSTile(2647, 3662, 3), new RSTile(2655, 3670, 3), 100, 330, true, false),

	RELLEKKA_PILE_OF_FISH("Jump-in", "Pile of fish", new RSArea(new RSTile(2655, 3665, 3), new RSTile(2666, 3685, 3)),
			new RSTile(2655, 3676, 3), new RSTile(2652, 3676, 0), 70, 330, false, false),

	/**
	 * CANIFIS ROOFTOP
	 */
	CANIFIS_TREE("Climb", "Tall tree", new RSArea(new RSTile(3503, 3483, 0), new RSTile(3515, 3488, 0)),
			new RSTile(3508, 3488, 0), new RSTile(3506, 3492, 2), 100, 360, false, false),

	CANIFIS_GAP_1("Jump", "Gap", new RSArea(new RSTile(3504, 3488, 2), new RSTile(3509, 3498)),
			new RSTile(3505, 3497, 2), new RSTile(3502, 3504, 2), 65, 0, false, false),

	CANIFIS_GAP_2("Jump", "Gap", new RSArea(new RSTile(3497, 3504, 2), new RSTile(3503, 3506, 2)),
			new RSTile(3498, 3504, 2), new RSTile(3492, 3504, 2), 65, 90, false, false),

	CANIFIS_GAP_3("Jump", "Gap", new RSArea(new RSTile(3487, 3499, 2), new RSTile(3492, 3504, 2)),
			new RSTile(3487, 3499, 2), new RSTile(3479, 3499, 3), 65, 90, false, false),

	CANIFIS_GAP_4("Jump", "Gap", new RSArea(new RSTile(3475, 3492, 3), new RSTile(3479, 3499, 3)),
			new RSTile(3478, 3493, 3), new RSTile(3478, 3486, 2), 65, 180, false, false),

	CANIFIS_POLE_VAULT("Vault", "Pole-vault", new RSArea(new RSTile(3477, 3481, 2), new RSTile(3484, 3487, 2)),
			new RSTile(3480, 3484, 2), new RSTile(3489, 3476, 3), 80, 270, false, false),

	CANIFIS_GAP_5("Jump", "Gap", new RSArea(new RSTile(3486, 3469, 3), new RSTile(3503, 3478, 3)),
			new RSTile(3500, 3476, 3), new RSTile(3510, 3476, 2), 65, 270, true, false),

	CANIFIS_GAP_6("Jump", "Gap", new RSArea(new RSTile(3509, 3475, 2), new RSTile(3515, 3482, 2)),
			new RSTile(3510, 3482, 2), new RSTile(3510, 3485, 0), 65, 0, false, false),

	/**
	 * POLLNIVNEACH ROOFTOP
	 */
	POLLNIVNEACH_BASKET("Climb-on", "Basket", new RSArea(new RSTile(3348, 2959, 0), new RSTile(3356, 2964, 0)),
			new RSTile(3353, 2962, 0), new RSTile(3351, 2964, 1), 100, 40, false, false),

	POLLNIVNEACH_STALL("Jump-on", "Market stall", new RSArea(new RSTile(3346, 2964, 1), new RSTile(3351, 2968, 1)),
			new RSTile(3350, 2968, 1), new RSTile(3352, 2973, 1), 60, 40, false, false),

	POLLNIVNEACH_BANNER("Grab", "Banner", new RSArea(new RSTile(3352, 2973, 1), new RSTile(3355, 2976, 1)),
			new RSTile(3355, 2976, 1), new RSTile(3360, 2977, 1), 100, 0, false, false),

	POLLNIVNEACH_GAP("Leap", "Gap", new RSArea(new RSTile(3360, 2977, 1), new RSTile(3362, 2979, 1)),
			new RSTile(3362, 2977, 1), new RSTile(3366, 2976, 1), 100, 330, false, false),

	POLLNIVNEACH_TREE_1("Jump-to", "Tree", new RSArea(new RSTile(3366, 2974, 1), new RSTile(3369, 2976, 1)),
			new RSTile(3368, 2976, 1), new RSTile(3368, 2982, 1), 100, 330, false, false),

	POLLNIVNEACH_WALL("Climb", "Rough wall", new RSArea(new RSTile(3355, 2980, 1), new RSTile(3369, 2986, 1)),
			new RSTile(3365, 2982, 1), new RSTile(3365, 2983, 2), 100, 0, false, false),

	POLLNIVNEACH_BARS("Cross", "Monkeybars", new RSArea(new RSTile(3355, 2980, 2), new RSTile(3365, 2984, 2)),
			new RSTile(3361, 2984, 2), new RSTile(3358, 2991, 2), 40, 60, false, true),

	POLLNIVNEACH_TREE_2("Jump-on", "Tree", new RSArea(new RSTile(3357, 2991, 2), new RSTile(3370, 2995, 2)),
			new RSTile(3360, 2995, 2), new RSTile(3359, 3000, 2), 100, 30, false, false),

	POLLNIVNEACH_LINE("Jump-to", "Drying line", new RSArea(new RSTile(3356, 3000, 2), new RSTile(3362, 3004, 2)),
			new RSTile(3362, 3002, 2), new RSTile(3363, 2998, 0), 100, 30, false, false),

	/**
	 * GNOME STRONGHOLD
	 */
	GNOME_LOG_BALANCE("Walk-across", "Log balance", new RSArea(new RSTile(2469, 3434, 0), new RSTile(2490, 3440, 0)),
			new RSTile(2474, 3436, 0), new RSTile(2474, 3428, 0), 100, 180, true, false),

	GNOME_OBSTACLE_NET_1("Climb-over", "Obstacle net", new RSArea(new RSTile(2469, 3420, 0), new RSTile(2478, 3429, 0)),
			new RSTile(2474, 3426, 0), new RSTile(2473, 3423, 1), 85, 180, false, false),

	GNOME_TREE_BRANCH_1("Climb", "Tree branch", new RSArea(new RSTile(2471, 3422, 1), new RSTile(2476, 3424, 1)),
			new RSTile(2473, 3423, 1), new RSTile(2473, 3420, 2), 85, 270, false, false),

	GNOME_BALANCING_ROPE("Walk-on", "Balancing rope", new RSArea(new RSTile(2473, 3418, 2), new RSTile(2477, 3421, 2)),
			new RSTile(2477, 3420, 2), new RSTile(2483, 3420, 2), 85, 270, true, false),

	GNOME_TREE_BRANCH_2("Climb-down", "Tree branch", new RSArea(new RSTile(2482, 3418, 2), new RSTile(2488, 3421, 2)),
			new RSTile(2488, 3419, 2), new RSTile(2487, 3420, 0), 85, 0, false, false),

	GNOME_TREE_NET_2("Climb-over", "Obstacle net", new RSArea(new RSTile(2481, 3417, 0), new RSTile(2490, 3425, 0)),
			new RSTile(2485, 3425, 0), new RSTile(2484, 3428, 0), 85, 0, true, false),

	GNOME_TREE_TUNNEL("Squeeze-through", "Obstacle pipe",
			new RSArea(new RSTile(2481, 3427, 0), new RSTile(2490, 3431, 0)), new RSTile(2484, 3430, 0),
			new RSTile(2484, 3437, 0), 85, 0, false, false),

	/**
	 * DRAYNOR ROOFTOP
	 */
	DRAYNOR_ROUGH_WALL("Climb", "Rough wall", new RSArea(new RSTile(3102, 3256, 0), new RSTile(3109, 3285, 0)),
			new RSTile(3103, 3279, 0), new RSTile(3102, 3279, 3), 100, 90, true, false),

	DRAYNOR_TIGHTROPE_1("Cross", "Tightrope",
			new RSArea(new RSTile[] { new RSTile(3104, 3282, 3), new RSTile(3104, 3276, 3), new RSTile(3096, 3276, 3),
					new RSTile(3096, 3283, 3) }),
			new RSTile(3099, 3277, 3), new RSTile(3090, 3276, 3), 100, 90, false, false),

	DRAYNOR_TIGHTROPE_2("Cross", "Tightrope",
			new RSArea(new RSTile[] { new RSTile(3095, 3276, 3), new RSTile(3091, 3280, 3), new RSTile(3086, 3274, 3),
					new RSTile(3090, 3271, 3) }),
			new RSTile(3091, 3276, 3), new RSTile(3092, 3266, 3), 100, 90, false, false),

	DRAYNOR_NARROW_WALL("Balance", "Narrow wall",
			new RSArea(new RSTile[] { new RSTile(3087, 3264, 3), new RSTile(3095, 3264, 3), new RSTile(3095, 3270, 3),
					new RSTile(3087, 3269, 3) }),
			new RSTile(3089, 3265, 3), new RSTile(3088, 3261, 3), 100, 90, false, false),

	DRAYNOR_WALL("Jump-up", "Wall",
			new RSArea(new RSTile[] { new RSTile(3090, 3256, 3), new RSTile(3086, 3256, 3), new RSTile(3087, 3262, 3),
					new RSTile(3089, 3262, 3) }),
			new RSTile(3088, 3257, 3), new RSTile(3088, 3254, 3), 100, 180, true, false),

	DRAYNOR_GAP("Jump", "Gap",
			new RSArea(new RSTile[] { new RSTile(3086, 3256, 3), new RSTile(3086, 3253, 3), new RSTile(3091, 3253, 3),
					new RSTile(3095, 3253, 3), new RSTile(3095, 3256, 3) }),
			new RSTile(3094, 3255, 3), new RSTile(3096, 3256, 3), 100, 330, false, false),

	DRAYNOR_CRATE("Climb-down", "Crate",
			new RSArea(new RSTile[] { new RSTile(3095, 3262, 3), new RSTile(3095, 3255, 3), new RSTile(3103, 3255, 3),
					new RSTile(3103, 3262, 3) }),
			new RSTile(3101, 3261, 3), new RSTile(3103, 3261, 0), 100, 330, false, false),

	/**
	 * AL-KHARID ROOFTOP
	 */
	AL_KHARID_ROUGH_WALL("Climb", "Rough wall", new RSArea(new RSTile(3265, 3187, 0), new RSTile(3286, 3200, 0)),
			new RSTile(3273, 3196, 0), new RSTile(3273, 3192, 3), 100, 210, true, false),

	AL_KHARID_TIGHTROPE_1("Cross", "Tightrope", new RSArea(new RSTile(3271, 3180, 3), new RSTile(3278, 3192, 0)),
			new RSTile(3272, 3182, 3), new RSTile(3272, 3173, 3), 100, 210, true, false),

	AL_KHARID_CABLE("Swing-across", "Cable", new RSArea(new RSTile(3265, 3161, 3), new RSTile(3272, 3173, 3)),
			new RSTile(3269, 3168, 3), new RSTile(3284, 3166, 3), 100, 270, true, false),

	AL_KHARID_ZIP_LINE("Teeth-grip", "Zip Line", new RSArea(new RSTile(3283, 3160, 3), new RSTile(3302, 3176, 3)),
			new RSTile(3301, 3163, 3), new RSTile(3315, 3163, 1), 100, 0, true, false),

	AL_KHARID_TROPICAL_TREE("Swing-across", "Tropical tree",
			new RSArea(new RSTile(3313, 3160, 1), new RSTile(3318, 3165, 1)), new RSTile(3318, 3165, 1),
			new RSTile(3317, 3173, 2), 100, 0, false, false),

	AL_KHARID_ROOF_TOP_BEAMS("Climb", "Roof top beams",
			new RSArea(new RSTile(3312, 3173, 2), new RSTile(3318, 3179, 2)), new RSTile(3316, 3179, 2),
			new RSTile(3316, 3181, 3), 100, 90, true, false),

	AL_KHARID_TIGHTROPE_2("Cross", "Tightrope", new RSArea(new RSTile(3312, 3180, 3), new RSTile(3318, 3186, 3)),
			new RSTile(3314, 3186, 3), new RSTile(3302, 3186, 3), 60, 60, false, false),

	AL_KHARID_GAP("Jump", "Gap", new RSArea(new RSTile(3298, 3185, 3), new RSTile(3305, 3193, 3)),
			new RSTile(3300, 3192, 3), new RSTile(3299, 3194, 0), 100, 310, false, false),

	/**
	 * VARROCK ROOFTOP
	 */
	VARROCK_ROUGH_WALL("Climb", "Rough wall", new RSArea(new RSTile(3210, 3409, 0), new RSTile(3225, 3422, 0)),
			new RSTile(3222, 3414, 0), new RSTile(3219, 3414, 3), 100, 140, false, false),

	VARROCK_CLOTHES_LINE("Cross", "Clothes line", new RSArea(new RSTile(3214, 3410, 3), new RSTile(3219, 3419, 3)),
			new RSTile(3214, 3414, 3), new RSTile(3208, 3414, 3), 85, 140, false, false),

	VARROCK_GAP_1("Leap", "Gap", new RSArea(new RSTile(3201, 3413, 3), new RSTile(3208, 3417, 3)),
			new RSTile(3202, 3416, 3), new RSTile(3197, 3416, 1), 85, 110, true, false),

	VARROCK_WALL("Balance", "Wall", new RSArea(new RSTile(3194, 3416, 1), new RSTile(3197, 3416, 1)),
			new RSTile(3194, 3416, 1), new RSTile(3190, 3407, 1), 60, 110, false, false),

	VARROCK_GAP_2("Leap", "Gap", new RSArea(new RSTile(3192, 3402, 3), new RSTile(3198, 3406, 3)),
			new RSTile(3193, 3402, 3), new RSTile(3193, 3398, 3), 100, 120, false, false),

	VARROCK_GAP_3("Leap", "Gap", new RSArea(new RSTile(3183, 3382, 3), new RSTile(3208, 3403, 3)),
			new RSTile(3208, 3397, 3), new RSTile(3218, 3399, 3), 100, 270, false, false),

	VARROCK_GAP_4("Leap", "Gap", new RSArea(new RSTile(3218, 3393, 3), new RSTile(3232, 3402, 3)),
			new RSTile(3232, 3402, 3), new RSTile(3236, 3403, 3), 100, 300, false, false),

	VARROCK_LEDGE("Hurdle", "Ledge", new RSArea(new RSTile(3235, 3403, 3), new RSTile(3240, 3408, 3)),
			new RSTile(3236, 3407, 3), new RSTile(3236, 3410, 3), 100, 300, false, false),

	VARROCK_EDGE("Jump-off", "Edge", new RSArea(new RSTile(3236, 3410, 3), new RSTile(3240, 3415, 3)),
			new RSTile(3236, 3415, 3), new RSTile(3236, 3418, 0), 100, 300, false, false),

	/**
	 * FALADOR ROOFTOP
	 */
	FALADOR_WALL("Climb", "Rough wall", new RSArea(new RSTile(3024, 3329, 0), new RSTile(3045, 3345, 0)),
			new RSTile(3036, 3341, 0), new RSTile(3036, 3343, 3), 100, 0, true, false),

	FALADOR_TIGHTROPE_1("Cross", "Tightrope", new RSArea(new RSTile(3036, 3342, 3), new RSTile(3040, 3343, 3)),
			new RSTile(3039, 3343, 3), new RSTile(3047, 3344, 3), 100, 0, false, false),

	FALADOR_HAND_HOLDS("Cross", "Hand holds", new RSArea(new RSTile(3044, 3341, 3), new RSTile(3051, 3349, 3)),
			new RSTile(3050, 3349, 3), new RSTile(3050, 3357, 3), 100, 0, true, false),

	FALADOR_GAP_1("Jump", "Gap", new RSArea(new RSTile(3048, 3357, 3), new RSTile(3050, 3358, 3)),
			new RSTile(3048, 3358, 3), new RSTile(3048, 3361, 3), 100, 0, false, false),

	FALADOR_GAP_2("Jump", "Gap", new RSArea(new RSTile(3045, 3361, 3), new RSTile(3048, 3367, 3)),
			new RSTile(3045, 3361, 3), new RSTile(3041, 3361, 3), 100, 0, false, false),

	FALADOR_TIGHTROPE_2("Cross", "Tightrope", new RSArea(new RSTile(3034, 3361, 3), new RSTile(3041, 3364, 3)),
			new RSTile(3035, 3361, 3), new RSTile(3028, 3354, 3), 100, 140, false, false),

	FALADOR_TIGHTROPE_3("Cross", "Tightrope", new RSArea(new RSTile(3026, 3352, 3), new RSTile(3029, 3355, 3)),
			new RSTile(3027, 3353, 3), new RSTile(3020, 3353, 3), 100, 140, false, false),

	FALADOR_GAP_3("Jump", "Gap", new RSArea(new RSTile(3009, 3353, 3), new RSTile(3021, 3358, 3)),
			new RSTile(3018, 3353, 3), new RSTile(3018, 3349, 3), 100, 140, true, false),

	FALADOR_LEDGE_1("Jump", "Ledge", new RSArea(new RSTile(3016, 3343, 3), new RSTile(3022, 3349, 3)),
			new RSTile(3016, 3346, 3), new RSTile(3014, 3346, 3), 100, 180, false, false),

	FALADOR_LEDGE_2("Jump", "Ledge", new RSArea(new RSTile(3011, 3344, 3), new RSTile(3014, 3346, 3)),
			new RSTile(3013, 3344, 3), new RSTile(3013, 3341, 3), 100, 180, false, false),

	FALADOR_LEDGE_3("Jump", "Ledge", new RSArea(new RSTile(3009, 3335, 3), new RSTile(3013, 3342, 3)),
			new RSTile(3013, 3335, 3), new RSTile(3013, 3333, 3), 100, 320, true, false),

	FALADOR_LEDGE_4("Jump", "Ledge", new RSArea(new RSTile(3012, 3331, 3), new RSTile(3017, 3334, 3)),
			new RSTile(3016, 3332, 3), new RSTile(3020, 3333, 3), 100, 320, true, false),

	FALADOR_EDGE("Jump", "Edge", new RSArea(new RSTile(3019, 3332, 3), new RSTile(3024, 3335, 3)),
			new RSTile(3024, 3334, 3), new RSTile(3029, 3333, 0), 100, 0, false, false),

	/**
	 * SEERS ROOFTOP
	 */
	SEERS_WALL("Climb-up", "Wall", new RSArea(new RSTile(2717, 3482, 0), new RSTile(2737, 3495, 0)),
			new RSTile(2729, 3487, 0), new RSTile(2729, 3491, 3), 100, 0, false, false),

	SEERS_GAP_1("Jump", "Gap", new RSArea(new RSTile(2721, 3490, 3), new RSTile(2730, 3497, 3)),
			new RSTile(2723, 3493, 3), new RSTile(2713, 3494, 2), 45, 100, false, false),

	SEERS_TIGHTROPE("Cross", "Tightrope", new RSArea(new RSTile(2705, 3488, 2), new RSTile(2713, 3495, 2)),
			new RSTile(2710, 3490, 2), new RSTile(2710, 3480, 2), 45, 130, false, false),

	SEERS_GAP_2("Jump", "Gap", new RSArea(new RSTile(2710, 3477, 2), new RSTile(2715, 3481, 2)),
			new RSTile(2710, 3477, 2), new RSTile(2710, 3472, 3), 45, 130, false, false),

	SEERS_GAP_3("Jump", "Gap", new RSArea(new RSTile(2700, 3470, 3), new RSTile(2715, 3475, 2)),
			new RSTile(2704, 3471, 3), new RSTile(2702, 3465, 2), 45, 130, false, false),

	SEERS_EDGE("Jump", "Edge", new RSArea(new RSTile(2698, 3460, 2), new RSTile(2702, 3465, 2)),
			new RSTile(2702, 3465, 2), new RSTile(2705, 3464, 0), 100, 130, false, true);

	private final String action;
	private final String name;
	private final RSArea area;
	private final RSTile start;
	private final RSTile end;
	private final int angle;
	private final int rotation;
	private final boolean move;
	private final boolean sleep;

	OBSTACLE(String action, String name, RSArea area, RSTile start, RSTile end, int angle, int rotation, boolean move,
			boolean sleep) {
		this.action = action;
		this.name = name;
		this.area = area;
		this.start = start;
		this.end = end;
		this.angle = angle;
		this.rotation = rotation;
		this.move = move;
		this.sleep = sleep;
	}

	public String getAction() {
		return this.action;
	}

	public String getName() {
		return this.name;
	}

	public RSArea getArea() {
		return this.area;
	}

	public RSTile getStartTile() {
		return this.start;
	}

	public RSTile getEndTile() {
		return this.end;
	}

	public int getAngle() {
		return this.angle;
	}

	public int getRotation() {
		return this.rotation;
	}

	public boolean shouldMove() {
		return this.move;
	}

	public boolean shouldSleep() {
		return this.sleep;
	}

}
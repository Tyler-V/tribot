package scripts.tutorial;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

public class Variables {
	
	// TILES
	public final static RSTile QUEST_BUILDING_UPSTAIRS = new RSTile(3084, 3124, 1);

	// AREAS
	public final static RSArea LUMBRIDGE_SPAWN = new RSArea(new RSTile(3200, 3200, 0), new RSTile(3300, 3300, 0));

	public final static RSArea RUNESCAPE_GUIDE_AREA = new RSArea(new RSTile[] { new RSTile(3092, 3112, 0),
			new RSTile(3095, 3112, 0), new RSTile(3097, 3111, 0), new RSTile(3098, 3110, 0), new RSTile(3098, 3105, 0),
			new RSTile(3096, 3104, 0), new RSTile(3095, 3100, 0), new RSTile(3092, 3100, 0), new RSTile(3091, 3102, 0),
			new RSTile(3087, 3102, 0), new RSTile(3087, 3107, 0) });

	public final static RSArea SURVIVAL_EXPERT_AREA = new RSArea(new RSTile[] { new RSTile(3083, 3099, 0),
			new RSTile(3084, 3097, 0), new RSTile(3087, 3096, 0), new RSTile(3089, 3094, 0), new RSTile(3090, 3094, 0),
			new RSTile(3090, 3091, 0), new RSTile(3091, 3088, 0), new RSTile(3092, 3089, 0), new RSTile(3095, 3088, 0),
			new RSTile(3097, 3087, 0), new RSTile(3102, 3087, 0), new RSTile(3106, 3089, 0), new RSTile(3108, 3094, 0),
			new RSTile(3108, 3098, 0), new RSTile(3107, 3101, 0), new RSTile(3105, 3102, 0), new RSTile(3104, 3106, 0),
			new RSTile(3104, 3108, 0), new RSTile(3102, 3112, 0), new RSTile(3097, 3115, 0), new RSTile(3089, 3115, 0),
			new RSTile(3086, 3110, 0), new RSTile(3083, 3105, 0) });

	public final static RSArea MASTER_CHEF_AREA = new RSArea(new RSTile[] { new RSTile(3077, 3097, 0),
			new RSTile(3083, 3098, 0), new RSTile(3088, 3095, 0), new RSTile(3090, 3093, 0), new RSTile(3090, 3090, 0),
			new RSTile(3090, 3086, 0), new RSTile(3091, 3078, 0), new RSTile(3081, 3065, 0), new RSTile(3070, 3073, 0),
			new RSTile(3073, 3082, 0), new RSTile(3073, 3086, 0), new RSTile(3073, 3091, 0),
			new RSTile(3077, 3092, 0) });

	public final static RSArea QUEST_GUIDE_AREA = new RSArea(new RSTile[] { new RSTile(3077, 3092, 0),
			new RSTile(3073, 3092, 0), new RSTile(3073, 3087, 0), new RSTile(3072, 3082, 0), new RSTile(3066, 3082, 0),
			new RSTile(3056, 3093, 0), new RSTile(3058, 3100, 0), new RSTile(3063, 3117, 0), new RSTile(3071, 3125, 0),
			new RSTile(3069, 3133, 0), new RSTile(3084, 3134, 0), new RSTile(3093, 3133, 0), new RSTile(3096, 3125, 0),
			new RSTile(3094, 3117, 0), new RSTile(3094, 3115, 0), new RSTile(3088, 3112, 0), new RSTile(3083, 3103, 0),
			new RSTile(3083, 3098, 0), new RSTile(3076, 3098, 0) });

	public final static RSArea MINING_INSTRUCTOR_AREA = new RSArea(new RSTile[] { new RSTile(3081, 9527, 0),
			new RSTile(3088, 9527, 0), new RSTile(3092, 9515, 0), new RSTile(3095, 9505, 0), new RSTile(3095, 9501, 0),
			new RSTile(3093, 9499, 0), new RSTile(3088, 9493, 0), new RSTile(3080, 9491, 0), new RSTile(3070, 9496, 0),
			new RSTile(3071, 9510, 0) });

	public final static RSArea COMBAT_INSTRUCTOR_AREA = new RSArea(new RSTile[] { new RSTile(3095, 9506, 0),
			new RSTile(3095, 9500, 0), new RSTile(3096, 9494, 0), new RSTile(3104, 9496, 0), new RSTile(3108, 9504, 0),
			new RSTile(3116, 9508, 0), new RSTile(3118, 9512, 0), new RSTile(3116, 9524, 0), new RSTile(3115, 9534, 0),
			new RSTile(3106, 9534, 0), new RSTile(3102, 9528, 0), new RSTile(3095, 9524, 0),
			new RSTile(3091, 9512, 0) });

	public final static RSArea BANK_AREA = new RSArea(new RSTile[] { new RSTile(3093, 3133, 0), new RSTile(3094, 3114, 0),
			new RSTile(3101, 3111, 0), new RSTile(3106, 3100, 0), new RSTile(3111, 3105, 0), new RSTile(3114, 3105, 0),
			new RSTile(3114, 3109, 0), new RSTile(3119, 3109, 0), new RSTile(3119, 3111, 0), new RSTile(3127, 3111, 0),
			new RSTile(3129, 3111, 0), new RSTile(3131, 3116, 0), new RSTile(3132, 3120, 0), new RSTile(3132, 3122, 0),
			new RSTile(3125, 3122, 0), new RSTile(3124, 3134, 0) });

	public final static RSArea FINANCIAL_ADVISOR_AREA = new RSArea(new RSTile[] { new RSTile(3130, 3126, 0),
			new RSTile(3125, 3126, 0), new RSTile(3125, 3123, 0), new RSTile(3130, 3123, 0) });

	public final static RSArea BROTHER_BRACE_AREA = new RSArea(new RSTile[] { new RSTile(3144, 3134, 0),
			new RSTile(3122, 3135, 0), new RSTile(3121, 3131, 0), new RSTile(3125, 3131, 0), new RSTile(3128, 3129, 0),
			new RSTile(3130, 3126, 0), new RSTile(3130, 3123, 0), new RSTile(3133, 3123, 0), new RSTile(3132, 3118, 0),
			new RSTile(3130, 3114, 0), new RSTile(3128, 3111, 0), new RSTile(3120, 3111, 0), new RSTile(3119, 3109, 0),
			new RSTile(3115, 3108, 0), new RSTile(3115, 3105, 0), new RSTile(3119, 3105, 0), new RSTile(3120, 3103, 0),
			new RSTile(3138, 3103, 0), new RSTile(3141, 3105, 0), new RSTile(3150, 3105, 0) });

	public final static RSArea MAGIC_INSTRUCTOR_AREA = new RSArea(new RSTile[] { new RSTile(3113, 3105, 0),
			new RSTile(3113, 3104, 0), new RSTile(3108, 3101, 0), new RSTile(3108, 3087, 0), new RSTile(3107, 3064, 0),
			new RSTile(3117, 3063, 0), new RSTile(3131, 3068, 0), new RSTile(3136, 3070, 0), new RSTile(3144, 3070, 0),
			new RSTile(3156, 3073, 0), new RSTile(3155, 3088, 0), new RSTile(3152, 3099, 0), new RSTile(3152, 3105, 0),
			new RSTile(3140, 3104, 0), new RSTile(3138, 3103, 0), new RSTile(3120, 3103, 0),
			new RSTile(3119, 3104, 0) });

	// PATHS
	public final static RSTile[] QUEST_GUIDE_PATH_1 = new RSTile[] { new RSTile(3072, 3090, 0),
			new RSTile(3069, 3096, 0), new RSTile(3070, 3102, 0), new RSTile(3071, 3106, 0), new RSTile(3069, 3111, 0),
			new RSTile(3069, 3116, 0), new RSTile(3072, 3120, 0), new RSTile(3075, 3124, 0), new RSTile(3079, 3128, 0),
			new RSTile(3083, 3129, 0), new RSTile(3086, 3126, 0) };

	public final static RSTile[] QUEST_GUIDE_PATH_2 = new RSTile[] { new RSTile(3072, 3090, 0),
			new RSTile(3071, 3094, 0), new RSTile(3071, 3100, 0), new RSTile(3073, 3105, 0), new RSTile(3077, 3107, 0),
			new RSTile(3078, 3112, 0), new RSTile(3078, 3116, 0), new RSTile(3076, 3124, 0), new RSTile(3081, 3128, 0),
			new RSTile(3084, 3128, 0), new RSTile(3086, 3126, 0) };

	public final static RSTile[] QUEST_GUIDE_PATH_3 = new RSTile[] { new RSTile(3072, 3090, 0),
			new RSTile(3071, 3094, 0), new RSTile(3071, 3099, 0), new RSTile(3072, 3103, 0), new RSTile(3074, 3106, 0),
			new RSTile(3079, 3108, 0), new RSTile(3082, 3110, 0), new RSTile(3085, 3113, 0), new RSTile(3088, 3115, 0),
			new RSTile(3091, 3119, 0), new RSTile(3091, 3123, 0), new RSTile(3090, 3126, 0),
			new RSTile(3086, 3126, 0) };

	public final static RSTile[] BROTHER_BRACE_PATH_1 = new RSTile[] { new RSTile(3130, 3124, 0),
			new RSTile(3134, 3122, 0), new RSTile(3134, 3118, 0), new RSTile(3133, 3114, 0), new RSTile(3130, 3109, 0),
			new RSTile(3129, 3107, 0) };

	public final static RSTile[] BROTHER_BRACE_PATH_2 = new RSTile[] { new RSTile(3130, 3124, 0),
			new RSTile(3134, 3123, 0), new RSTile(3136, 3119, 0), new RSTile(3136, 3114, 0), new RSTile(3135, 3109, 0),
			new RSTile(3132, 3107, 0), new RSTile(3129, 3107, 0) };

	public final static RSTile[] MAGIC_INSTRUCTOR_PATH_1 = new RSTile[] { new RSTile(3122, 3102, 0),
			new RSTile(3119, 3098, 0), new RSTile(3117, 3094, 0), new RSTile(3118, 3090, 0), new RSTile(3119, 3087, 0),
			new RSTile(3122, 3087, 0), new RSTile(3125, 3087, 0), new RSTile(3128, 3085, 0), new RSTile(3132, 3087, 0),
			new RSTile(3138, 3086, 0), new RSTile(3140, 3087, 0) };

	public final static RSTile[] MAGIC_INSTRUCTOR_PATH_2 = new RSTile[] { new RSTile(3122, 3102, 0),
			new RSTile(3125, 3098, 0), new RSTile(3126, 3094, 0), new RSTile(3130, 3092, 0), new RSTile(3135, 3092, 0),
			new RSTile(3138, 3089, 0), new RSTile(3140, 3086, 0) };

	public final static RSTile[] MAGIC_INSTRUCTOR_PATH_3 = new RSTile[] { new RSTile(3122, 3102, 0),
			new RSTile(3122, 3095, 0), new RSTile(3124, 3092, 0), new RSTile(3126, 3088, 0), new RSTile(3130, 3086, 0),
			new RSTile(3134, 3088, 0), new RSTile(3139, 3086, 0), new RSTile(3137, 3087, 0) };

}

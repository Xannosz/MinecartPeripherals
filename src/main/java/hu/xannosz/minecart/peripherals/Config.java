package hu.xannosz.minecart.peripherals;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> DETECTION_RANGE;

    static {
        BUILDER.push("Configs for Minecart Peripherals Mod");

        DETECTION_RANGE = BUILDER.comment("Detection range")
                .defineInRange("detectionRange", 25, 5, 75);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

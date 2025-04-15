package hu.xannosz.minecart.peripherals;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import static hu.xannosz.minecart.peripherals.Registration.MINECART_LOADER_TURTLE_UPGRADE;

@Mod(MinecartPeripherals.MOD_ID)
public class MinecartPeripherals {
	public static final String MOD_ID = "minecart_peripherals";

	public MinecartPeripherals() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		Registration.register(modEventBus);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");

		ForgeComputerCraftAPI.registerPeripheralProvider((level, pos, side) -> {
			BlockEntity entity = level.getBlockEntity(pos);
			if (!(entity instanceof MinecartDetectorBlockEntity blockEntity)) {
				return LazyOptional.empty();
			}
			return LazyOptional.of(() -> {
				if (ModList.get().isLoaded("betterminecarts")) {
					return new ExtendedMinecartDetectorPeripheral(blockEntity);
				} else {
					return new MinecartDetectorPeripheral(blockEntity);
				}
			});
		});

		modEventBus.addListener((RegisterEvent event) -> {
			event.register(TurtleUpgradeSerialiser.registryId(), new ResourceLocation(MinecartPeripherals.MOD_ID, "minecart_loader_turtle_upgrade"), () -> MINECART_LOADER_TURTLE_UPGRADE);
		});

		modEventBus.addListener(this::addCreative);
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey().toString().contains("computercraft:tab")) {
			event.accept(Registration.MINECART_DETECTOR_ITEM);
			event.accept(Registration.MINECART_LOADER_ITEM);
		}
	}
}

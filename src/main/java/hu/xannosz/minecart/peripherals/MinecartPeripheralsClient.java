package hu.xannosz.minecart.peripherals;

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static hu.xannosz.minecart.peripherals.Registration.MINECART_LOADER_TURTLE_UPGRADE;

@Mod.EventBusSubscriber(modid = MinecartPeripherals.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MinecartPeripheralsClient {
	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void registerTurtleModels(RegisterTurtleModellersEvent event) {
		event.register(MINECART_LOADER_TURTLE_UPGRADE, TurtleUpgradeModeller.flatItem());
	}
}

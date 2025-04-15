package hu.xannosz.minecart.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class MinecartLoaderTurtleUpgrade extends AbstractTurtleUpgrade {
	protected MinecartLoaderTurtleUpgrade(ResourceLocation id, ItemStack stack) {
		super(id, TurtleUpgradeType.PERIPHERAL, stack);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		if (ModList.get().isLoaded("betterminecarts")) {
			return new ExtendedMinecartLoaderPeripheral(turtle);
		} else {
			return new MinecartLoaderPeripheral(turtle);
		}
	}
}

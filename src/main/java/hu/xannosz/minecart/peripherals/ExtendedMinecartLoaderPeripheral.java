package hu.xannosz.minecart.peripherals;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.turtle.ITurtleAccess;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import hu.xannosz.betterminecarts.utils.Colorable;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Optional;
import java.util.UUID;

public class ExtendedMinecartLoaderPeripheral extends MinecartLoaderPeripheral {
	public ExtendedMinecartLoaderPeripheral(ITurtleAccess turtle) {
		super(turtle);
	}

	@LuaFunction(mainThread = true)
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public void colorizeMinecart(int slot, Optional<Boolean> isBottom) throws LuaException {
		boolean bottom = isBottom.isEmpty() || isBottom.get();
		ItemStack fromStack = turtle.getInventory().getItem(slot - 1);
		AbstractMinecart minecart = getMinecartAtFront();
		MinecartColor color = MinecartColor.getFromItem(fromStack.getItem());

		if (color != null && minecart instanceof AbstractLocomotive locomotive) {
			Player player = new FakePlayer((ServerLevel) turtle.getLevel(),
					new GameProfile(UUID.randomUUID() ,"MinecartPeripheral"));
			player.setItemInHand(InteractionHand.MAIN_HAND, fromStack);
			player.setShiftKeyDown(bottom);
			locomotive.interact(player, InteractionHand.MAIN_HAND);
		}

		if (color != null && minecart instanceof Colorable colorable) {
			colorable.setColor(color.getLabel());
			fromStack.shrink(1);
		}
	}
}

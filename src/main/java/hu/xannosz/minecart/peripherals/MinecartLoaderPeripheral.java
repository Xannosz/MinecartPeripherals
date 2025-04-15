package hu.xannosz.minecart.peripherals;

import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Slf4j
public class MinecartLoaderPeripheral implements IPeripheral {

	protected final ITurtleAccess turtle;

	public MinecartLoaderPeripheral(ITurtleAccess turtle) {
		this.turtle = turtle;
	}

	@NotNull
	@Override
	public String getType() {
		return "minecartLoader";
	}

	@NotNull
	@Override
	public Object getTarget() {
		return turtle;
	}

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof MinecartLoaderPeripheral && ((MinecartLoaderPeripheral) other).turtle == turtle;
	}

	@LuaFunction(mainThread = true)
	public boolean isMinecartAtTheFront() {
		try {
			getMinecartAtFront();
		} catch (LuaException ex) {
			return false;
		}
		return true;
	}

	@LuaFunction(mainThread = true)
	public boolean isValidInventory() {
		try {
			getInventoryAtFront();
		} catch (LuaException ex) {
			return false;
		}
		return true;
	}

	@LuaFunction(mainThread = true)
	public int inventorySize() throws LuaException {
		return getInventoryAtFront().getSlots();
	}

	@LuaFunction(mainThread = true)
	public Map<Integer, Map<String, ?>> inventoryList() throws LuaException {
		IItemHandler inventory = getInventoryAtFront();
		Map<Integer, Map<String, ?>> result = new HashMap<>();
		var size = inventory.getSlots();
		for (var i = 0; i < size; i++) {
			var stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty()) result.put(i + 1, VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack));
		}

		return result;
	}

	@LuaFunction(mainThread = true)
	public Map<String, ?> getItemDetail(int slot) throws LuaException {
		IItemHandler inventory = getInventoryAtFront();
		assertBetween(slot, inventory.getSlots(), "Slot out of range (%s)");

		var stack = inventory.getStackInSlot(slot - 1);
		return stack.isEmpty() ? null : VanillaDetailRegistries.ITEM_STACK.getDetails(stack);
	}

	@LuaFunction(mainThread = true)
	public int getItemLimit(int slot) throws LuaException {
		IItemHandler inventory = getInventoryAtFront();
		assertBetween(slot, inventory.getSlots(), "Slot out of range (%s)");
		return inventory.getSlotLimit(slot - 1);
	}

	@LuaFunction(mainThread = true)
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public int pushItems(int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot) throws LuaException {
		var inventory = getInventoryAtFront();

		assertBetween(fromSlot, turtle.getInventory().getContainerSize(), "From slot out of range (%s)");
		ItemStack fromStack = turtle.getInventory().getItem(fromSlot - 1);
		int actualLimit = limit.orElse(fromStack.getCount());
		if (toSlot.isPresent()) {
			assertBetween(toSlot.get(), inventory.getSlots(), "To slot out of range (%s)");
		}
		if (actualLimit <= 0) {
			return 0;
		}

		if (toSlot.isEmpty()) {
			int added = 0;
			for (int i = 0; i < inventory.getSlots(); i++) {
				int consumed = addToSlot(i, inventory, fromStack, actualLimit);
				actualLimit -= consumed;
				added += consumed;
			}
			return added;
		} else {
			return addToSlot(toSlot.get(), inventory, fromStack, actualLimit);
		}
	}

	@LuaFunction(mainThread = true)
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public int pullItems(int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot) throws LuaException {
		var inventory = getInventoryAtFront();

		assertBetween(fromSlot, inventory.getSlots(), "From slot out of range (%s)");
		ItemStack fromStack = inventory.getStackInSlot(fromSlot - 1);
		int actualLimit = limit.orElse(fromStack.getCount());
		if (toSlot.isPresent()) {
			assertBetween(toSlot.get(), turtle.getInventory().getContainerSize(), "To slot out of range (%s)");
		}

		if (actualLimit <= 0) {
			return 0;
		}

		if (toSlot.isEmpty()) {
			int added = 0;
			for (int i = 0; i < turtle.getInventory().getContainerSize(); i++) {
				int consumed = addToSlot(i, turtle.getInventory(), fromStack, actualLimit);
				actualLimit -= consumed;
				added += consumed;
			}
			return added;
		} else {
			return addToSlot(toSlot.get(), turtle.getInventory(), fromStack, actualLimit);
		}
	}

	@LuaFunction(mainThread = true)
	public void addMinecart(String direction, int slot) throws LuaException {
		assertBetween(slot, turtle.getInventory().getContainerSize(), "Slot out of range (%s)");
		final BlockPos blockPos = getFrontBlock();
		Direction dir;
		switch (direction.toLowerCase(Locale.US)) {
			case "north" -> dir = Direction.NORTH;
			case "south" -> dir = Direction.SOUTH;
			case "west" -> dir = Direction.WEST;
			case "east" -> dir = Direction.EAST;
			default -> throw new LuaException("Invalid direction: " + direction +
					"\n[\"north\", \"south\", \"west\", \"east\"]");
		}
		if (turtle.getLevel().getBlockState(blockPos).getBlock() instanceof BaseRailBlock) {
			ItemStack itemStack = turtle.getInventory().getItem(slot - 1);
			UseOnContext context = new UseOnContext(turtle.getLevel(),
					null, InteractionHand.MAIN_HAND, itemStack,
					new BlockHitResult(new Vec3(0, 0, 0),
							dir, blockPos, true));
			itemStack.useOn(context);
		}
	}

	@LuaFunction(mainThread = true)
	@SuppressWarnings("ConstantConditions")
	public void removeMinecart(int slot) throws LuaException {
		assertBetween(slot, turtle.getInventory().getContainerSize(), "Slot out of range (%s)");
		AbstractMinecart minecart = getMinecartAtFront();
		minecart.destroy(turtle.getLevel().damageSources().genericKill());
		ItemStack result = minecart.getPickResult();
		List<ItemEntity> itemEntities = turtle.getLevel().getEntitiesOfClass(ItemEntity.class,
				new AABB(getFrontBlock().getX() - 1, getFrontBlock().getY() - 1, getFrontBlock().getZ() - 1,
						getFrontBlock().getX() + 1, getFrontBlock().getY() + 1, getFrontBlock().getZ() + 1));
		for (ItemEntity entity : itemEntities) {
			if (entity.getItem().getItem().equals(result.getItem())) {
				if (turtle.getInventory().getItem(slot - 1).isEmpty()) {
					turtle.getInventory().setItem(slot - 1, entity.getItem());
					entity.remove(Entity.RemovalReason.DISCARDED);
				}
				break;
			}
		}
	}

	@LuaFunction(mainThread = true)
	public void renameMinecart(String name) throws LuaException {
		AbstractMinecart minecart = getMinecartAtFront();
		minecart.setCustomName(Component.literal(name));
	}

	@LuaFunction(mainThread = true)
	public void toggleMinecartName(boolean showName) throws LuaException {
		AbstractMinecart minecart = getMinecartAtFront();
		minecart.setCustomNameVisible(showName);
	}

	protected AbstractMinecart getMinecartAtFront() throws LuaException {
		List<AbstractMinecart> minecarts = turtle.getLevel().getEntitiesOfClass(AbstractMinecart.class, new AABB(getFrontBlock()));
		if (minecarts.isEmpty()) {
			throw new LuaException("Minecart not found");
		}
		return minecarts.get(0);
	}

	private static int addToSlot(int toSlot, IItemHandler toInventory, ItemStack fromStack, int actualLimit) {
		ItemStack toStack = toInventory.getStackInSlot(toSlot - 1);
		if (toStack.isEmpty()) {
			ItemStack s = fromStack.copy();
			s.setCount(actualLimit);
			toInventory.insertItem(toSlot - 1, s, false);
			fromStack.shrink(actualLimit);
			return actualLimit;
		} else {
			if (toStack.getItem().equals(fromStack.getItem())) {
				actualLimit = Math.min(actualLimit, toStack.getMaxStackSize() - toStack.getCount());
				toStack.setCount(toStack.getCount() + actualLimit);
				fromStack.shrink(actualLimit);
				return actualLimit;
			}
		}
		return 0;
	}

	private static int addToSlot(int toSlot, Container toInventory, ItemStack fromStack, int actualLimit) {
		ItemStack toStack = toInventory.getItem(toSlot - 1);
		if (toStack.isEmpty()) {
			ItemStack s = fromStack.copy();
			s.setCount(actualLimit);
			toInventory.setItem(toSlot - 1, s);
			fromStack.shrink(actualLimit);
			return actualLimit;
		} else {
			if (toStack.getItem().equals(fromStack.getItem())) {
				actualLimit = Math.min(actualLimit, toStack.getMaxStackSize() - toStack.getCount());
				toStack.setCount(toStack.getCount() + actualLimit);
				fromStack.shrink(actualLimit);
				return actualLimit;
			}
		}
		return 0;
	}

	private IItemHandler getInventoryAtFront() throws LuaException {
		return getMinecartAtFront().getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new LuaException("Minecart inventory not found"));
	}

	private BlockPos getFrontBlock() {
		return turtle.getPosition().relative(turtle.getDirection());
	}

	private void assertBetween(int value, int max, String message) throws LuaException {
		if (value < 1 || value > max) {
			throw new LuaException(String.format(message, "between " + 1 + " and " + max));
		}
	}
}

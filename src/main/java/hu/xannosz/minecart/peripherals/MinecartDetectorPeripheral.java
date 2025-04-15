package hu.xannosz.minecart.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MinecartDetectorPeripheral implements IPeripheral {
	@NotNull
	protected final MinecartDetectorBlockEntity blockEntity;

	public MinecartDetectorPeripheral(@NotNull MinecartDetectorBlockEntity blockEntity) {
		this.blockEntity = blockEntity;
	}

	@NotNull
	@Override
	public String getType() {
		return "minecartDetector";
	}

	@Override
	public void attach(IComputerAccess computer) {
		blockEntity.attach(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		blockEntity.detach(computer);
	}

	@NotNull
	@Override
	public Object getTarget() {
		return blockEntity;
	}

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof MinecartDetectorPeripheral && ((MinecartDetectorPeripheral) other).blockEntity == blockEntity;
	}

	@LuaFunction(mainThread = true)
	public List<Map<String, Object>> listRails() {
		return new ArrayList<>(blockEntity.getRails().values());
	}

	@LuaFunction(mainThread = true)
	public List<Map<String, Object>> listMinecarts() {
		return new ArrayList<>(blockEntity.getMinecarts().values());
	}

	@LuaFunction(mainThread = true)
	public Map<String, Object> getRail(int x, int y, int z) throws LuaException {
		Map<String, Object> data = blockEntity.getRails().get(
				new BlockPos(x + blockEntity.getBlockPos().getX(),
						y + blockEntity.getBlockPos().getY(),
						z + blockEntity.getBlockPos().getZ()));
		if (data == null) {
			throw new LuaException("No accessible rail on : [" + x + ", " + y + ", " + z + "]");
		}
		return data;
	}

	@LuaFunction(mainThread = true)
	public Map<String, Object> getMinecart(int id) throws LuaException {
		Map<String, Object> data = blockEntity.getMinecarts().get(id);
		if (data == null) {
			throw new LuaException("No accessible minecart with Id : " + id);
		}
		return data;
	}

	@LuaFunction(mainThread = true)
	public void activateMinecart(int id, boolean activate) {
		blockEntity.activateMinecart(id, activate);
	}

	@LuaFunction(mainThread = true)
	public void moveMinecart(int id, String direction) throws LuaException {
		Direction dir;
		switch (direction.toLowerCase(Locale.US)) {
			case "north" -> dir = Direction.NORTH;
			case "south" -> dir = Direction.SOUTH;
			case "west" -> dir = Direction.WEST;
			case "east" -> dir = Direction.EAST;
			default -> throw new LuaException("Invalid direction: " + direction +
					"\n[\"north\", \"south\", \"west\", \"east\"]");
		}
		blockEntity.moveMinecart(id, dir);
	}

	@LuaFunction(mainThread = true)
	public void stopMinecart(int id) {
		blockEntity.stopMinecart(id);
	}

	@LuaFunction(mainThread = true)
	public Map<Integer, Map<String, ?>> getMinecartInventoryList(int id) throws LuaException {
		return blockEntity.getMinecartInventoryList(id);
	}
}

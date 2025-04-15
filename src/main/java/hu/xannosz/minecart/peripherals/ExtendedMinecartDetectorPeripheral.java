package hu.xannosz.minecart.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import hu.xannosz.betterminecarts.utils.ButtonId;
import org.jetbrains.annotations.NotNull;

public class ExtendedMinecartDetectorPeripheral extends MinecartDetectorPeripheral {
	public ExtendedMinecartDetectorPeripheral(@NotNull MinecartDetectorBlockEntity blockEntity) {
		super(blockEntity);
	}

	@LuaFunction(mainThread = true)
	public void setLocomotiveSpeed(int id, int speed) {
		switch (speed) {
			case 3 -> blockEntity.clickLocomotiveButton(id, ButtonId.FF_FORWARD);
			case 2 -> blockEntity.clickLocomotiveButton(id, ButtonId.F_FORWARD);
			case 1 -> blockEntity.clickLocomotiveButton(id, ButtonId.FORWARD);
			case 0 -> blockEntity.clickLocomotiveButton(id, ButtonId.PAUSE);
			case -1 -> blockEntity.clickLocomotiveButton(id, ButtonId.STOP);
			case -2 -> blockEntity.clickLocomotiveButton(id, ButtonId.BACK);
		}
	}

	@LuaFunction(mainThread = true)
	public void toggleLocomotiveLamp(int id) {
		blockEntity.clickLocomotiveButton(id, ButtonId.LAMP);
	}

	@LuaFunction(mainThread = true)
	public void toggleLocomotiveSignal(int id) {
		blockEntity.clickLocomotiveButton(id, ButtonId.REDSTONE);
	}

	@LuaFunction(mainThread = true)
	public void whistle(int id) {
		blockEntity.clickLocomotiveButton(id, ButtonId.WHISTLE);
	}

	@LuaFunction(mainThread = true)
	public void connect(int id1, int id2) {
		blockEntity.connect(id1, id2);
	}

	@LuaFunction(mainThread = true)
	public void disconnect(int id1, int id2) {
		blockEntity.disconnect(id1, id2);
	}

	@LuaFunction(mainThread = true)
	public void revert(int id1, int id2) {
		blockEntity.revert(id1, id2);
	}
}

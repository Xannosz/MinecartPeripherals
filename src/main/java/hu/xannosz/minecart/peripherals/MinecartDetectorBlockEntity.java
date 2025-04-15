package hu.xannosz.minecart.peripherals;

import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import hu.xannosz.betterminecarts.item.ModItems;
import hu.xannosz.betterminecarts.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MinecartDetectorBlockEntity extends BlockEntity implements IPeripheralProvider {
	private final MinecartDetectorPeripheral peripheral;
	private final Set<IComputerAccess> computers = new HashSet<>();
	private Map<BlockPos, Map<String, Object>> rails = new HashMap<>();
	private Map<Integer, Map<String, Object>> minecarts = new HashMap<>();

	public MinecartDetectorBlockEntity(BlockPos pos, BlockState state) {
		super(Registration.MINECART_DETECTOR_BLOCK_ENTITY.get(), pos, state);
		if (ModList.get().isLoaded("betterminecarts")) {
			peripheral = new ExtendedMinecartDetectorPeripheral(this);
		} else {
			peripheral = new MinecartDetectorPeripheral(this);
		}
	}

	private static final Capability<IPeripheralProvider> PERIPHERAL_PROVIDER_CAPABILITY =
			CapabilityManager.get(new CapabilityToken<>() {
			});

	private final LazyOptional<IPeripheralProvider> peripheralProviderOptional = LazyOptional.of(() -> this);

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == PERIPHERAL_PROVIDER_CAPABILITY) {
			return peripheralProviderOptional.cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		peripheralProviderOptional.invalidate();
	}

	@NotNull
	@Override
	public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
		return LazyOptional.of(() -> peripheral);
	}

	public void attach(IComputerAccess computer) {
		computers.add(computer);
	}

	public void detach(IComputerAccess computer) {
		computers.remove(computer);
	}

	public Map<BlockPos, Map<String, Object>> getRails() {
		return rails;
	}

	public Map<Integer, Map<String, Object>> getMinecarts() {
		return minecarts;
	}

	@SuppressWarnings("ConstantConditions")
	public void activateMinecart(int id, boolean activate) {
		if (minecarts.containsKey(id)) {
			if (getLevel().getEntity(id) instanceof AbstractMinecart minecart) {
				minecart.activateMinecart(minecart.getBlockX(), minecart.getBlockY(), minecart.getBlockZ(), activate);
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void moveMinecart(int id, Direction direction) {
		if (minecarts.containsKey(id)) {
			getLevel().getEntity(id).push(direction.getStepX() / 20d, 0, direction.getStepZ() / 20d);
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void stopMinecart(int id) {
		if (minecarts.containsKey(id)) {
			getLevel().getEntity(id).setDeltaMovement(0, 0, 0);
		}
	}

	@SuppressWarnings("ConstantConditions")
	public Map<Integer, Map<String, ?>> getMinecartInventoryList(int id) throws LuaException {
		if (minecarts.containsKey(id)) {
			IItemHandler inventory = getLevel().getEntity(id).getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new LuaException("Minecart inventory not found"));
			Map<Integer, Map<String, ?>> result = new HashMap<>();
			var size = inventory.getSlots();
			for (var i = 0; i < size; i++) {
				var stack = inventory.getStackInSlot(i);
				if (!stack.isEmpty()) result.put(i + 1, VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack));
			}

			return result;
		} else {
			throw new LuaException("No accessible minecart with Id : " + id);
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void clickLocomotiveButton(int id, ButtonId buttonId) {
		if (minecarts.containsKey(id)) {
			if (getLevel().getEntity(id) instanceof AbstractLocomotive locomotive) {
				locomotive.executeButtonClick(buttonId);
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void connect(int id1, int id2) {
		if (minecarts.containsKey(id1) || minecarts.containsKey(id2)) {
			ItemStack stack = new ItemStack(ModItems.CROWBAR.get());
			stack.getOrCreateTag().putInt("firstCartId", id1);
			stack.getOrCreateTag().putString("mode", CrowbarMode.CONNECT.getLabel());
			TrainUtil.clickedByCrowbar(stack, getLevel().getEntity(id2), (ServerLevel) getLevel());
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void disconnect(int id1, int id2) {
		if (minecarts.containsKey(id1) || minecarts.containsKey(id2)) {
			ItemStack stack = new ItemStack(ModItems.CROWBAR.get());
			stack.getOrCreateTag().putInt("firstCartId", id1);
			stack.getOrCreateTag().putString("mode", CrowbarMode.DISCONNECT.getLabel());
			TrainUtil.clickedByCrowbar(stack, getLevel().getEntity(id2), (ServerLevel) getLevel());
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void revert(int id1, int id2) {
		if (minecarts.containsKey(id1) || minecarts.containsKey(id2)) {
			ItemStack stack = new ItemStack(ModItems.CROWBAR.get());
			stack.getOrCreateTag().putInt("firstCartId", id1);
			stack.getOrCreateTag().putString("mode", CrowbarMode.REVERT.getLabel());
			TrainUtil.clickedByCrowbar(stack, getLevel().getEntity(id2), (ServerLevel) getLevel());
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void tick() {
		if (level == null || level.isClientSide()) {
			return;
		}

		// update rail list
		Map<BlockPos, Map<String, Object>> railsDone = new HashMap<>();
		Map<BlockPos, Map<String, Object>> railsInProgress = new HashMap<>();
		if (getRailData(getBlockPos().above()) != null) {
			railsInProgress.put(getBlockPos().above(), getRailData(getBlockPos().above()));
		}

		for (int i = 0; i < Config.DETECTION_RANGE.get(); i++) {
			Map<BlockPos, Map<String, Object>> newRailsInProgress = new HashMap<>();
			for (Map.Entry<BlockPos, Map<String, Object>> rail : railsInProgress.entrySet()) {
				addRailData(rail.getKey().north(), newRailsInProgress);
				addRailData(rail.getKey().west(), newRailsInProgress);
				addRailData(rail.getKey().south(), newRailsInProgress);
				addRailData(rail.getKey().east(), newRailsInProgress);

				addRailData(rail.getKey().north().above(), newRailsInProgress);
				addRailData(rail.getKey().west().above(), newRailsInProgress);
				addRailData(rail.getKey().south().above(), newRailsInProgress);
				addRailData(rail.getKey().east().above(), newRailsInProgress);

				addRailData(rail.getKey().north().below(), newRailsInProgress);
				addRailData(rail.getKey().west().below(), newRailsInProgress);
				addRailData(rail.getKey().south().below(), newRailsInProgress);
				addRailData(rail.getKey().east().below(), newRailsInProgress);

				railsDone.put(rail.getKey(), rail.getValue());
			}
			railsInProgress = newRailsInProgress;
		}

		rails = new HashMap<>(railsDone);

		// update minecart list
		Map<Integer, Map<String, Object>> updatedMinecarts = new HashMap<>();
		for (BlockPos pos : rails.keySet()) {
			List<AbstractMinecart> minecartList = getLevel().getEntitiesOfClass(AbstractMinecart.class, new AABB(pos));
			for (AbstractMinecart minecart : minecartList) {
				updatedMinecarts.put(minecart.getId(), getMinecartData(minecart));
			}
		}

		// detect minecart stepped in
		Map<Integer, Map<String, Object>> steppedInMinecarts = new HashMap<>(updatedMinecarts);
		for (Map.Entry<Integer, Map<String, Object>> m : minecarts.entrySet()) {
			steppedInMinecarts.remove(m.getKey());
		}

		// detect minecart stepped out
		Map<Integer, Map<String, Object>> steppedOutMinecarts = new HashMap<>(minecarts);
		for (Map.Entry<Integer, Map<String, Object>> m : updatedMinecarts.entrySet()) {
			steppedOutMinecarts.remove(m.getKey());
		}

		// send events to computers
		for (IComputerAccess computerAccess : computers) {
			for (Map.Entry<Integer, Map<String, Object>> in : steppedInMinecarts.entrySet()) {
				computerAccess.queueEvent("minecart_stepped_in", computerAccess.getAttachmentName(), in.getKey(), in.getValue());
			}
			for (Map.Entry<Integer, Map<String, Object>> out : steppedOutMinecarts.entrySet()) {
				computerAccess.queueEvent("minecart_stepped_out", computerAccess.getAttachmentName(), out.getKey(), out.getValue());
			}
		}

		minecarts = updatedMinecarts;
		setChanged();
	}

	@SuppressWarnings("ConstantConditions")
	private Map<String, Object> getRailData(BlockPos pos) {
		BlockState state = getLevel().getBlockState(pos);
		if (state.getBlock() instanceof BaseRailBlock rail) {
			Map<String, Object> railData = new HashMap<>();
			railData.put("X", pos.getX() - getBlockPos().getX());
			railData.put("Y", pos.getY() - getBlockPos().getY());
			railData.put("Z", pos.getZ() - getBlockPos().getZ());
			railData.put("Type", state.getBlock().getDescriptionId());
			railData.put("RailShape", rail.getRailDirection(state, level, pos, null).getName());
			if (state.hasProperty(BlockStateProperties.POWERED)) {
				railData.put("Powered", state.getValue(BlockStateProperties.POWERED));
			}
			return railData;
		}
		return null;
	}

	@SuppressWarnings("ConstantConditions")
	private Map<String, Object> getMinecartData(AbstractMinecart minecart) {
		Map<String, Object> minecartData = new HashMap<>();
		minecartData.put("X", minecart.getX() - getBlockPos().getX());
		minecartData.put("Y", minecart.getY() - getBlockPos().getY());
		minecartData.put("Z", minecart.getZ() - getBlockPos().getZ());
		minecartData.put("Id", minecart.getId());
		minecartData.put("Type", minecart.getEncodeId());
		minecartData.put("UUID", minecart.getStringUUID());
		minecartData.put("MovementX", minecart.getDeltaMovement().x);
		minecartData.put("MovementY", minecart.getDeltaMovement().y);
		minecartData.put("MovementZ", minecart.getDeltaMovement().z);
		minecartData.put("IsNameVisible", minecart.isCustomNameVisible());
		if (minecart.hasCustomName()) {
			minecartData.put("Name", minecart.getCustomName().getString());
		}

		if (ModList.get().isLoaded("betterminecarts")) {
			if (minecart instanceof Linkable linkable) {
				AbstractMinecart parent = linkable.getLinkedParent();
				if (parent != null) {
					minecartData.put("LinkedParent", parent.getId());
				}
				AbstractMinecart child = linkable.getLinkedChild();
				if (child != null) {
					minecartData.put("LinkedChild", child.getId());
				}
				Linkable head = TrainUtil.getHeadOfTrain(linkable);
				if (head != null) {
					minecartData.put("TrainHeadId", ((AbstractMinecart) head).getId());
				}
			}
			if (minecart instanceof Colorable colorable) {
				minecartData.put("Color", colorable.getColor().getLabel());
			}
			if (minecart instanceof AbstractLocomotive locomotive) {
				minecartData.put("TopColor", locomotive.getTopFilter().getLabel());
				minecartData.put("BottomColor", locomotive.getBottomFilter().getLabel());
			}
		}

		return minecartData;
	}

	private void addRailData(BlockPos pos, Map<BlockPos, Map<String, Object>> rails) {
		Map<String, Object> railData = getRailData(pos);
		if (railData != null) {
			rails.put(pos, railData);
		}
	}
}

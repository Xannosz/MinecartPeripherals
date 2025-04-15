package hu.xannosz.minecart.peripherals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinecartDetectorBlock extends BaseEntityBlock {
	public MinecartDetectorBlock() {
		super(BlockBehaviour.Properties.copy(Blocks.DIRT).noOcclusion().sound(SoundType.METAL));
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return Registration.MINECART_DETECTOR_BLOCK_ENTITY.get().create(pos, state);
	}

	@Nullable
	@Override
	@SuppressWarnings("ConstantConditions")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level _level, @NotNull BlockState _state, @NotNull BlockEntityType<T> _type) {
		return (level, pos, state, type) -> ((MinecartDetectorBlockEntity) level.getBlockEntity(pos)).tick();
	}
}

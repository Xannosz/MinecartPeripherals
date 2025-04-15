package hu.xannosz.minecart.peripherals;

import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MinecartPeripherals.MOD_ID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MinecartPeripherals.MOD_ID);
	private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MinecartPeripherals.MOD_ID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MinecartPeripherals.MOD_ID);

	public static final RegistryObject<Block> MINECART_DETECTOR_BLOCK = BLOCKS.register("minecart_detector_block", MinecartDetectorBlock::new);
	public static final RegistryObject<BlockEntityType<MinecartDetectorBlockEntity>> MINECART_DETECTOR_BLOCK_ENTITY =
			BLOCK_ENTITIES.register("minecart_detector_block_entity", () ->
					BlockEntityType.Builder.of(MinecartDetectorBlockEntity::new, MINECART_DETECTOR_BLOCK.get()).build(null));
	public static final RegistryObject<Item> MINECART_DETECTOR_ITEM = ITEMS.register("minecart_detector_item", () -> new BlockItem(MINECART_DETECTOR_BLOCK.get(), new Item.Properties()));
	public static final RegistryObject<Item> MINECART_LOADER_ITEM = ITEMS.register("minecart_loader_item", () -> new Item(new Item.Properties()));

	public static final TurtleUpgradeSerialiser<MinecartLoaderTurtleUpgrade> MINECART_LOADER_TURTLE_UPGRADE = TurtleUpgradeSerialiser.simpleWithCustomItem(
			MinecartLoaderTurtleUpgrade::new
	);

	public static void register(IEventBus e) {
		BLOCKS.register(e);
		ITEMS.register(e);
		MENUS.register(e);
		BLOCK_ENTITIES.register(e);
	}
}

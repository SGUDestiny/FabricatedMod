package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.block_entities.FabricatorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FabricatedMod.MODID);

    public static final RegistryObject<BlockEntityType<FabricatorBlockEntity>> FABRICATOR = BLOCK_ENTITIES.register("fabricator",
            () -> BlockEntityType.Builder.of(FabricatorBlockEntity::new, BlockInit.FABRICATOR.get()).build(null));

    public static void register(IEventBus bus)
    {
        BLOCK_ENTITIES.register(bus);
    }
}

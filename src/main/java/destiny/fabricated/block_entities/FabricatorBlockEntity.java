package destiny.fabricated.block_entities;

import destiny.fabricated.init.BlockEntityInit;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.menu.FabricatorUpgradesMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FabricatorBlockEntity extends BlockEntity implements GeoBlockEntity
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation OPEN = RawAnimation.begin().thenPlay("open");
    protected static final RawAnimation OPEN_THEN_IDLE = RawAnimation.begin().thenPlay("open").thenLoop("open_idle");
    protected static final RawAnimation OPEN_IDLE = RawAnimation.begin().thenLoop("open_idle");
    protected static final RawAnimation FABRICATE_THEN_IDLE = RawAnimation.begin().thenPlay("fabricate").thenLoop("open_idle");
    protected static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("close");

    public ItemStackHandler upgrades = createHandler(6);

    public FabricatorBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(BlockEntityInit.FABRICATOR.get(), pPos, pBlockState);
    }

    public ItemStackHandler getUpgrades()
    {
        return upgrades;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FabricatorBlockEntity fabricator)
    {

    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.put("upgrades", upgrades.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.upgrades.deserializeNBT(tag.getCompound("upgrades"));
    }

    public void open(Level level, BlockPos pos, FabricatorBlockEntity fabricator)
    {
        fabricator.stopTriggeredAnimation("fabricator", null);
        fabricator.triggerAnim("fabricator", "open_then_idle");
        level.playSound(null, pos, SoundInit.FABRICATOR_OPEN.get(), SoundSource.BLOCKS);
    }

    public void close(Level level, BlockPos pos, FabricatorBlockEntity fabricator)
    {
        fabricator.stopTriggeredAnimation("fabricator", null);
        fabricator.triggerAnim("fabricator", "close");
        level.playSound(null, pos, SoundInit.FABRICATOR_CLOSE.get(), SoundSource.BLOCKS);
    }

    public void fabricate(Level level, BlockPos pos, FabricatorBlockEntity fabricator)
    {
        fabricator.stopTriggeredAnimation("fabricator", null);
        fabricator.triggerAnim("fabricator", "fabricate_then_idle");
        level.playSound(null, pos, SoundInit.FABRICATOR_FABRICATE.get(), SoundSource.BLOCKS);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "fabricator", 0, state -> PlayState.STOP).triggerableAnim("open", OPEN).triggerableAnim("open_then_idle", OPEN_THEN_IDLE).triggerableAnim("open_idle", OPEN_IDLE).triggerableAnim("fabricate_then_idle", FABRICATE_THEN_IDLE).triggerableAnim("close", CLOSE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }

    public void openUpgradesMenu(Player player, Level level, BlockPos pos, FabricatorBlockEntity fabricator)
    {
        if(!level.isClientSide())
        {
            MenuProvider provider = new MenuProvider() {
                @Override
                public Component getDisplayName()
                {
                    return Component.translatable("screen.fabricated.fabricator_upgrades");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory,
                                                                  Player pPlayer)
                {
                    return new FabricatorUpgradesMenu(pContainerId, pPlayerInventory, fabricator);
                }
            };
            NetworkHooks.openScreen((ServerPlayer) player, provider, fabricator.getBlockPos());
        }
    }

    public ItemStackHandler createHandler(int size)
    {
        return new ItemStackHandler(size)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                markUpdated();
            }
        };
    }

    public void markUpdated()
    {
        super.setChanged();
        if(level != null)
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }
}

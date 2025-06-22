package destiny.fabricated.block_entities;

import destiny.fabricated.init.BlockEntityInit;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.menu.FabricatorUpgradesMenu;
import destiny.fabricated.network.packets.FabricatorCraftItemPacket;
import destiny.fabricated.network.packets.FabricatorUpdateStatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import destiny.fabricated.items.FabricatorRecipeModuleItem.RecipeData;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.keyframe.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class FabricatorBlockEntity extends BlockEntity implements GeoBlockEntity
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static class Animations {
        protected static final String MAIN_CONTROLLER = "main";

        protected static final RawAnimation OPEN = RawAnimation.begin().thenPlay("fabricator.open");
        protected static final RawAnimation OPEN_THEN_IDLE = RawAnimation.begin().thenPlay("fabricator.open").thenLoop("fabricator.open_idle");
        protected static final RawAnimation OPEN_IDLE = RawAnimation.begin().thenLoop("fabricator.open_idle");
        protected static final RawAnimation FABRICATE_THEN_IDLE = RawAnimation.begin().thenPlay("fabricator.fabricate");
        protected static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("fabricator.idle_loop");
        protected static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("fabricator.close");
        protected static final RawAnimation CLOSE_THEN_IDLE = RawAnimation.begin().thenPlay("fabricator.close").thenLoop("fabricator.idle_loop");

        private Animations() {}
    }

    public ItemStackHandler upgrades = createHandler(6);
    public List<RecipeData> recipeTypes = new ArrayList<>();
    public ItemStack craftStack = ItemStack.EMPTY;
    public int state = 2;

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
        fabricator.recipeTypes.clear();
        for (int i = 0; i < fabricator.upgrades.getSlots(); i++)
        {
            ItemStack stack = fabricator.upgrades.getStackInSlot(i);
            if(stack.getItem() instanceof FabricatorRecipeModuleItem recipeModule)
            {
                fabricator.recipeTypes.addAll(recipeModule.getRecipeTypes(stack));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.put("upgrades", upgrades.serializeNBT());
        ListTag recipeList = new ListTag();
        this.recipeTypes.forEach(data -> recipeList.add(data.serialize()));
        tag.put("recipes", recipeList);
        tag.putInt("state", this.state);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.upgrades.deserializeNBT(tag.getCompound("upgrades"));
        tag.getList("recipes", StringTag.TAG_STRING).forEach(keyTag -> {
            RecipeData data = new RecipeData();
            data.deserialize(((CompoundTag) keyTag));
            this.recipeTypes.add(data);
        });
        this.state = tag.getInt("state");
    }

    public void open(Level level, BlockPos pos, FabricatorBlockEntity fabricator)
    {
        level.playSound(null, pos, SoundInit.FABRICATOR_OPEN.get(), SoundSource.BLOCKS);

        if(level.isClientSide())
            state = 1;
        else NetworkInit.sendToTracking(fabricator, new FabricatorUpdateStatePacket(pos, 1));

    }

    public void close(Level level, BlockPos pos, FabricatorBlockEntity fabricator)
    {
        level.playSound(null, pos, SoundInit.FABRICATOR_CLOSE.get(), SoundSource.BLOCKS);

        if(level.isClientSide())
            state = 0;
        else NetworkInit.sendToTracking(fabricator, new FabricatorUpdateStatePacket(pos, 0));
    }

    public void fabricate(Level level, BlockPos pos, FabricatorBlockEntity fabricator, ItemStack stack)
    {
        //fabricator.triggerAnim("main", Animations.FABRICATE_THEN_IDLE.toString());
        level.playSound(null, pos, SoundInit.FABRICATOR_FABRICATE.get(), SoundSource.BLOCKS);

        this.craftStack = stack;
        if(level.isClientSide())
            state = 3;
        else NetworkInit.sendToTracking(fabricator, new FabricatorUpdateStatePacket(pos, 3));
    }

    private <T extends FabricatorBlockEntity> PlayState handleAnimationState(AnimationState<T> state) {
        if (this.state == 1)
            return state.setAndContinue(Animations.OPEN_THEN_IDLE);
        else if(this.state == 0)
            return state.setAndContinue(Animations.CLOSE_THEN_IDLE);
        else if(this.state == 3)
            return state.setAndContinue(Animations.FABRICATE_THEN_IDLE);
        else if(this.state == 4)
            return state.setAndContinue(Animations.OPEN_IDLE);
        else
            return state.setAndContinue(Animations.IDLE_LOOP);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<FabricatorBlockEntity> controller = new AnimationController<>(this, Animations.MAIN_CONTROLLER, 0, this::handleAnimationState);
        //controller.setAnimation(Animations.IDLE_LOOP);
        controller.setCustomInstructionKeyframeHandler(
        event ->
        {
            if(event.getKeyframeData().getInstructions().equals("craftItem"))
            {
                this.state = 4;
                NetworkInit.sendToServer(new FabricatorCraftItemPacket(this.craftStack, this.getBlockPos()));
            }
        });
        controllers.add(controller);
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

    public void openCraftingMenu(Player player, Level level, BlockPos pos, FabricatorBlockEntity fabricator)
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
                    return new FabricatorCraftingMenu(pContainerId, pPlayerInventory, fabricator);
                }
            };
            NetworkHooks.openScreen((ServerPlayer) player, provider, fabricator.getBlockPos());
            this.open(level, pos, fabricator);
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

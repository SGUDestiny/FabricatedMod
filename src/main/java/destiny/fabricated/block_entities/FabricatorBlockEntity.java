package destiny.fabricated.block_entities;

import destiny.fabricated.client.screen.FabricatorCraftScreen;
import destiny.fabricated.init.BlockEntityInit;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.items.FabricatorBulkModuleItem;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import destiny.fabricated.items.FabricatorRecipeModuleItem.RecipeData;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.menu.FabricatorUpgradesMenu;
import destiny.fabricated.network.packets.FabricatorCraftItemPacket;
import destiny.fabricated.network.packets.FabricatorUpdateStatePacket;
import destiny.fabricated.network.packets.ServerboundFabricatorStatePacket;
import destiny.fabricated.network.packets.ServerboundSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FabricatorBlockEntity extends BlockEntity implements GeoBlockEntity
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static class Animations {
        protected static final String MAIN_CONTROLLER = "main";

        protected static final RawAnimation OPEN = RawAnimation.begin().thenPlay("fabricator.open");
        protected static final RawAnimation OPEN_THEN_IDLE = RawAnimation.begin().thenPlay("fabricator.open").thenLoop("fabricator.open_idle");
        protected static final RawAnimation OPEN_IDLE = RawAnimation.begin().thenLoop("fabricator.open_idle");
        protected static final RawAnimation FABRICATE_THEN_IDLE = RawAnimation.begin().thenPlay("fabricator.fabricate").thenLoop("fabricator.open_idle");
        protected static final RawAnimation IDLE_LOOP = RawAnimation.begin().thenLoop("fabricator.idle_loop");
        protected static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("fabricator.close");
        protected static final RawAnimation CLOSE_THEN_IDLE = RawAnimation.begin().thenPlay("fabricator.close").thenLoop("fabricator.idle_loop");

        private Animations() {}
    }

    public ItemStackHandler upgrades = createHandler(6);
    public ItemStack craftStack = ItemStack.EMPTY;
    public List<ItemStack> ingredients = new ArrayList<>();
    public boolean isOpen = false;

    public int state = 2;
    public int fabricationStep = 0;
    public int fabricationCounter = -1;

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
        if(fabricator.fabricationCounter != -1)
        {
            fabricator.fabricationCounter += 1;
        }

        if(!level.isClientSide() && (!fabricator.isOpen && fabricator.state == 4))
            fabricator.close(level, pos, fabricator);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.put("upgrades", upgrades.serializeNBT());
        //tag.putBoolean("is_open", isOpen);
        //tag.putInt("state", this.state);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.upgrades.deserializeNBT(tag.getCompound("upgrades"));
        //this.isOpen = tag.getBoolean("is_open");
        //this.state = tag.getInt("state");

        this.isOpen = false;
        this.state = 2;
    }

    public Set<RecipeData> getRecipeTypes()
    {
        Set<RecipeData> recipeTypes = new HashSet<>();
        for (int i = 0; i < this.upgrades.getSlots(); i++)
        {
            ItemStack stack = this.upgrades.getStackInSlot(i);
            if(stack.getItem() instanceof FabricatorRecipeModuleItem recipeModule)
            {
                recipeTypes.addAll(recipeModule.getRecipeTypes(stack));
            }
        }

        return recipeTypes;
    }

    public void open(Level level, BlockPos pos, FabricatorBlockEntity fabricator)
    {
        level.playSound(null, pos, SoundInit.FABRICATOR_OPEN.get(), SoundSource.BLOCKS);
        setChanged();
        if(level.isClientSide())
        {
            state = 1;
            NetworkInit.sendToServer(new ServerboundFabricatorStatePacket(pos, 1, true));
        }
        else
        {
            this.state = 1;

            fabricator.isOpen = true;
            NetworkInit.sendToTracking(fabricator, new FabricatorUpdateStatePacket(pos, 1, true));
        }
    }

    public void close(Level level, BlockPos pos, FabricatorBlockEntity fabricator)
    {
        level.playSound(null, pos, SoundInit.FABRICATOR_CLOSE.get(), SoundSource.BLOCKS);
        setChanged();
        if(level.isClientSide())
        {
            state = 0;
            NetworkInit.sendToServer(new ServerboundFabricatorStatePacket(pos, 0, false));
        }
        else
        {
            this.state = 0;
            fabricator.isOpen = false;
            NetworkInit.sendToTracking(fabricator, new FabricatorUpdateStatePacket(pos, 0, false));
        }
    }

    public void fabricate(Level level, BlockPos pos, FabricatorBlockEntity fabricator, ItemStack stack, List<ItemStack> ingredients)
    {

        this.craftStack = stack;
        this.ingredients = ingredients;
        setChanged();
        if(level.isClientSide())
        {
            state = 3;
            NetworkInit.sendToServer(new ServerboundSoundPacket(pos, SoundInit.FABRICATOR_FABRICATE.get()));
            NetworkInit.sendToServer(new ServerboundFabricatorStatePacket(pos, 3, this.isOpen));
        }
        else
        {
            this.state = 3;
            NetworkInit.sendToTracking(fabricator, new FabricatorUpdateStatePacket(pos, 3, this.isOpen));
        }
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
        controller.setCustomInstructionKeyframeHandler(
        event ->
        {
            if (event.getKeyframeData().getInstructions().equals("start"))
            {
                this.fabricationStep = 1;
                this.fabricationCounter = 0;
            }
            if (event.getKeyframeData().getInstructions().equals("switch"))
            {
                this.fabricationStep = 2;
                this.fabricationCounter = 0;
            }
            if (event.getKeyframeData().getInstructions().equals("end"))
            {
                this.fabricationStep = 0;
                this.fabricationCounter = -1;
                this.state = 4;
                NetworkInit.sendToServer(new ServerboundFabricatorStatePacket(this.getBlockPos(), 4, this.isOpen));
                NetworkInit.sendToServer(new FabricatorCraftItemPacket(this.craftStack, this.ingredients, this.getBlockPos()));
                this.craftStack = ItemStack.EMPTY;
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
            this.isOpen = true;
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

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack)
            {
                if(stack.getItem() instanceof FabricatorBulkModuleItem || stack.getItem() instanceof FabricatorRecipeModuleItem)
                {
                    return this.stacks.stream().noneMatch(module -> module.getItem() instanceof FabricatorBulkModuleItem) || !(stack.getItem() instanceof FabricatorBulkModuleItem);
                }

                return false;
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

package destiny.fabricated.block_entities;

import destiny.fabricated.blocks.FabricatorBlock;
import destiny.fabricated.client.screen.FabricatorCraftScreen;
import destiny.fabricated.init.BlockEntityInit;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.items.FabricatorBulkModuleItem;
import destiny.fabricated.items.FabricatorModuleItem;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import destiny.fabricated.items.FabricatorRecipeModuleItem.RecipeData;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.menu.FabricatorUpgradesMenu;
import destiny.fabricated.network.ServerPacketHandler;
import destiny.fabricated.network.packets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
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
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class FabricatorBlockEntity extends BlockEntity implements GeoBlockEntity
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static class Animations
    {
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
    public int batchValue = 1;

    public int fabricatingTicker = 0;

    public boolean closeAfterCraft = false;

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
        if (level.isClientSide()) fabricator.clientTick();
        else fabricator.serverTick();
    }

    public void clientTick()
    {
        if(this.getBlockState().getValue(FabricatorBlock.STATE).equals(FabricatorBlock.FabricatorState.FABRICATING))
        {
            fabricatingTicker++;
        }

        if(fabricatingTicker > 60)
        {
            fabricatingTicker = 0;
            if(Minecraft.getInstance().screen instanceof FabricatorCraftScreen craftScreen)
                craftScreen.recipeStuff(craftScreen.selectedTypeKey);
        }
    }

    public void serverTick()
    {
        if(this.getBlockState().getValue(FabricatorBlock.STATE).equals(FabricatorBlock.FabricatorState.FABRICATING))
        {
            fabricatingTicker++;
        }

        if(this.fabricatingTicker > 60)
        {
            ItemStack result = this.craftStack.copyWithCount(this.craftStack.getCount() * this.batchValue);

            ItemEntity itemEntity = new ItemEntity(level, this.getBlockPos().getCenter().x, this.getBlockPos().getCenter().y, this.getBlockPos().getCenter().z, result);
            level.addFreshEntity(itemEntity);

            this.fabricatingTicker = 0;
            this.craftStack = ItemStack.EMPTY;
            if(closeAfterCraft)
                close(level, getBlockPos(), false);
            else
            {
                NetworkInit.sendToTracking(this, new ClientboundFabricatorRecalcRecipesPacket());

                triggerAnim("main", "open_idle");
                level.setBlock(getBlockPos(), getBlockState().setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.OPEN), 2);
            }
            markUpdated();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.put("upgrades", upgrades.serializeNBT());
        tag.putInt("batch", this.batchValue);

        CompoundTag itemTag = new CompoundTag();
        tag.put("craft_stack", this.craftStack.save(itemTag));
        tag.putInt("fabricating_ticker", this.fabricatingTicker);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.upgrades.deserializeNBT(tag.getCompound("upgrades"));
        this.batchValue = tag.getInt("batch");

        this.craftStack = ItemStack.of(tag.getCompound("craft_stack"));
        this.fabricatingTicker = tag.getInt("fabricating_ticker");
    }

    public List<RecipeData> getRecipeTypes()
    {
        List<RecipeData> recipeTypes = new ArrayList<>();
        for (int i = 0; i < this.upgrades.getSlots(); i++)
        {
            ItemStack stack = this.upgrades.getStackInSlot(i);
            if(stack.getItem() instanceof FabricatorRecipeModuleItem recipeModule)
            {
                recipeModule.getRecipeTypes(stack).forEach(data -> {
                    if(recipeTypes.stream().noneMatch(recipeData -> recipeData.key.equals(data.key)))
                        recipeTypes.add(data);
                });
            }
        }

        return recipeTypes;
    }

    public void incrementBatch(int amount)
    {
        this.batchValue += amount;
        if(batchValue < 1)
            batchValue = 1;

        int maxValue = maxBatch();
        if(batchValue > maxValue)
            batchValue = maxValue;

        if(this.level != null && this.level.isClientSide())
            NetworkInit.sendToServer(new ServerboundFabricationBatchPacket(this.getBlockPos(), batchValue));
        markUpdated();
    }

    public void setBatch(int amount)
    {
        this.batchValue = amount;
        if(batchValue < 1)
            batchValue = 1;

        if(batchValue < 1)
            batchValue = 1;

        int maxValue = maxBatch();
        if(batchValue > maxValue)
            batchValue = maxValue;

        if(this.level != null && this.level.isClientSide())
            NetworkInit.sendToServer(new ServerboundFabricationBatchPacket(this.getBlockPos(), batchValue));
        markUpdated();
    }

    public int maxBatch()
    {
        int maxBatch = 1;
        for (int i = 0; i < this.upgrades.getSlots(); i++)
        {
            ItemStack stack = this.upgrades.getStackInSlot(i);
            if(stack.getItem() instanceof FabricatorBulkModuleItem bulkModule)
                maxBatch = bulkModule.getBulkAmount(stack);
        }

        return maxBatch;
    }

    public void open(Level level, BlockPos pos)
    {
        if(level.isClientSide())
        {
            NetworkInit.sendToServer(new ServerboundFabricatorAnimPacket(pos, "open", false));
            return;
        }

        level.playSound(null, pos, SoundInit.FABRICATOR_OPEN.get(), SoundSource.BLOCKS);
        level.setBlock(pos, getBlockState().setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.OPEN), 2);
        triggerAnim("main", "open");
    }

    public void close(Level level, BlockPos pos, boolean closeAfterCraft)
    {
        this.closeAfterCraft = closeAfterCraft;
        if(level.isClientSide())
        {

            NetworkInit.sendToServer(new ServerboundFabricatorAnimPacket(pos, "close", closeAfterCraft));
            return;
        }
        if(!closeAfterCraft)
        {
            level.setBlock(pos, getBlockState().setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.CLOSED), 2);
            triggerAnim("main", "close");
            level.playSound(null, pos, SoundInit.FABRICATOR_CLOSE.get(), SoundSource.BLOCKS);
        }
    }

    public void fabricate(Level level, BlockPos pos, ItemStack stack, List<ItemStack> ingredients)
    {
        if(level.isClientSide())
        {
            NetworkInit.sendToServer(new ServerboundFabricatorAnimPacket(pos, "fabricate", false));
            NetworkInit.sendToServer(new ServerboundFabricatorCraftItemPacket(pos, stack, ingredients));
            NetworkInit.sendToServer(new ServerboundSoundPacket(pos, SoundInit.FABRICATOR_FABRICATE.get()));
            return;
        }

        level.setBlock(pos, getBlockState().setValue(FabricatorBlock.STATE, FabricatorBlock.FabricatorState.FABRICATING), 2);
        triggerAnim("main", "fabricate");
    }

    private <T extends FabricatorBlockEntity> PlayState handleAnimationState(AnimationState<T> state) {
        return state.setAndContinue(Animations.IDLE_LOOP);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<FabricatorBlockEntity> controller = new AnimationController<>(this, Animations.MAIN_CONTROLLER, 0, this::handleAnimationState);
        controller.triggerableAnim("open", Animations.OPEN_THEN_IDLE);
        controller.triggerableAnim("open_idle", Animations.OPEN_IDLE);
        controller.triggerableAnim("close", Animations.CLOSE_THEN_IDLE);
        controller.triggerableAnim("fabricate", Animations.FABRICATE_THEN_IDLE);
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
                    return Component.translatable("screen.fabricated.fabricator_crafting");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory,
                                                                  Player pPlayer)
                {
                    return new FabricatorCraftingMenu(pContainerId, pPlayerInventory, fabricator);
                }
            };
            NetworkHooks.openScreen((ServerPlayer) player, provider, buf -> {
                buf.writeBlockPos(fabricator.getBlockPos());
                buf.writeInt(-1);
                buf.writeInt(0);
            });
            this.open(level, pos);
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
                if(stack.getItem() instanceof FabricatorModuleItem)
                {
                    if(stack.getItem() instanceof FabricatorRecipeModuleItem)
                    {
                        return this.stacks.stream().noneMatch(upgradeStack -> {
                            if(upgradeStack.getItem() instanceof FabricatorRecipeModuleItem recipeModule)
                            {
                                for (int i = 0; i < recipeModule.getRecipeTypes(stack).size(); i++)
                                {
                                    List<RecipeData> stackRecipes = recipeModule.getRecipeTypes(stack);
                                    for (int j = 0; j < recipeModule.getRecipeTypes(upgradeStack).size(); j++)
                                    {
                                        List<RecipeData> upgradeRecipes = recipeModule.getRecipeTypes(upgradeStack);
                                        RecipeData stackRecipe = stackRecipes.get(i);
                                        RecipeData upgradeRecipe = upgradeRecipes.get(j);

                                        if(stackRecipe.key.equals(upgradeRecipe.key))
                                            return true;

                                    }
                                }
                                return false;
                            }
                            return false;
                        });
                    }

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

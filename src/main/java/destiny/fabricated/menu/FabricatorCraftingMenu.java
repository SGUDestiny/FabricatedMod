package destiny.fabricated.menu;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.init.BlockInit;
import destiny.fabricated.init.MenuInit;
import destiny.fabricated.items.FabricatorModuleItem;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FabricatorCraftingMenu extends AbstractContainerMenu
{
    public FabricatorBlockEntity blockEntity;
    public Level level;
    public List<FabricatorRecipeModuleItem.RecipeData> recipeTypes;

    public FabricatorCraftingMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer)
    {
        this(containerId, inventory, inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public FabricatorCraftingMenu(int pContainerId, Inventory inventory, BlockEntity blockEntity)
    {
        super(MenuInit.FABRICATOR_CRAFTING.get(), pContainerId);
        this.blockEntity = ((FabricatorBlockEntity) blockEntity);
        this.level = inventory.player.level();
        this.recipeTypes = ((FabricatorBlockEntity) blockEntity).getRecipeTypes();
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player pPlayer)
    {
        if(this.blockEntity.state != 3)
            this.blockEntity.close(this.level, this.blockEntity.getBlockPos(), this.blockEntity);
        this.blockEntity.isOpen = false;
    }

    @Override
    public void slotsChanged(Container pContainer)
    {
        super.slotsChanged(pContainer);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.FABRICATOR.get());
    }
}

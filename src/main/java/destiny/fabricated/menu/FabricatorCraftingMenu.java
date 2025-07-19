package destiny.fabricated.menu;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.blocks.FabricatorBlock;
import destiny.fabricated.init.BlockInit;
import destiny.fabricated.init.MenuInit;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class FabricatorCraftingMenu extends AbstractContainerMenu
{
    public FabricatorBlockEntity blockEntity;
    public Level level;
    public List<FabricatorRecipeModuleItem.RecipeData> recipeTypes;
    public int item;
    public boolean switching;
    public int type;

    public FabricatorCraftingMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer)
    {
        this(containerId, inventory, inventory.player.level().getBlockEntity(buffer.readBlockPos()), buffer.readInt(), buffer.readInt());
    }

    public FabricatorCraftingMenu(int pContainerId, Inventory inventory, BlockEntity blockEntity)
    {
        super(MenuInit.FABRICATOR_CRAFTING.get(), pContainerId);
        this.blockEntity = ((FabricatorBlockEntity) blockEntity);
        this.level = inventory.player.level();
        this.recipeTypes = ((FabricatorBlockEntity) blockEntity).getRecipeTypes();
        this.type = -1;
        this.item = 0;

        for (int i = 0; i < 36; ++i)
        {
            this.addSlot(new Slot(inventory, i, -10000, 0));
        }
    }

    public FabricatorCraftingMenu(int pContainerId, Inventory inventory, BlockEntity blockEntity, int type, int target)
    {
        super(MenuInit.FABRICATOR_CRAFTING.get(), pContainerId);
        this.blockEntity = ((FabricatorBlockEntity) blockEntity);
        this.level = inventory.player.level();
        this.recipeTypes = ((FabricatorBlockEntity) blockEntity).getRecipeTypes();
        this.type = type;
        this.item = target;

        for (int i = 0; i < 36; ++i)
        {
            this.addSlot(new Slot(inventory, i, -10000, 0));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player pPlayer)
    {
        if(switching)
            return;

        blockEntity.close(level, blockEntity.getBlockPos(), blockEntity.getBlockState().getValue(FabricatorBlock.STATE).equals(FabricatorBlock.FabricatorState.FABRICATING));
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

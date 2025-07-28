package destiny.fabricated.recipes.containers;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FabricatorContainer implements CraftingContainer
{
    private List<ItemStack> items;

    public FabricatorContainer(List<ItemStack> stored)
    {
        this.items = stored;
    }

    @Override
    public int getContainerSize()
    {
        return items.size();
    }

    @Override
    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int pAmount)
    {
        return items.remove(slot);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        return items.remove(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack)
    {
        items.set(slot, stack);
    }

    @Override
    public void setChanged()
    {

    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
        items.clear();
    }

    @Override
    public int getWidth()
    {
        return 0;
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public List<ItemStack> getItems()
    {
        return this.items;
    }

    @Override
    public void fillStackedContents(StackedContents pContents)
    {

    }
}

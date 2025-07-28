package destiny.fabricated.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.List;

public class Fabrication
{
    public List<ItemStack> outputs;
    public List<ItemStack> inputs;

    public Fabrication(List<ItemStack> outputs, List<ItemStack> inputs)
    {
        this.outputs = outputs;
        this.inputs = inputs;
    }

    public List<ItemStack> getOutputs()
    {
        return outputs;
    }

    public List<ItemStack> getInputs()
    {
        return inputs;
    }

    public ItemStack getDisplayItem()
    {
        if(this.outputs.isEmpty())
            return ItemStack.EMPTY;

        return this.outputs.get(0);
    }
}

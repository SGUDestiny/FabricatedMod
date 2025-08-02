package destiny.fabricated.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Fabrication
{
    public List<ItemStack> outputs;
    public List<ItemStack> inputs;
    public Consumer<Fabrication> modifier;

    public Fabrication(List<ItemStack> outputs, List<ItemStack> inputs)
    {
        this.outputs = outputs;
        this.inputs = inputs;
        this.modifier = null;
    }

    public Fabrication(List<ItemStack> outputs, List<ItemStack> inputs, Consumer<Fabrication> modifier)
    {
        this.outputs = outputs;
        this.inputs = inputs;
        this.modifier = modifier;
    }

    public List<ItemStack> getOutputs()
    {
        if(modifier != null)
            modifier.accept(this);

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

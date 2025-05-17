package destiny.fabricated.client.model.block;

import destiny.fabricated.FabricatedMod;
import destiny.fabricated.block_entities.FabricatorBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class FabricatorModel extends DefaultedBlockGeoModel<FabricatorBlockEntity> {
    public FabricatorModel() {
        super(new ResourceLocation(FabricatedMod.MODID, "fabricator"));
    }

    @Override
    public RenderType getRenderType(FabricatorBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}

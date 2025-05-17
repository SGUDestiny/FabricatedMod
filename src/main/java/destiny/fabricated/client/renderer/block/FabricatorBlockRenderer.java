package destiny.fabricated.client.renderer.block;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.client.model.block.FabricatorModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FabricatorBlockRenderer extends GeoBlockRenderer<FabricatorBlockEntity> {
    public FabricatorBlockRenderer() {
        super(new FabricatorModel());
    }
}

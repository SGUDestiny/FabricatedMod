package destiny.fabricated.block_entities;

import destiny.fabricated.init.BlockEntityInit;
import destiny.fabricated.init.SoundInit;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FabricatorBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation OPEN = RawAnimation.begin().thenPlay("open");
    protected static final RawAnimation OPEN_THEN_IDLE = RawAnimation.begin().thenPlay("open").thenLoop("open_idle");
    protected static final RawAnimation OPEN_IDLE = RawAnimation.begin().thenLoop("open_idle");
    protected static final RawAnimation FABRICATE_THEN_IDLE = RawAnimation.begin().thenPlay("fabricate").thenLoop("open_idle");
    protected static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("close");

    public FabricatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.FABRICATOR.get(), pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FabricatorBlockEntity fabricator) {

    }

    public void open(Level level, BlockPos pos, FabricatorBlockEntity fabricator) {
        fabricator.stopTriggeredAnimation("fabricator", null);
        fabricator.triggerAnim("fabricator", "open_then_idle");
        level.playSound(null, pos, SoundInit.FABRICATOR_OPEN.get(), SoundSource.BLOCKS);
    }

    public void close(Level level, BlockPos pos, FabricatorBlockEntity fabricator) {
        fabricator.stopTriggeredAnimation("fabricator", null);
        fabricator.triggerAnim("fabricator", "close");
        level.playSound(null, pos, SoundInit.FABRICATOR_CLOSE.get(), SoundSource.BLOCKS);
    }

    public void fabricate(Level level, BlockPos pos, FabricatorBlockEntity fabricator) {
        fabricator.stopTriggeredAnimation("fabricator", null);
        fabricator.triggerAnim("fabricator", "fabricate_then_idle");
        level.playSound(null, pos, SoundInit.FABRICATOR_FABRICATE.get(), SoundSource.BLOCKS);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "fabricator", 0, state -> PlayState.STOP)
                .triggerableAnim("open", OPEN)
                .triggerableAnim("open_then_idle", OPEN_THEN_IDLE)
                .triggerableAnim("open_idle", OPEN_IDLE)
                .triggerableAnim("fabricate_then_idle", FABRICATE_THEN_IDLE)
                .triggerableAnim("close", CLOSE)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

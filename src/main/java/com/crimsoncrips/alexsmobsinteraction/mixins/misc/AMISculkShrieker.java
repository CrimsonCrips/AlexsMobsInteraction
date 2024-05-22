package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(SculkShriekerBlock.class)
public class AMISculkShrieker extends BaseEntityBlock implements SimpleWaterloggedBlock {

    @Shadow @Final public static BooleanProperty CAN_SUMMON;
    @Shadow @Final public static BooleanProperty SHRIEKING;

    protected AMISculkShrieker(Properties pProperties) {
        super(pProperties);
    }
    protected float getSoundVolume() {
        return 1.0F;
    }
    public float getVoicePitch() {
        return -20;
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (AMInteractionConfig.SKREECHER_WARD_ENABLED){
            ItemStack stack = player.getItemInHand(handIn);
            RandomSource random = player.getRandom();
            if (stack.getItem() == AMItemRegistry.SKREECHER_SOUL.get() && !state.getValue(CAN_SUMMON)) {
                for (int x = 0; x < 5; x++){
                    for (int z = 0; z < 5; z++){
                        BlockPos sculkPos = new BlockPos(pos.getX() + x - 2,pos.getY() - 1,pos.getZ() + z - 2);
                        BlockState sculkPosState = worldIn.getBlockState(sculkPos);
                        if (random.nextDouble() < 0.7 && sculkPosState.is(BlockTags.SCULK_REPLACEABLE)) {
                            worldIn.setBlock(sculkPos, Blocks.SCULK.defaultBlockState(),2);
                            worldIn.scheduleTick(sculkPos, sculkPosState.getBlock(), 8);
                            worldIn.playSound((Player)null, sculkPos, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + random.nextFloat() * 0.4F);
                            if (random.nextDouble() < 0.2) worldIn.addParticle(ParticleTypes.SCULK_SOUL, sculkPos.getX() + 0.5, sculkPos.getY() + 1.15, sculkPos.getZ() + 0.5,  0.0, 0.05, 0.0);
                            if (random.nextDouble() < 0.2) for (int i = 0; i < random.nextInt(5); i++)worldIn.addParticle(ParticleTypes.SCULK_CHARGE_POP, sculkPos.getX() + 0.5, sculkPos.getY() + 1.15, sculkPos.getZ() + 0.5,  0 + random.nextGaussian() * 0.02, 0.01 + random.nextGaussian() * 0.02, 0 + random.nextGaussian() * 0.02);
                        }
                    }
                }
                worldIn.playSound(null,pos,SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.AMBIENT, this.getSoundVolume(), this.getVoicePitch());
                worldIn.setBlockAndUpdate(pos, state.setValue(CAN_SUMMON, true));
                if (!player.isCreative()) stack.shrink(1);
                for (int i = 0; i < 100; ++i) {
                    double d0 = random.nextGaussian() * 0.02D;
                    double d1 = random.nextGaussian() * 0.02D;
                    double d2 = random.nextGaussian() * 0.02D;
                    worldIn.addParticle(ParticleTypes.SCULK_SOUL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, d0, d1, d2);
                }
                return InteractionResult.SUCCESS;
            } else return InteractionResult.FAIL;
        } else return InteractionResult.FAIL;
    }


    @Nullable
    @Override
    @Shadow
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }


}

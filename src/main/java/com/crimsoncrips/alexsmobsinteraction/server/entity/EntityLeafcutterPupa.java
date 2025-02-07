package com.crimsoncrips.alexsmobsinteraction.server.entity;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIVariant;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class EntityLeafcutterPupa extends ThrowableItemProjectile {

    public EntityLeafcutterPupa(EntityType p_i50154_1_, Level p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public EntityLeafcutterPupa(Level worldIn, LivingEntity throwerIn) {
        super(AMIEntityRegistry.LEAFCUTTER_PUPA.get(), throwerIn, worldIn);
    }

    public EntityLeafcutterPupa(Level worldIn, double x, double y, double z) {
        super(AMIEntityRegistry.LEAFCUTTER_PUPA.get(), x, y, z, worldIn);
    }

    public EntityLeafcutterPupa(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMIEntityRegistry.LEAFCUTTER_PUPA.get(), world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        Level world = this.level();
        BlockPos blockpos = new BlockPos((int) result.getLocation().x,(int)result.getLocation().y,(int)result.getLocation().z);
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.is(AMTagRegistry.LEAFCUTTER_PUPA_USABLE_ON) && world.getBlockState(blockpos.below()).is(AMTagRegistry.LEAFCUTTER_PUPA_USABLE_ON) && AlexsMobsInteraction.COMMON_CONFIG.THROWABLE_PUPI_ENABLED.get()) {
            world.playSound(this, blockpos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!world.isClientSide) {
                world.setBlock(blockpos, AMBlockRegistry.LEAFCUTTER_ANTHILL.get().defaultBlockState(), 3);
                world.setBlock(blockpos.below(), AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get().defaultBlockState(), 3);
                BlockEntity tileentity = world.getBlockEntity(blockpos);
                if (tileentity instanceof TileEntityLeafcutterAnthill) {
                    TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill)tileentity;
                    int j = Math.min(3, AMConfig.leafcutterAntColonySize);
                    int variant = world.random.nextBoolean() ? 1 : 2;
                    for(int k = 0; k < j; ++k) {
                        EntityLeafcutterAnt beeentity = new EntityLeafcutterAnt(AMEntityRegistry.LEAFCUTTER_ANT.get(), world);
                        if (AlexsMobsInteraction.COMMON_CONFIG.LEAFCUTTER_VARIANTS_ENABLED.get()){
                            ((AMIVariant) beeentity).setVariant(variant);
                        } else {
                            ((AMIVariant) beeentity).setVariant(1);
                        }
                        beeentity.setQueen(k == 0);
                        beehivetileentity.tryEnterHive(beeentity, false, 100);
                    }
                }
            }
        }
    }

    protected Item getDefaultItem() {
        return AMItemRegistry.LEAFCUTTER_ANT_PUPA.get();
    }
}

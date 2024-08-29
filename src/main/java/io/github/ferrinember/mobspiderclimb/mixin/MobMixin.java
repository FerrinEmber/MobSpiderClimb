package io.github.ferrinember.mobspiderclimb.mixin;

import io.github.ferrinember.mobspiderclimb.Config;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MobEntity.class)
public abstract class MobMixin extends LivingEntity {

    @Shadow public abstract PathNavigator getNavigation();

    @Shadow protected abstract PathNavigator createNavigation(World pLevel);

    @Shadow protected PathNavigator navigation;

    protected MobMixin(EntityType<? extends LivingEntity> pEntityType, World pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void addClimb(CallbackInfo info) {
        if (!this.level.isClientSide) {
            if (((Config.climbingMobsAllowlist.contains(this.getType()) && !Config.useAllowlistAsBanlist) || (!Config.climbingMobsAllowlist.contains(this.getType()) && Config.useAllowlistAsBanlist)) && (this.getTags().contains(Config.climbingTag) || Objects.equals(Config.climbingTag, ""))) {
                this.setClimbing(this.horizontalCollision);
                if (!(this.getNavigation() instanceof GroundPathNavigator)) {
                    this.navigation = this.createNavigation(this.level);
                }
            }
            else{
                if ((this.getNavigation() instanceof ClimberPathNavigator) && !(this.createNavigation(this.level) instanceof ClimberPathNavigator)) {
                    this.navigation = this.createNavigation(this.level);
                }
            }
        }
    }

    @Inject(method = "defineSynchedData", at = @At(value = "HEAD"))
    private void addData(CallbackInfo info) {
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(MobEntity.class, DataSerializers.BYTE);

    @Inject(method = "createNavigation", at = @At(value = "HEAD"), cancellable = true)
    private void addCreateNavigation(World pLevel, CallbackInfoReturnable<PathNavigator> cir) {
        if (((Config.climbingMobsAllowlist.contains(this.getType()) && !Config.useAllowlistAsBanlist) || (!Config.climbingMobsAllowlist.contains(this.getType()) && Config.useAllowlistAsBanlist)) && (this.getTags().contains(Config.climbingTag)) || Objects.equals(Config.climbingTag, "")) {
            cir.setReturnValue(new ClimberPathNavigator( (MobEntity) (Object) this,pLevel));
        }
        else {
            cir.setReturnValue(new GroundPathNavigator( (MobEntity) (Object) this,pLevel));
        }
    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean bClimbing) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (bClimbing) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

}

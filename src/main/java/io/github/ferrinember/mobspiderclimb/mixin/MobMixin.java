package io.github.ferrinember.mobspiderclimb.mixin;

import io.github.ferrinember.mobspiderclimb.Config;
import io.github.ferrinember.mobspiderclimb.util.ModifiedWallClimberNavigation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements Targeting {

    @Shadow public abstract PathNavigation getNavigation();

    @Shadow protected abstract PathNavigation createNavigation(Level pLevel);

    @Shadow protected PathNavigation navigation;


    protected MobMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void addClimb(CallbackInfo info) {
        if (!this.level().isClientSide) {
            if (((Config.climbingMobsAllowlist.contains(this.getType()) && !Config.useClimbAllowlistAsBanlist) || (!Config.climbingMobsAllowlist.contains(this.getType()) && Config.useClimbAllowlistAsBanlist)) && (this.getTags().contains(Config.climbingTag) || Objects.equals(Config.climbingTag, ""))) {
                this.setClimbing(this.horizontalCollision);
                if (!(this.getNavigation() instanceof ModifiedWallClimberNavigation)) {
                    this.navigation = this.createNavigation(this.level());
                }
            }
            else{
                if ((this.getNavigation() instanceof ModifiedWallClimberNavigation) && !(this.createNavigation(this.level()) instanceof ModifiedWallClimberNavigation)) {
                    this.navigation = this.createNavigation(this.level());
                }
            }
            if (this.isClimbing()){
                if (this.tickCount % 20 == 1) {
                    this.swing(InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    @Inject(method = "defineSynchedData", at = @At(value = "HEAD"))
    private void addData(CallbackInfo info) {
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);

    @Inject(method = "createNavigation", at = @At(value = "HEAD"), cancellable = true)
    private void addCreateNavigation(Level pLevel, CallbackInfoReturnable<PathNavigation> cir) {
        if (((Config.climbingMobsAllowlist.contains(this.getType()) && !Config.useClimbAllowlistAsBanlist) || (!Config.climbingMobsAllowlist.contains(this.getType()) && Config.useClimbAllowlistAsBanlist)) && (this.getTags().contains(Config.climbingTag)) || Objects.equals(Config.climbingTag, "")) {
            cir.setReturnValue(new ModifiedWallClimberNavigation( (Mob) (Object) this,pLevel));
        }
        else {
            cir.setReturnValue(new GroundPathNavigation( (Mob) (Object) this,pLevel));
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

package io.github.ferrinember.mobspiderclimb.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

import javax.annotation.Nullable;

public class ModifiedWallClimberNavigation extends GroundPathNavigation {
    @Nullable
    private BlockPos pathToPosition;
    boolean climbAdjusted;

    public ModifiedWallClimberNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }

    public Path createPath(BlockPos pPos, int pAccuracy) {
        this.pathToPosition = pPos;
        return super.createPath(pPos, pAccuracy);
    }

    public Path createPath(Entity pEntity, int pAccuracy) {
        this.pathToPosition = pEntity.getOnPos().offset(0,1,0);
        return this.createPath(this.pathToPosition, pAccuracy);
    }

    public boolean moveTo(Entity pEntity, double pSpeed) {
        Path path = this.createPath((Entity)pEntity, 0);
        if (path != null) {
            return this.moveTo(path, pSpeed);
        } else {
            this.pathToPosition = pEntity.getOnPos().offset(0,1,0);
            this.speedModifier = pSpeed;
            return true;
        }
    }

    public void tick() {
        BlockPos mobPos = this.mob.blockPosition();
        boolean climbableDetected = false;
        if (!this.isDone()) {
            super.tick();
        } else if (this.pathToPosition != null) {
            for (BlockPos adjPos : BlockPos.betweenClosed(mobPos.offset(-1, 0, -1),mobPos.offset(1, (int) Math.ceil(this.mob.getBbHeight()), 1))) {
                if ((adjPos.getX() != mobPos.getX() || adjPos.getZ() != mobPos.getZ()) && this.level.getBlockState(adjPos).blocksMotion()) {
                    climbableDetected = true;
                    break;
                }
            }
            if (!climbableDetected || this.pathToPosition.closerToCenterThan(this.mob.position(), Math.max((double)this.mob.getBbWidth(), 1.0)) || this.mob.getY() > (double)this.pathToPosition.getY() && BlockPos.containing((double)this.pathToPosition.getX(), this.mob.getY(), (double)this.pathToPosition.getZ()).closerToCenterThan(this.mob.position(), Math.max((double)this.mob.getBbWidth(), 1.0))) {
                this.pathToPosition = null;
            }
            else {
                this.mob.setYRot(Math.round(this.mob.getYRot()/90)*90);
                this.mob.getMoveControl().setWantedPosition((double)this.pathToPosition.getX(), (double)this.pathToPosition.getY(), (double)this.pathToPosition.getZ(), this.speedModifier);
            }
        }

    }
}

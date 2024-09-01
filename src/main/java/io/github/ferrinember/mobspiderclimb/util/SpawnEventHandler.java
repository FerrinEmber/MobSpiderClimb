package io.github.ferrinember.mobspiderclimb.util;

import io.github.ferrinember.mobspiderclimb.Config;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpawnEventHandler {
    @SubscribeEvent
    public void addFloatGoal(MobSpawnEvent event) {
        if ((Config.floatingMobsAllowlist.contains(event.getEntity().getType()) && !Config.useFloatAllowlistAsBanlist) || (!Config.floatingMobsAllowlist.contains(event.getEntity().getType()) && Config.useFloatAllowlistAsBanlist)) {
            event.getEntity().goalSelector.addGoal(1, new FloatGoal(event.getEntity()));
        }
    }
}


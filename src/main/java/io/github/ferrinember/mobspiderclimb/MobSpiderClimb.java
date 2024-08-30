package io.github.ferrinember.mobspiderclimb;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MobSpiderClimb.MOD_ID)
public class MobSpiderClimb
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "mobspiderclimb";
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public MobSpiderClimb()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}

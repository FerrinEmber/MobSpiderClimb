package io.github.ferrinember.mobspiderclimb;


import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mod.EventBusSubscriber(modid = MobSpiderClimb.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CLIMBING_MOBS = BUILDER
            .comment("A list of mobs (hostile, passive, you name it) to allow to use climbing behavior.\n" +
                    "Only actually works with mobs that natively use the default ground navigation AI, which thankfully is most mobs, but flying mobs like phantoms, swimming mobs like guardians, and bouncing mobs like slimes won't be affected if added.\n" +
                    "Modded mobs may work as well, just be sure to use lead with the mod namespace! I.e. \"modname:mobname\", like you would use if you created it with the 'summon' command. If it doesn't work, it's likely that the mob doesn't use the ground navigation AI.\n" +
                    "If you notice your config resetting to default, that probably means you've given it invalid mob names. Double check spelling, and ensure you that \"modname:mobname\" also works with the summon command!")
            .defineList("climbingMobsAllowlist", ImmutableList.of("minecraft:zombie", "minecraft:drowned", "minecraft:husk", "minecraft:zombie_villager"), Config::validateMobName);

    private static final ForgeConfigSpec.ConfigValue<String> CLIMBING_TAG = BUILDER
            .comment("\nNBTTag required for mob to use climbing behavior. Leave as empty double quotes (default) to not require a tag. For example, a value of \"canClimb\" would mean that only mobs with the \"canClimb\" nbt tag could climb, such as those summoned with the command...\n" +
                    "/summon minecraft:zombie ~ ~ ~ {Tags: [\"canClimb\"]}")
            .define("climbingTag", "");

    private static final ForgeConfigSpec.BooleanValue USE_ALLOWLIST_AS_BANLIST = BUILDER
            .comment("\nUses the allowlist as a banlist, attempting to add climbing behavior to all mobs EXCEPT those listed in climbingMobsAllowlist.")
            .define("useAllowlistAsBanlist", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static Set<EntityType> climbingMobsAllowlist;
    public static String climbingTag;
    public static Boolean useAllowlistAsBanlist;

    private static boolean validateMobName(final Object obj)
    {
        return obj instanceof String && ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(obj.toString()));
    }

    @SubscribeEvent
    static void onLoad(final ModConfig.ModConfigEvent event)
    {
        climbingMobsAllowlist = CLIMBING_MOBS.get().stream()
                .map(mobName -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mobName)))
                .collect(Collectors.toSet());
        climbingTag = CLIMBING_TAG.get();
        useAllowlistAsBanlist = USE_ALLOWLIST_AS_BANLIST.get();
    }
}

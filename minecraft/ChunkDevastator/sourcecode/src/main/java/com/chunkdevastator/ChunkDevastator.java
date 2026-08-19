package com.chunkdevastator;

import com.chunkdevastator.doom.DoomManager;
import com.chunkdevastator.net.DoomPayloads;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

/**
 * Mod entrypoint (server side). SPEC 2.1: any damage to a survival/adventure
 * player arms the doom - except void damage and /kill. Creative/spectator exempt.
 */
public class ChunkDevastator implements ModInitializer {
    @Override
    public void onInitialize() {
        DoomPayloads.register();

        ServerLivingEntityEvents.AFTER_DAMAGE.register(ChunkDevastator::onAfterDamage);
        ServerTickEvents.END_SERVER_TICK.register(server -> DoomManager.get(server).tick());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> DoomManager.get(server).syncAll(handler.getPlayer()));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> DoomManager.get(server).load()); // worlds exist only after STARTED (overworld() is null during STARTING)
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> DoomManager.get(server).save());
    }

    private static void onAfterDamage(LivingEntity entity, DamageSource source, float baseDamage, float damageTaken, boolean blocked) {
        if (damageTaken <= 0.0F || entity.isRemoved()) {
            return;
        }
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (isExemptSource(source)) {
            return;
        }
        ServerLevel level = (ServerLevel) player.level();
        DoomManager.get(level.getServer()).arm(level, level.getChunkAt(player.blockPosition()).getPos());
    }

    private static boolean isExemptSource(DamageSource source) {
        // Void damage and /kill (generic kill) never arm the doom.
        return source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL);
    }
}
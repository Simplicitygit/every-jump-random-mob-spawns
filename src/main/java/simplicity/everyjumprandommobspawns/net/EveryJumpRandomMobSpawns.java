package simplicity.everyjumprandommobspawns.net;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EveryJumpRandomMobSpawns implements ModInitializer {
	public static final String MOD_ID = "every-jump-random-mob-spawns";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Map<UUID, Boolean> wasOnGround = new HashMap<>();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				boolean before = wasOnGround.getOrDefault(player.getUUID(), true);
				boolean now = player.onGround();

				if (before && !now && player.getDeltaMovement().y > 0) {
					EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getRandom(player.getRandom())
							.<EntityType<?>>map(holder -> holder.value())
							.orElse(EntityTypes.PIG);

					type.spawn(player.level(), player.blockPosition(), EntitySpawnReason.COMMAND);
				}

				wasOnGround.put(player.getUUID(), now);
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}

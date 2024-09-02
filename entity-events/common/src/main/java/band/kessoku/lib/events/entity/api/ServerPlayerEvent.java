/*
 * Copyright (c) 2024 KessokuTeaTime
 *
 * Licensed under the GNU Lesser General Pubic License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package band.kessoku.lib.events.entity.api;

import band.kessoku.lib.event.api.Event;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPlayerEvent {
    /**
     * An event that is called when the data from an old player is copied to a new player.
     *
     * <p>This event is typically called before a player is completely respawned.
     * Mods may use this event to copy old player data to a new player.
     */
    public static final Event<CopyFrom> COPY_FROM = Event.of(callbacks -> (oldPlayer, newPlayer, alive) -> {
        for (CopyFrom callback : callbacks) {
            callback.copyFromPlayer(oldPlayer, newPlayer, alive);
        }
    });

    /**
     * An event that is called after a player has been respawned.
     *
     * <p>Mods may use this event for reference clean up on the old player.
     */
    public static final Event<ServerPlayerEvent.AfterRespawn> AFTER_RESPAWN = Event.of(callbacks -> (oldPlayer, newPlayer, alive) -> {
        for (AfterRespawn callback : callbacks) {
            callback.afterRespawn(oldPlayer, newPlayer, alive);
        }
    });

    /**
     * An event that is called when a player takes fatal damage.
     *
     * @deprecated Use the more general {@link ServerLivingEntityEvent#ALLOW_DEATH} event instead and check for {@code instanceof ServerPlayerEntity}.
     */
    @Deprecated
    public static final Event<AllowDeath> ALLOW_DEATH = Event.of(callbacks -> (player, damageSource, damageAmount) -> {
        for (AllowDeath callback : callbacks) {
            if (!callback.allowDeath(player, damageSource, damageAmount)) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface CopyFrom {
        /**
         * Called when player data is copied to a new player.
         *
         * @param oldPlayer the old player
         * @param newPlayer the new player
         * @param alive whether the old player is still alive
         */
        void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
    }

    @FunctionalInterface
    public interface AfterRespawn {
        /**
         * Called after player a has been respawned.
         *
         * @param oldPlayer the old player
         * @param newPlayer the new player
         * @param alive whether the old player is still alive
         */
        void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
    }

    /**
     * @deprecated Use the more general {@link ServerLivingEntityEvent#ALLOW_DEATH} event instead and check for {@code instanceof ServerPlayerEntity}.
     */
    @Deprecated
    @FunctionalInterface
    public interface AllowDeath {
        /**
         * Called when a player takes fatal damage (before totems of undying can take effect).
         *
         * @param player the player
         * @param damageSource the fatal damage damageSource
         * @param damageAmount the damageAmount of damage that has killed the player
         * @return true if the death should go ahead, false otherwise.
         */
        boolean allowDeath(ServerPlayerEntity player, DamageSource damageSource, float damageAmount);
    }

    private ServerPlayerEvent() {
    }

    static {
        // Forward general living entity event to (older) player-specific event.
        ServerLivingEntityEvent.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof ServerPlayerEntity player) {
                return ServerPlayerEvent.ALLOW_DEATH.invoker().allowDeath(player, damageSource, damageAmount);
            }

            return true;
        });
    }
}

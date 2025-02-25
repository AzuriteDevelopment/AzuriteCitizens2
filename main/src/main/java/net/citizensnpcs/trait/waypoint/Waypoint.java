package net.citizensnpcs.trait.waypoint;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.persistence.PersistenceLoader;
import net.citizensnpcs.api.util.Messaging;
import net.citizensnpcs.trait.waypoint.triggers.DelayTrigger;
import net.citizensnpcs.trait.waypoint.triggers.WaypointTrigger;
import net.citizensnpcs.trait.waypoint.triggers.WaypointTriggerRegistry;
import net.citizensnpcs.util.Messages;

/**
 * Represents a {@link Location} with a number of {@link WaypointTriggers} that activate on reaching the location.
 */
public class Waypoint implements Locatable {
    @Persist(required = true)
    private Location location;
    @Persist
    private List<WaypointTrigger> triggers;

    /**
     * For persistence - avoid using otherwise.
     */
    public Waypoint() {
    }

    public Waypoint(Location at) {
        location = at;
    }

    public void addTrigger(WaypointTrigger trigger) {
        if (triggers == null) {
            triggers = Lists.newArrayList();
        }
        triggers.add(trigger);
    }

    public void describeTriggers(CommandSender sender) {
        String base = "";
        for (WaypointTrigger trigger : getTriggers()) {
            base += "\n    - " + trigger.description();
        }
        Messaging.sendTr(sender, Messages.WAYPOINT_TRIGGER_LIST, base);
    }

    /**
     * Returns the distance in blocks to another waypoint.
     */
    public double distance(Waypoint dest) {
        return location.distance(dest.location);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Waypoint other = (Waypoint) obj;
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (triggers == null) {
            if (other.triggers != null) {
                return false;
            }
        } else if (!triggers.equals(other.triggers)) {
            return false;
        }
        return true;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    public List<WaypointTrigger> getTriggers() {
        return triggers == null ? Collections.<WaypointTrigger> emptyList() : triggers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((location == null) ? 0 : location.hashCode());
        return prime * result + ((triggers == null) ? 0 : triggers.hashCode());
    }

    /**
     * Runs waypoint triggers for the given NPC.
     */
    public void onReach(NPC npc) {
        if (triggers == null)
            return;
        runTriggers(npc, 0);
    }

    private void runTriggers(final NPC npc, int start) {
        for (int i = start; i < triggers.size(); i++) {
            WaypointTrigger trigger = triggers.get(i);
            trigger.onWaypointReached(npc, location);
            if (!(trigger instanceof DelayTrigger))
                continue;
            int delay = ((DelayTrigger) trigger).getDelay();
            if (delay <= 0)
                continue;
            final int newStart = i + 1;
            Bukkit.getScheduler().scheduleSyncDelayedTask(CitizensAPI.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    runTriggers(npc, newStart);
                }
            }, delay);
            break;
        }
    }

    static {
        PersistenceLoader.registerPersistDelegate(WaypointTrigger.class, WaypointTriggerRegistry.class);
    }
}
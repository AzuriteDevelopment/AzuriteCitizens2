package net.citizensnpcs.nms.v1_8_R3.util;

import net.citizensnpcs.Settings.Setting;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.nms.v1_8_R3.entity.EntityHumanNPC;
import net.citizensnpcs.util.NMS;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PlayerlistTrackerEntry extends EntityTrackerEntry {
    private EntityPlayer lastUpdatedPlayer;

    public PlayerlistTrackerEntry(Entity entity, int i, int j, boolean flag) {
        super(entity, i, j, flag);
    }

    public PlayerlistTrackerEntry(EntityTrackerEntry entry) {
        this(getTracker(entry), getB(entry), getC(entry), getU(entry));
    }

    public boolean isUpdating() {
        return lastUpdatedPlayer != null;
    }

    public void updateLastPlayer() {
        if (lastUpdatedPlayer == null)
            return;
        final Entity tracker = getTracker(this);
        final EntityPlayer entityplayer = lastUpdatedPlayer;
        NMS.sendTabListAdd(entityplayer.getBukkitEntity(), (Player) tracker.getBukkitEntity());
        lastUpdatedPlayer = null;
        if (!Setting.DISABLE_TABLIST.asBoolean())
            return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(CitizensAPI.getPlugin(), () -> NMS.sendTabListRemove(entityplayer.getBukkitEntity(), (Player) tracker.getBukkitEntity()));
    }

    @Override
    public void updatePlayer(final EntityPlayer entityplayer, int count, boolean shouldUpdate) {
        // prevent updates to NPC "viewers"
        if (entityplayer instanceof EntityHumanNPC)
            return;
        lastUpdatedPlayer = entityplayer;
        super.updatePlayer(entityplayer, count, shouldUpdate);
        lastUpdatedPlayer = null;
    }

    @Override
    public void updatePlayer(final EntityPlayer entityplayer) {
        // prevent updates to NPC "viewers"
        if (entityplayer instanceof EntityHumanNPC)
            return;
        lastUpdatedPlayer = entityplayer;
        super.updatePlayer(entityplayer);
        lastUpdatedPlayer = null;
    }

    private static int getB(EntityTrackerEntry entry) {
        try {
            return (Integer) B.get(entry);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getC(EntityTrackerEntry entry) {
        try {
            return (Integer) C.get(entry);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static Entity getTracker(EntityTrackerEntry entry) {
        try {
            return (Entity) TRACKER.get(entry);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean getU(EntityTrackerEntry entry) {
        try {
            return (Boolean) U.get(entry);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Field B = NMS.getField(EntityTrackerEntry.class, "b");
    private static Field C = NMS.getField(EntityTrackerEntry.class, "c");
    private static Field TRACKER = NMS.getField(EntityTrackerEntry.class, "tracker");
    private static Field U = NMS.getField(EntityTrackerEntry.class, "u");
    private static Method update = NMS.getMethod(EntityTrackerEntry.class, "updatePlayer", false, EntityPlayer.class, int.class, boolean.class);
}

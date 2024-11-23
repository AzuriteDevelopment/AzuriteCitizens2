package net.citizensnpcs.trait;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.util.NMS;
import net.citizensnpcs.util.Util;

@TraitName("scoreboardtrait")
public class ScoreboardTrait extends Trait {
    @Persist
    private ChatColor color;
    private ChatColor previousGlowingColor;
    @Persist
    private final Set<String> tags = new HashSet<String>();

    public ScoreboardTrait() {
        super("scoreboardtrait");
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void apply(Team team, boolean nameVisibility) {
        Set<String> newTags = new HashSet<String>(tags);

        if (!SUPPORT_TEAM_SETOPTION) {
            NMS.setTeamNameTagVisible(team, nameVisibility);
        }

        if (npc.data().has(NPC.GLOWING_COLOR_METADATA)) {
            color = ChatColor.valueOf(npc.data().get(NPC.GLOWING_COLOR_METADATA));
            npc.data().remove(NPC.GLOWING_COLOR_METADATA);
        }
        if (color != null) {
            if (SUPPORT_GLOWING_COLOR && Util.getMinecraftRevision().contains("1_12_R1")) {
                SUPPORT_GLOWING_COLOR = false;
            }

            if (team.getPrefix() == null || team.getPrefix().length() == 0 || previousGlowingColor == null
                    || (previousGlowingColor != null
                            && !team.getPrefix().equals(previousGlowingColor.toString()))) {
                team.setPrefix(color.toString());
                previousGlowingColor = color;
            }

        }
        Util.sendTeamPacketToOnlinePlayers(team, 2);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    private static boolean SUPPORT_GLOWING_COLOR = true;
    private static boolean SUPPORT_TAGS = false;
    private static boolean SUPPORT_TEAM_SETOPTION = true;
}

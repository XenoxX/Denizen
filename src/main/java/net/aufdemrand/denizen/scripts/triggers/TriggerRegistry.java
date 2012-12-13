package net.aufdemrand.denizen.scripts.triggers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.interfaces.DenizenRegistry;
import net.aufdemrand.denizen.interfaces.RegistrationableInstance;
import net.aufdemrand.denizen.scripts.triggers.core.ClickTrigger;
import net.aufdemrand.denizen.scripts.triggers.core.DamageTrigger;
import net.citizensnpcs.api.npc.NPC;

public class TriggerRegistry implements DenizenRegistry {

    public Denizen denizen;

    public TriggerRegistry(Denizen denizen) {
        this.denizen = denizen;
    }

    private Map<String, AbstractTrigger> instances = new HashMap<String, AbstractTrigger>();
    private Map<Class<? extends AbstractTrigger>, String> classes = new HashMap<Class<? extends AbstractTrigger>, String>();

    @Override
    public boolean register(String triggerName, RegistrationableInstance instance) {
        this.instances.put(triggerName.toUpperCase(), (AbstractTrigger) instance);
        this.classes.put(((AbstractTrigger) instance).getClass(), triggerName.toUpperCase());
        return true;
    }

    @Override
    public Map<String, AbstractTrigger> list() {
        return instances;
    }

    @Override
    public AbstractTrigger get(String triggerName) {
        if (instances.containsKey(triggerName.toUpperCase())) return instances.get(triggerName.toUpperCase());
        else return null;
    }

    @Override
    public <T extends RegistrationableInstance> T get(Class<T> clazz) {
        if (classes.containsKey(clazz)) return (T) clazz.cast(instances.get(classes.get(clazz)));
        else return null;
    }

    @Override
    public void registerCoreMembers() {
        new ClickTrigger().activate().as("Click").withOptions(true, 2.0, CooldownType.PLAYER);
        new DamageTrigger().activate().as("Damage").withOptions(true, 0.5, CooldownType.NPC);
        denizen.getDebugger().echoApproval("Loaded core triggers: " + instances.keySet().toString());
    }

    /*
     * Trigger cool-downs are used by Denizen internally in intervals specified by the config.
     * Not to be confused with Script Cool-downs. 
     */

    public enum CooldownType { NPC, PLAYER }

    Map<Integer, Map<String, Long>> npcCooldown = new ConcurrentHashMap<Integer, Map<String,Long>>();
    Map<String, Map<String, Long>> playerCooldown = new ConcurrentHashMap<String, Map<String,Long>>();

    public boolean checkCooldown(NPC npc, Player player, AbstractTrigger triggerClass) {
        // Check npcCooldown
        if (!npcCooldown.containsKey(Integer.valueOf(npc.getId()))) return true;
        else if (!npcCooldown.get(Integer.valueOf(npc.getId())).containsKey(triggerClass.name)) return true;
        else if (System.currentTimeMillis() > npcCooldown.get(Integer.valueOf(npc.getId())).get(triggerClass.name)) return true;
        // Check playerCooldown
        if (!playerCooldown.containsKey(player.getName() + "/" + npc.getId())) return true;
        else if (!playerCooldown.get(player.getName() + "/" + npc.getId()).containsKey(triggerClass.name)) return true;
        else if (System.currentTimeMillis() > playerCooldown.get(player.getName() + "/" + npc.getId()).get(triggerClass.name)) return true;
        return false;
    }

    public void setCooldown(NPC npc, Player player, AbstractTrigger triggerClass, double seconds, CooldownType type) {
        if (type == CooldownType.NPC) { 
            Map<String, Long> triggerMap = new HashMap<String, Long>();
            triggerMap.put(triggerClass.name, System.currentTimeMillis() + Long.valueOf((long) (seconds * 1000)));
            npcCooldown.put(Integer.valueOf(npc.getId()), triggerMap);
            return;

        } else if (type == CooldownType.PLAYER) {
            Map<String, Long> triggerMap = new HashMap<String, Long>();
            triggerMap.put(triggerClass.name, System.currentTimeMillis() + Long.valueOf((long) (seconds * 1000)));
            playerCooldown.put(player.getName() + "/" + npc.getId(), triggerMap);
            return;
        }
    }

}
package net.aufdemrand.denizen.scripts.commands;

import java.util.HashMap;
import java.util.Map;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.interfaces.DenizenRegistry;
import net.aufdemrand.denizen.interfaces.RegistrationableInstance;
import net.aufdemrand.denizen.scripts.commands.core.AnnounceCommand;
import net.aufdemrand.denizen.scripts.commands.core.CastCommand;
import net.aufdemrand.denizen.scripts.commands.core.CooldownCommand;
import net.aufdemrand.denizen.scripts.commands.core.DisengageCommand;
import net.aufdemrand.denizen.scripts.commands.core.EngageCommand;
import net.aufdemrand.denizen.scripts.commands.core.ExecuteCommand;
import net.aufdemrand.denizen.scripts.commands.core.FailCommand;
import net.aufdemrand.denizen.scripts.commands.core.FeedCommand;
import net.aufdemrand.denizen.scripts.commands.core.FinishCommand;
import net.aufdemrand.denizen.scripts.commands.core.FlagCommand;
import net.aufdemrand.denizen.scripts.commands.core.HealCommand;
import net.aufdemrand.denizen.scripts.commands.core.IfCommand;
import net.aufdemrand.denizen.scripts.commands.core.ListenCommand;
import net.aufdemrand.denizen.scripts.commands.core.LookcloseCommand;
import net.aufdemrand.denizen.scripts.commands.core.ModifyBlockCommand;
import net.aufdemrand.denizen.scripts.commands.core.NarrateCommand;
import net.aufdemrand.denizen.scripts.commands.core.PlaySoundCommand;
import net.aufdemrand.denizen.scripts.commands.core.SwitchCommand;
import net.aufdemrand.denizen.scripts.commands.core.TriggerCommand;

public class CommandRegistry implements DenizenRegistry {

    public Denizen denizen;

    public CommandRegistry(Denizen denizen) {
        this.denizen = denizen;
    }

    private Map<String, AbstractCommand> instances = new HashMap<String, AbstractCommand>();
    private Map<Class<? extends AbstractCommand>, String> classes = new HashMap<Class<? extends AbstractCommand>, String>();
    
    @Override
    public boolean register(String commandName, RegistrationableInstance commandInstance) {
        this.instances.put(commandName.toUpperCase(), (AbstractCommand) commandInstance);
        this.classes.put(((AbstractCommand) commandInstance).getClass(), commandName.toUpperCase());
        return true;
    }
    
    @Override
    public Map<String, AbstractCommand> list() {
        return instances;
    }

    @Override
    public AbstractCommand get(String commandName) {
        if (instances.containsKey(commandName.toUpperCase())) return instances.get(commandName.toUpperCase());
        else return null;
    }

    @Override
    public <T extends RegistrationableInstance> T get(Class<T> clazz) {
        if (classes.containsKey(clazz)) return (T) clazz.cast(instances.get(classes.get(clazz)));
        else return null;
    }
    
    @Override
    public void registerCoreMembers() {
        new CooldownCommand().activate().as("COOLDOWN").withOptions("[DURATION:#] (GLOBAL) (PLAYER:player_name) ('SCRIPT:name of script')", 1);
        new EngageCommand().activate().as("ENGAGE").withOptions("(DURATION:#) (NPCID:#)", 0);
        new DisengageCommand().activate().as("DISENGAGE").withOptions("(NPCID:#)", 0);
        new FlagCommand().activate().as("FLAG").withOptions("(DENIZEN|PLAYER|GLOBAL) [[NAME([#])]:[VALUE]|[NAME]:[FLAG_ACTION]:(VALUE)]", 1);
        new FinishCommand().activate().as("FINISH").withOptions("(PLAYER:player_name)", 0);
        new FailCommand().activate().as("FAIL").withOptions("(PLAYER:player_name)", 0);
        new IfCommand().activate().as("IF").withOptions("(!)[COMPARABLE] (OPERATOR) (COMPARED_TO) (BRIDGE) (...) [COMMAND] (ELSE) (COMMAND) // see documentation.", 2);
        new TriggerCommand().activate().as("TRIGGER").withOptions("[NAME:Trigger_Name] [(TOGGLE:TRUE|FALSE)|(COOLDOWN:#.#)|(RADIUS:#)]", 2);
        new ModifyBlockCommand().activate().as("MODIFYBLOCK").withOptions("[LOCATION:x,y,z,world] [MATERIAL:DATA VALUE] (RADIUS:##) (HEIGHT:##) (DEPTH:##)", 2);
        new CastCommand().activate().as("CAST").withOptions("TYPE:PotionEffectType (DURATION:#) (POWER:#) (TARGET:NPC|PLAYER)", 1);
        new FeedCommand().activate().as("FEED").withOptions("(AMT:#) (TARGET:NPC|PLAYER)", 0);
        new HealCommand().activate().as("HEAL").withOptions("(AMT:#) (TARGET:NPC|PLAYER)", 0);
        new SwitchCommand().activate().as("SWITCH").withOptions("[LOCATION:x,y,z,world] (STATE:ON|OFF|TOGGLE) (DURATION:#)", 1);
        new LookcloseCommand().activate().as("LOOKCLOSE").withOptions("[TOGGLE:TRUE|FALSE] (RANGE:#.#) (REALISTIC)", 1);
        new AnnounceCommand().activate().as("ANNOUNCE").withOptions("['Text to announce']", 1);
        new NarrateCommand().activate().as("NARRATE").withOptions("(PLAYER:player_name) ['Text to narrate']", 1);
        new ExecuteCommand().activate().as("EXECUTE").withOptions("[AS_PLAYER|AS_SERVER|AS_NPC|AS_OP] ['Bukkit Command']", 2);
        new PlaySoundCommand().activate().as("PLAYSOUND").withOptions("[LOCATION:x,y,z,world] [SOUND:NAME] (VOLUME:#) (PITCH:#)", 2);
        new ListenCommand().activate().as("LISTEN").withOptions("[Listener_Type] [ID:ListenerID] [Listener Arguments] // see documentation.", 2);
        
        denizen.getDebugger().echoApproval("Loaded core commands: " + instances.keySet().toString());
    }


}
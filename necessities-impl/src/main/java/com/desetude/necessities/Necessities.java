package com.desetude.necessities;

import co.aikar.commands.BukkitCommandManager;
import com.desetude.modularity.additionalmodules.BukkitGuiceModule;
import com.desetude.modularity.loader.ModuleLoader;
import com.desetude.necessities.configurate.Config;
import com.desetude.necessities.configurate.ConfigFactory;
import com.desetude.necessities.lifecycle.InitializationEvent;
import com.desetude.necessities.module.ConfigurateModuleConfig;
import com.desetude.necessities.module.ModulesConfig;
import com.desetude.necessities.module.NecessitiesGuiceModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Necessities extends JavaPlugin {

    private ConfigFactory configFactory;
    private BukkitCommandManager commandManager;

    @Override
    public void onEnable() {
        load();
    }

    @Override
    public void onDisable() {
        save();
    }

    public void reload() {
        //TODO: async
        save();
        load();
    }

    private void save() {
        this.configFactory.saveConfigs();
    }

    public void load() {
        this.configFactory = new ConfigFactory(getDataFolder());

        Config<ModulesConfig> modulesConfig = this.configFactory.createMapping("modules", ModulesConfig.class);
        modulesConfig.load();

        new ModuleLoader()
                .addInjectorModules(
                        new BukkitGuiceModule(this),
                        new NecessitiesGuiceModule(getLogger(), this.configFactory, new BukkitCommandManager(this))
                ).setConfig(new ConfigurateModuleConfig(modulesConfig))
                .load();

        Bukkit.getPluginManager().callEvent(new InitializationEvent());
    }

}
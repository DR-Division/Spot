package com.light.spot;

import com.light.spot.Command.SpotCommand;
import com.light.spot.Manager.SpotManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Spot extends JavaPlugin {

    @Override
    public void onEnable(){
        getLogger().info("스팟 플러그인이 활성화 되었습니다.");
        getCommand("spot").setExecutor(new SpotCommand(this));
        SpotManager.getInstance().turnOn();
    }

    @Override
    public void onDisable(){
        getLogger().info("스팟 플러그인이 비활성화 되었습니다.");
        SpotManager.getInstance().turnOff();
    }
}

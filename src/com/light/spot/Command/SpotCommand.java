package com.light.spot.Command;

import com.light.spot.Manager.SpotManager;
import com.light.spot.Spot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpotCommand implements CommandExecutor {

    private Spot Plugin;

    public SpotCommand(Spot Plugin){
        this.Plugin = Plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            if (SpotManager.getInstance().isOn(p)){
                p.sendMessage("§6[!] §c스팟이 비활성화 되었습니다.");
                SpotManager.getInstance().removePlayer(p);
            }
            else{
                p.sendMessage("§6[!] §b스팟이 활성화 되었습니다.");
                SpotManager.getInstance().addPlayer(p, UUID.randomUUID());
            }
            return true;
        }
        return false;
    }
}

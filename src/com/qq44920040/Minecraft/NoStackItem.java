package com.qq44920040.Minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class NoStackItem extends JavaPlugin implements Listener {
    private String Msg;
    private HashMap<Integer,Integer> StackInfo = new HashMap<>();

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"config.yml");
        if (!(file.exists())){
            saveDefaultConfig();
        }
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        ReloadConfig();
        super.onEnable();
    }



    @EventHandler
    public void PlayerCloseInventory(InventoryCloseEvent event){
        Inventory inv = event.getPlayer().getInventory();
        System.out.println(inv.getType());
        if (inv.getType()== InventoryType.PLAYER){
            HashMap<Integer,Integer> TempInfo = new HashMap<>();
            for (int a=0;a<=35;a++){
                ItemStack itemStack = inv.getItem(a);
                if (itemStack!=null&&itemStack.getType()!=Material.AIR){
                    int Itemnum = itemStack.getTypeId();
                    if (StackInfo.containsKey(Itemnum)){
                        if (TempInfo.containsKey(Itemnum)){
                            if (TempInfo.get(Itemnum)+1>StackInfo.get(Itemnum)){
                                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation().add(4.0,0.0,0.0),itemStack);
                                inv.setItem(a,null);
                                ((Player)event.getPlayer()).sendMessage(Msg);
                            }else {
                                TempInfo.replace(Itemnum,TempInfo.get(Itemnum)+1);
                            }
                        }else {
                            TempInfo.put(Itemnum,1);
                        }
                    }
                }
            }

        }
    }

    @EventHandler
    public void PlayerPickItem(PlayerPickupItemEvent event){
        Inventory inv = event.getPlayer().getInventory();
        int ItemId = event.getItem().getItemStack().getTypeId();
        int NumStack=1;
        if (StackInfo.containsKey(ItemId)){
            int ConfigStack = StackInfo.get(ItemId);
            for (ItemStack I:inv){
                if (I!=null&&I.getType()!= Material.AIR){
                    if (I.getTypeId()==ItemId){
                        NumStack++;
                    }
                }
            }
            if (NumStack>ConfigStack){
                event.getPlayer().sendMessage(Msg);
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()&&label.equalsIgnoreCase("nsi")&&args.length==1&&args[0].equalsIgnoreCase("reload")){
            ReloadConfig();
            sender.sendMessage("[NoStackItem]配置项重载成功");
        }
        return super.onCommand(sender, command, label, args);
    }

    private void ReloadConfig() {
        reloadConfig();
        Msg = getConfig().getString("Msg");
        Set<String> mines = getConfig().getConfigurationSection("NoStackItem").getKeys(false);
        for (String temp : mines) {
            int StackNum = getConfig().getInt("NoStackItem." + temp + ".StackNum");
            System.out.println(temp);
            StackInfo.put(Integer.parseInt(temp), StackNum);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}

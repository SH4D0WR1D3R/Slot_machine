package me.shadow.me.shadow.Me;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public final class shadow extends JavaPlugin implements Listener {

    List<Inventory> invs = new ArrayList<Inventory>();
    public static ItemStack[] contents;
    // this holds all of the different items
    private int itemIndex = 0;
    // this is to record the current index of the item

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        // this registers the event
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e){
        Player player = e.getPlayer(); // gets the player entity details
        Block block = e.getClickedBlock(); // gets block details for the block clicked
        if (block.getState() instanceof Sign){ // if the block is a sign
            Sign sign = (Sign) block.getState(); // assign the sign entity to a variable
            if (sign.getLine(1).equalsIgnoreCase("SLOT") && sign.getLine(2).equalsIgnoreCase("MACHINE")){
                slotMachine(player); // checks the sign is correct and runs the slotmachine code
            }
        }
    }

    public void slotMachine(Player player){
        ItemStack fee = new ItemStack(Material.DIAMOND);
        // item that you need to use to pay
        fee.setAmount(1);
        // costs 1 diamond
        if (player.getInventory().getItemInMainHand().isSimilar(fee)) {
            // if player holding diamond
            player.getInventory().removeItem(fee);
            // removes the number of items
            //spin

            spin(player, 1);
            //return true;
        }
        player.sendMessage(ChatColor.DARK_RED + "You need a diamond");
            // error message
    }

    public void shuffle(Inventory inv) {
        if (contents == null) {
            // if no items in the items array
            ItemStack[] items = new ItemStack[28];
            // sets array
            items[0] = new ItemStack(Material.NETHER_STAR, 1); // rarest
            items[1] = new ItemStack(Material.DIAMOND, 1);
            items[2] = new ItemStack(Material.DIAMOND, 1);
            for (int i = 3; i < 6; i++){
                items[i] = new ItemStack(Material.KELP, 1);
            }
            for (int i = 6; i < 10; i++){
                items[i] = new ItemStack(Material.GOLD_INGOT, 1);
            }
            for (int i = 10; i < 15; i++){
                items[i] = new ItemStack(Material.EMERALD, 1);
            }
            for (int i = 15; i < 21; i++){
                items[i] = new ItemStack(Material.SWEET_BERRIES, 1);
            }
            for (int i = 21; i < 28; i++) {
                items[i] =new ItemStack(Material.ROTTEN_FLESH, 1); // most common
            }

            contents = items; // sets contents to items array
        }
        int startingIndex = ThreadLocalRandom.current().nextInt(contents.length);
        // generates and fetches the current index. returns it
        // randomises starting value

        // adding items to inventory

        for (int index = 0; index < startingIndex; index++) { // runs through each index in contents
            for (int itemstacks = 2; itemstacks < 21; itemstacks += 9) { // runs through necessary positions in inventory for first
                //wheel
                inv.setItem(itemstacks, contents[(itemstacks + itemIndex) % contents.length]);
                // sets values of inventory index to relevant item
            }
            itemIndex++; // moves to next item
        }

        int startingIndex2 = ThreadLocalRandom.current().nextInt(contents.length);
        for (int index = 0; index < startingIndex2; index++) {
            for (int itemstacks = 4; itemstacks < 23; itemstacks += 9) {
                inv.setItem(itemstacks, contents[(itemstacks + itemIndex) % contents.length]);
            }
            itemIndex++;
        }

        int startingIndex3 = ThreadLocalRandom.current().nextInt(contents.length);
        for (int index = 0; index < startingIndex3; index++) {
            for (int itemstacks = 6; itemstacks < 25; itemstacks+= 9){
                inv.setItem(itemstacks, contents[(itemstacks + itemIndex) % contents.length]);
            }
            itemIndex++;
        }
    }
// need to set to different delays. so, just have this as a method that is repeated 3 times with varying delays
// need to randomise the spins more

    public void spin(final Player player, Integer fee) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "" + ChatColor.BOLD + "Good luck!");
        // name of inventory
        shuffle(inv); // calls shuffle method to shuffle wheels
        invs.add(inv); // adds to inventory array
        player.openInventory(inv); // opens inventory for user

        new BukkitRunnable(){
            double delay = 0; // sets values to time the spins
            int ticks = 0;
            boolean done = false;

            Random r = new Random();
            double seconds = 7.0 + (15.0 - 7.0) * r.nextDouble(); // sets how long wheel will spin

            public void run(){
                if (done) {
                    return;
                }
                ticks++;
                delay += 1/ (20 *seconds); //delay keeps increasing
                // until it stops the spinning
                if (ticks > delay * 10){
                    ticks = 0; // resets ticks and changes positions for items
                    for (int itemstacks = 2; itemstacks < 21; itemstacks+= 9){
                        inv.setItem(itemstacks, contents[(itemstacks + itemIndex) % contents.length]);
                    }
                    for (int itemstacks = 4; itemstacks < 23; itemstacks+= 9){
                        inv.setItem(itemstacks, contents[(itemstacks + itemIndex) % contents.length]);
                    }
                    for (int itemstacks = 6; itemstacks < 25; itemstacks+= 9){
                        inv.setItem(itemstacks, contents[(itemstacks + itemIndex) % contents.length]);
                    }
                    itemIndex++;

                    if (delay >= 0.7){ // if long delay
                        done = true; // stops the spinning
                        new BukkitRunnable(){
                            public void run(){
                                ItemStack item1 = inv.getItem(11); // checks the relevant row to see if the user has won
                                ItemStack item2 = inv.getItem(13);
                                ItemStack item3 = inv.getItem(15);
                                Integer reward = 0;
                                Material rewardItem = Material.DIAMOND; // initially sets reward item
                                if (item1.equals(item2) && item2.equals(item3)){ // checks all 3 are same
                                    if (item1.getType().equals(Material.NETHER_STAR)){ // rarest - gives most reward
                                        reward = 10;
                                        rewardItem = Material.DIAMOND;
                                    }
                                    else if (item1.getType().equals(Material.DIAMOND)){
                                        reward = 5;
                                        rewardItem = Material.DIAMOND;
                                    }
                                    else if (item1.getType().equals(Material.EMERALD)){
                                        reward = 3;
                                        rewardItem = Material.DIAMOND;
                                    }
                                    else if (item1.getType().equals(Material.GOLD_INGOT)){
                                        reward = 2;
                                        rewardItem = Material.DIAMOND;
                                    }
                                    else if (item1.getType().equals(Material.KELP)){
                                        reward = 2;
                                        rewardItem = Material.GOLD_BLOCK;
                                    }
                                    else if (item1.getType().equals(Material.SWEET_BERRIES)){
                                        reward = 1;
                                        rewardItem = Material.DIAMOND;
                                    }
                                    else if (item1.getType().equals(Material.ROTTEN_FLESH)){ // least rare - least reward
                                        reward = 5;
                                        rewardItem = Material.ROTTEN_FLESH;
                                    }

                                    player.getInventory().addItem(new ItemStack(rewardItem, reward)); // adds reward to user inventory
                                    player.updateInventory();

                                    player.closeInventory(); // stops the slot machine
                                    cancel();
                                }
                                else{
                                    Bukkit.broadcastMessage("Better luck next time " + player.getDisplayName());
                                    // bad luck message
                                }
                            }
                        }.runTaskLater(shadow.getPlugin(shadow.class), 50);
                        cancel();
                    }

                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    @EventHandler
    public void OnClick(InventoryClickEvent e){ // used to make sure user can't take from the inventory while in use
        if (!invs.contains(e.getInventory())){
            return;
        }
        e.setCancelled(true);
        return;
    }
}
package dreamyr.eventplugin.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RewardSystem {

    public static void giveReward(Player player, List<ItemStack> rewardItems) {
        for (ItemStack item : rewardItems) {
            player.getInventory().addItem(item);
        }
        player.sendMessage("§aВам видано нагороду!");
    }

}

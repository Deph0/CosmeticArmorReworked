package lain.mods.cos.client;

import java.util.Map;
import java.util.UUID;
import lain.mods.cos.InventoryManager;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import org.apache.commons.io.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

public class InventoryManagerClient extends InventoryManager
{

    LoadingCache<UUID, InventoryCosArmor> cacheClient = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryCosArmor>()
    {

        @Override
        public InventoryCosArmor load(UUID owner) throws Exception
        {
            return new InventoryCosArmor();
        }

    });

    Map<UUID, UUID> map = Maps.newHashMap();

    @Override
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid)
    {
        if (map.isEmpty())
        {
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.thePlayer != null)
                map.put(UUID.nameUUIDFromBytes(("OfflinePlayer:" + mc.thePlayer.getGameProfile().getName()).getBytes(Charsets.UTF_8)), mc.thePlayer.getUniqueID());
        }
        if (map.containsKey(uuid))
            uuid = map.get(uuid);
        return cacheClient.getUnchecked(uuid);
    }

    @SubscribeEvent
    public void handleEvent(ClientDisconnectionFromServerEvent event)
    {
        PlayerRenderHandler.HideCosArmor = false;
        cacheClient.invalidateAll();
        map.clear();
    }

}

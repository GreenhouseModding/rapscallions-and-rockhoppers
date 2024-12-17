package dev.greenhouseteam.rapscallionsandrockhoppers.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersLootTables;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersMemoryModuleTypes;
import dev.greenhouseteam.rapscallionsandrockhoppers.registry.RockhoppersSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class CoughUpRewards extends DelayedBehaviour<Penguin> {
    Player playerToCoughFor = null;

    public CoughUpRewards(int delayTicks) {
        super(delayTicks);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(RockhoppersMemoryModuleTypes.FISH_EATEN, MemoryStatus.VALUE_PRESENT), Pair.of(RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR, MemoryStatus.VALUE_PRESENT), Pair.of(RockhoppersMemoryModuleTypes.BOAT_TO_FOLLOW, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    public boolean checkExtraStartConditions(ServerLevel level, Penguin penguin) {
        Entity playerToCoughFor = level.getEntity(BrainUtils.getMemory(penguin, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR));
        if (playerToCoughFor instanceof Player player && player.onGround()) {
            this.playerToCoughFor = player;
        }
        return penguin.onGround() && !penguin.isInWaterOrBubble() && this.playerToCoughFor != null;
    }

    @Override
    protected void doDelayedAction(Penguin penguin) {
        BrainUtils.setMemory(penguin, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.playerToCoughFor, true));
        LootTable lootTable = penguin.level().getServer().reloadableRegistries().getLootTable(RockhoppersLootTables.PENGUIN_COUGH_UP);
        LootParams.Builder builder = (new LootParams.Builder((ServerLevel)penguin.level())).withParameter(LootContextParams.THIS_ENTITY, penguin).withParameter(LootContextParams.ORIGIN, penguin.position());
        LootParams params = builder.create(LootContextParamSets.GIFT);
        for (int i = 0; i < Math.min(penguin.getFishEaten(), 12); ++i) {
            lootTable.getRandomItems(params, stack -> BehaviorUtils.throwItem(penguin, stack, this.playerToCoughFor.position()));
        }
        penguin.playSound(RockhoppersSoundEvents.PENGUIN_COUGH, 0.8F, 0.9F + penguin.getRandom().nextFloat() * 0.2F);
    }

    @Override
    protected void start(Penguin penguin) {
        BrainUtils.setMemory(penguin, RockhoppersMemoryModuleTypes.HUNGRY_TIME, penguin.tickCount + 4800);
        BrainUtils.setMemory(penguin, RockhoppersMemoryModuleTypes.TIME_ALLOWED_TO_EAT, penguin.tickCount + 1800);
        penguin.setCoughTicks(0);
    }

    @Override
    protected void stop(Penguin penguin) {
        this.playerToCoughFor = null;
        BrainUtils.clearMemory(penguin, RockhoppersMemoryModuleTypes.PLAYER_TO_COUGH_FOR);
        BrainUtils.clearMemory(penguin, RockhoppersMemoryModuleTypes.FISH_EATEN);
    }
}

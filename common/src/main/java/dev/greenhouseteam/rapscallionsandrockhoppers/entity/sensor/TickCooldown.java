package dev.greenhouseteam.rapscallionsandrockhoppers.entity.sensor;

import dev.greenhouseteam.rapscallionsandrockhoppers.entity.Penguin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TickCooldown extends PredicateSensor<Integer, Penguin> {
    private final MemoryModuleType<Integer> memory;
    private List<MemoryModuleType<?>> requiredMemories = new ObjectArrayList<>();
    private boolean shouldTickUp = false;
    private Function<Penguin, Integer> removeAt = (penguin) -> 0;

    public TickCooldown(MemoryModuleType<Integer> memory) {
        this.memory = memory;
        this.requiredMemories.add(this.memory);
    }

    public TickCooldown copy() {
        TickCooldown newSensor = new TickCooldown(this.memory);
        newSensor.requiredMemories = this.requiredMemories;
        newSensor.shouldTickUp = this.shouldTickUp;
        newSensor.removeAt = this.removeAt;

        return newSensor;
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return this.requiredMemories;
    }

    @Override
    protected void doTick(ServerLevel level, Penguin penguin) {
        Optional<Integer> cooldown = penguin.getBrain().getMemory(this.memory);
        cooldown.ifPresent(integer -> {
            if (!this.predicate().test(integer, penguin)) return;
            int value = integer - 1;
            BrainUtils.setMemory(penguin, this.memory, value);

            if (value <= 0) {
                penguin.getBrain().setMemory(this.memory, Optional.empty());
            }
        });
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return new SensorType<>(this::copy);
    }
}

package me.aleksilassila.litematica.printer.utils;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BreakQueuePacingTest {
    @Test void breakCounterResetsEveryTick() {
        var state = new BreakQueueState();
        state.recordBreak();
        state.recordBreak();
        assertEquals(2, state.breaksThisTick());
        state.tickMarkers();
        assertEquals(0, state.breaksThisTick());
    }

    @Test void intervalOfZeroOrOneNeverBlocksTheNextTick() {
        var state = new BreakQueueState();
        state.startBreakDelay(0);
        assertFalse(state.isBreakDelayed());
        state.startBreakDelay(1);
        assertTrue(state.isBreakDelayed(), "same tick stays blocked");
        state.tickMarkers();
        assertFalse(state.isBreakDelayed(), "interval 1 allows the next tick");
    }

    @Test void intervalDelaysTheNextBatchByThatManyTicks() {
        var state = new BreakQueueState();
        state.startBreakDelay(3);
        state.tickMarkers();
        assertTrue(state.isBreakDelayed());
        state.tickMarkers();
        assertTrue(state.isBreakDelayed());
        state.tickMarkers();
        assertFalse(state.isBreakDelayed());
    }

    @Test void cancellingPlannedWorkClearsPacing() {
        var state = new BreakQueueState();
        state.add(BlockPos.ZERO);
        state.recordBreak();
        state.startBreakDelay(10);
        state.cancelPlannedWork();
        assertFalse(state.isBreakDelayed());
        assertEquals(0, state.breaksThisTick());
    }

    @Test void resetClearsPacing() {
        var state = new BreakQueueState();
        state.recordBreak();
        state.startBreakDelay(10);
        state.reset();
        assertFalse(state.isBreakDelayed());
        assertEquals(0, state.breaksThisTick());
    }
}

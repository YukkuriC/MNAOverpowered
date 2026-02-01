package io.yukkuric.mnaop.mixin.fixes;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mna.api.spells.adjusters.SpellAdjustingContext;
import com.mna.spells.SpellCaster;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(SpellCaster.class)
public class FixSpellAdjusterRegisterLock {
    private static final Lock lock = new ReentrantReadWriteLock().writeLock();

    @WrapMethod(method = "registerAdjuster(Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V", remap = false, require = 0)
    private static void forceSingleThreaded(Predicate<SpellAdjustingContext> executeCheck, Consumer<SpellAdjustingContext> adjuster, Operation<Void> original) {
        lock.lock();
        original.call(executeCheck, adjuster);
        lock.unlock();
    }
}

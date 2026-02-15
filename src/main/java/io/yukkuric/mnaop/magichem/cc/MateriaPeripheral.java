package io.yukkuric.mnaop.magichem.cc;

import com.aranaira.magichem.block.entity.ext.AbstractMateriaStorageMultiTypeBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractMateriaStorageSingleTypeBlockEntity;
import com.aranaira.magichem.item.MateriaItem;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import io.yukkuric.mnaop.MNAOPMod;

import java.util.List;

import static com.aranaira.magichem.block.entity.ext.AbstractMateriaStorageMultiTypeBlockEntity.materiaMap;

public class MateriaPeripheral implements GenericPeripheral {
    public static final String ID = MNAOPMod.MODID + ":materia_holder";
    public static MateriaPeripheral INSTANCE = new MateriaPeripheral();

    @Override
    public String id() {
        return ID;
    }

    private MateriaItem toMateria(String raw) throws LuaException {
        if (!materiaMap.containsKey(raw))
            throw new LuaException("Not valid materia: " + raw + String.join(", ", materiaMap.keySet()));
        return materiaMap.get(raw);
    }

    @LuaFunction(mainThread = true)
    public List<String> getMateriaTypes(AbstractMateriaStorageMultiTypeBlockEntity be) {
        return be.getMateriaTypes().stream().map(x -> x.getMateriaName()).toList();
    }
    @LuaFunction(mainThread = true)
    public int getAmount(AbstractMateriaStorageMultiTypeBlockEntity be, String id) throws LuaException {
        var mat = toMateria(id);
        return be.getCurrentStock(mat);
    }
    @LuaFunction(mainThread = true)
    public int getLimit(AbstractMateriaStorageMultiTypeBlockEntity be, String id) throws LuaException {
        var mat = toMateria(id);
        return be.getStorageLimit(mat);
    }
    @LuaFunction(mainThread = true)
    public float getPercent(AbstractMateriaStorageMultiTypeBlockEntity be, String id) throws LuaException {
        var mat = toMateria(id);
        return be.getCurrentStockPercent(mat);
    }

    @LuaFunction(mainThread = true)
    public String getMateriaType(AbstractMateriaStorageSingleTypeBlockEntity be) {
        return be.getMateriaType().getMateriaName();
    }
    @LuaFunction(mainThread = true)
    public int getAmount(AbstractMateriaStorageSingleTypeBlockEntity be) throws LuaException {
        return be.getCurrentStock();
    }
    @LuaFunction(mainThread = true)
    public int getLimit(AbstractMateriaStorageSingleTypeBlockEntity be, String id) throws LuaException {
        return be.getStorageLimit();
    }
    @LuaFunction(mainThread = true)
    public float getPercent(AbstractMateriaStorageSingleTypeBlockEntity be, String id) throws LuaException {
        return be.getCurrentStockPercent();
    }
}

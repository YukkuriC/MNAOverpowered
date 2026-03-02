from YukkuriC.minecraft.collector import *

do_collect_simple(
    __file__,
    pattern='../build/libs/*-all.jar',
    pattern_exist='*.jar',
    renamer=lambda n: n.replace('-all.jar', '.jar'),
)

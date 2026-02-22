import os, re
from collections import defaultdict
from YukkuriC.minecraft.codegen.jinja import *
from YukkuriC.algorithm import min_diff_seq
from functools import partial

open = partial(open, encoding='utf-8')

pat_func = re.compile(
    r'''
        @LuaFunction .+?
        public \s+([\w<>,\s]+) \s+(\w+) \s*
        \(
            \s* (\w+)\s+\w+
            (\s*,\s* IComputerAccess \s+\w+)?
            (\s*,\s* (.+?))?
        \)
    ''',
    re.S | re.M | re.X,
)

if 'annotations':
    CLASS_DESCRIP_MAP = {
        "AbstractMateriaStorageSingleTypeBlockEntity": "Single-type materia jar/vessel",
        "AbstractMateriaStorageMultiTypeBlockEntity": "Multi-type materia storage",
        "IMateriaProvisionRequester": "Devices with materia provision functionality",
        "IMateriaSortingRequester": "Devices with materia sorting functionality",
        "IHasDeviceRecipeSlot": "Devices with recipe selection functionality",
        "AlchemicalNexusBlockEntity": "Alchemical Nexus",
    }
    FILE_TITLE_MAP = {
        "AlchemicalNexusPeripheral": "Alchemical Nexus Peripheral",
        "MateriaPeripheral": "Materia Container Peripheral",
        "RecipePeripheral": "Multi-Recipe Device Peripheral",
    }

    def wrapFile(filename):
        basename = os.path.splitext(filename)[0]
        return FILE_TITLE_MAP.get(basename, filename)

    def wrapType(type):
        return CLASS_DESCRIP_MAP.get(type, f'_{type}_')


if 'data':
    methodsByType = defaultdict(list)
    typesByFile = defaultdict(set)

    def check_file(filename):
        with open(os.path.join(base_dir, filename)) as f:
            data = f.read()
        for match in re.findall(pat_func, data):
            rType, func, target, _, _, args = match
            if target not in CLASS_DESCRIP_MAP:
                print(f'ignored: {target}.{func}')
                continue
            typesByFile[filename].add(target)
            methodsByType[target].append((rType, func, args))

    base_dir = r'../src/main/java/io/yukkuric/mnaop/magichem/cc'
    for f in os.listdir(base_dir):
        check_file(f)

sorted = sorted  # attach global
batch_gen(
    load_env(__file__),
    {
        'doc_cc_magichem': 'doc/MagiChemCC_raw.md',
    },
    ext='.md',
    render_args=globals(),
)

# keep all raw lines, merge manual doc
MERGE_TARGETS = [('../doc/MagiChemCC_raw.md', '../doc/MagiChemCC.md')]
for src, dst in MERGE_TARGETS:
    if not os.path.isfile(dst):
        continue
    with open(src) as f:
        slines = f.read().strip().splitlines()
    with open(dst) as f:
        dlines = f.read().strip().splitlines()
    seq = min_diff_seq(slines, dlines)

    with open(dst, 'w') as f:
        for op, l in seq:
            print(l, file=f)

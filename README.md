# MNAOverpowered

[![Curseforge](https://badges.moddingx.org/curseforge/versions/1374012) ![CurseForge](https://badges.moddingx.org/curseforge/downloads/1374012)](https://www.curseforge.com/minecraft/mc-mods/mnaoverpowered)  
[![Modrinth](https://badges.moddingx.org/modrinth/versions/qJT4GBnW) ![Modrinth](https://badges.moddingx.org/modrinth/downloads/qJT4GBnW)](https://modrinth.com/mod/mnaoverpowered)

Another QoL addon containing small tweaks about easy gaming experiences for Mana and Artifice

## Feature(s)

_note: all features that currently can't be turned off are marked with `☆`_  
_note 2: features which are controlled by `mnaoverpowered-mixin.ini` rather than `mnaoverpowered-common.toml` are marked with `△`, editing which requires a full restart for changes to apply_

- Speed-up casting resource regeneration, regardless of player's food saturation
- Block changes:
    - Eldrin Matrices:
        - no more diminish when placed more than 1
        - will receive multiplier from Eldrin Wellsprings
    - Rune Forges can grant both upgrades using hybrid crystal pairs
        - _note: still need a pair of pedestals and crystals to work_
- Configurable construct's milking cooldown
    - set to values < 0 to disable this feature
    - the real cooldown will be a random value between 1-2 times of this set value
- Config for min/max strength of natural generated wellsprings
- Custom commands
    - `(mnaop) locate wellspring [type]`: locate nearest wellspring from all, or of certain affinity
        - _currently only supporting those already generated :(_
- `△` Lodestar editing screen enhanced
    - `Ctrl + D` when selecting a group or node to make a full copy
        - including group-included nodes and their inner connections
    - Pressing `Delete` or `BackSpace` to remove current selected group or node
    - Removing a group holding `Shift` to wipe its node contents too
    - (experimental) `Ctrl + Z` to undo; `Ctrl + Y` or `Ctrl + Shift + Z` to redo
        - ONLY DURING SCREEN OPEN, will lose all undos after closing menu and saving changes
- Shift-right-clicking blocks in world when holding Codex Arcana to query related pages

---

- `MagiChem` stuff (only available when installed):
    - Unlocks the limit that a player can only have 1 orrery
    - Constructs with advanced transport ability moves materia shlorps as fast as max-powered labyrinth navigators
    - Multiple-stacked Circle of Power gains more multiplicative output rate on top
    - Tooltip enhanced:
        - displays each admixture's translated formula and overall essentia value
        - displays distillation contents for all items supported
    - `△` Custom construct task nodes
        - Batch provide materia to all locations inside a Book of Marks
            - including actuators connected to the root machine (starting from `v0.5.2-pre16`)
    - `△` CC:Tweaked interop (peripheral support):
        - materia container blocks (e.g. Mirror Labyrinth)
        - multi-recipe holder machines (e.g. Alchemical Nexus)
        - Alchemical Nexus specific control (by-stage requirements & auto item supply)
        - a more detailed Peripheral document can be found [HERE](https://github.com/YukkuriC/MNAOverpowered/blob/main/doc/MagiChemCC.md)

---

- `△` Unofficial fixes for some known issues
    - `Pylon: Mana` burns all constructs' mana with distance more than 16
    - lodestar filter with hollow inside loses its contents ([#1176](https://github.com/Mithion/Mana-And-Artifice/issues/1176))
    - potential crash & wrong tier for items ([#1182](https://github.com/Mithion/Mana-And-Artifice/issues/1182))
    - occasional startup crash caused by concurrent registry ([#1213](https://github.com/Mithion/Mana-And-Artifice/issues/1213))

---

- _MORE TO BE ADDED_

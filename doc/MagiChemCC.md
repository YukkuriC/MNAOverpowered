# MagiChem x CC:Tweaked Extra Peripheral Document

## Alchemical Nexus Peripheral
### Alchemical Nexus
- `List<Integer> getStage()`
    - return a list with 2 numbers
        - [1] for current crafting stage (batch of items & materia) (0-index)
        - [2] for the animation stage of the nexus, could be used to determine which sub-phase it's in
- `int getTotalStages()`
- `List<Map<String, Object>> getStageItemRequirements()`
    - all item requirements of current stage, wrapped with general CC item stack representation
- `int fillRequiredItems(String fromName)`
    - auto searches for required items in the inventory peripheral with given name, then try to extract 1 each into the nexus
    - does nothing when certain slot is occupied, even by wrong item
    - returns slots filled bitwise - starting from `0`, each `i`-th successful fill adds `pow(2, i)` to result

## Materia Container Peripheral
_note: all materia string representation currently uses prefix-less id_
_for example, `legendary` for Admixture of Legendary_

### Multi-type materia storage
- `List<String> getMateriaTypes()`
- `int getAmount(String id)`
- `int getLimit(String id)`
- `float getPercent(String id)`
    - _returns 1 when full, not 100_

### Single-type materia jar/vessel
- `String getMateriaType()`
    - `nil` if empty
- `int getAmount()`
- `int getLimit()`
- `float getPercent()`
    - _returns 1 when full, not 100_

### Devices with materia provision functionality
- `boolean needProvisioning()`
- `Map<String, Integer> getProvisioningNeeds()`

### Devices with materia sorting functionality
- `boolean needSorting()`

## Multi-Recipe Device Peripheral
### Devices with recipe selection functionality
- `Map<String, Object> getRecipe()`
    - returns current crafting target item, wrapped with general CC item stack representation
    - or `nil` if there's no recipe
- `int setRecipe(String input)`
    - input item's registry id with namespace; e.g. `magichem:wisdom_stone_rubedo`
- `void clearRecipe()`
    - _note: not working on Alchemical Nexus currently :(_

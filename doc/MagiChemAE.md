# AE2 interop for MagiChem

## Pattern provider & crafting machine support

- Prime Aggregator (Exaltation)
    - Supported inputs: source item
        - Optional: materia (at most 64 if bottled, since bottle slot cannot overflow), slurry fluid
    - Only allowing materials no more than those for 1 exaltation process in each pattern, or the whole pattern will be rejected
    - Machine needs not to be in-progress to receive pattern inputs
- (Grand) Circle of Fabrication
    - Supporting both item and fluid crafting
    - Inputs materia via pattern, or uses Glass Bottle as input placeholder, because AE2 doesn't allow patterns with empty input
    - Machine needs to have no recipe set, no non-matching items in input slots, and/or glass bottle slot not full to receive
    - pattern output amount cannot exceed max allowed size for one craft (e.g. a recipe with max batch 8 & rate 25% allows at most 32 as output)
- (Grand) Fusery
    - Supports materia input via pattern
    - _NOT YET supports slurry input_
    - ___NOTE: navigator directly to labyrinth (or mirror) will result that crafting tasks won't finish, and it's recommended to link navigators to Interface or Patter Provider blocks___
- _(Grand) Centrifuge already supports arbitrary input without set recipe, so here's no further support_
    - ___NOTE: navigator directly to labyrinth (or mirror) will result that crafting tasks won't finish, and it's recommended to link navigators to Interface or Patter Provider blocks___

## Mirror Labyrinth (with bound Magic Mirror) can get recognized by Storage Bus, providing unbottled materia blobs

- note: inserting bottled materia stacks to labyrinth by hand receives glass bottles of same amount
- and will **split bottles directly into the world** if no player is present

## Misc. changes

- AE2 blocks with internal inventory (Interface & Pattern Provider) is valid Navigator targets now (output only)
    - NOTE: if target block overflows, shlorps with nowhere to go becomes item drops in world, so make sure never targets offline or occupied Interface or Pattern Provider blocks

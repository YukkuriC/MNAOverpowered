# MagiChem x CC:Tweaked Extra Peripheral Document

## Alchemical Nexus Peripheral
### Alchemical Nexus
- `List<Integer> getStage()`
- `int getTotalStages()`
- `List<Map<String, Object>> getStageItemRequirements()`
- `int fillRequiredItems(String fromName)`

## Materia Container Peripheral
### Multi-type materia storage
- `List<String> getMateriaTypes()`
- `int getAmount(String id)`
- `int getLimit(String id)`
- `float getPercent(String id)`

### Single-type materia jar/vessel
- `String getMateriaType()`
- `int getAmount()`
- `int getLimit()`
- `float getPercent()`

### Devices with materia provision functionality
- `boolean needProvisioning()`
- `Map<String, Integer> getProvisioningNeeds()`

### Devices with materia sorting functionality
- `boolean needSorting()`

## Multi-Recipe Device Peripheral
### Devices with recipe selection functionality
- `Map<String, Object> getRecipe()`
- `int setRecipe(String input)`


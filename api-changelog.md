### 2.3.0
 * Deprecated IForestryMultiErrorSource - Use IErrorLogicSource from forestry API instead

### 2.2.0
 * Added IConfigLoader - allows other mods to submit configs to be loaded Gendustry

### 2.1.0
 * Added blocks.IForestryMultiErrorSource - access to multiple forestry error states (used in industrial apiary)

### 2.0.0
 * Switched to [Semantic Version Scheme](http://semver.org/)
 * Added **GendustryAPI.Registries** and **registries.IRegistriesApi**
 * Added **registries.IFluidSourceRegistry** - holds items to fluid conversion values
 * Added **registries.IMutatronOverrides** and **EnumMutationSetting** - allows overriding what mutations are available
 
Older changes were not recorded, you can see the commits [here](https://github.com/bdew/gendustry/commits/mc1710/src/net/bdew/gendustry/api).

package net.bdew.gendustry.custom

import java.util.Locale

import forestry.api.apiculture.{EnumBeeType, IBeeModelProvider}
import forestry.api.core.IModelManager
import forestry.apiculture.items.ItemBeeGE
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object BeeModelProvider extends IBeeModelProvider {
  @SideOnly(Side.CLIENT)
  private var models: Array[ModelResourceLocation] = null

  @SideOnly(Side.CLIENT)
  override def registerModels(item: Item, manager: IModelManager) {
    val beeIconDir: String = "bees/default/"
    val beeType: EnumBeeType = item.asInstanceOf[ItemBeeGE].getType
    val beeTypeNameBase: String = beeIconDir + beeType.toString.toLowerCase(Locale.ENGLISH)
    if (models == null) {
      models = new Array[ModelResourceLocation](EnumBeeType.values.length)
    }
    models(beeType.ordinal) = manager.getModelLocation(beeTypeNameBase)
    manager.registerVariant(item, new ResourceLocation("gendustry:" + beeTypeNameBase))
  }

  @SideOnly(Side.CLIENT)
  override def getModel(kind: EnumBeeType): ModelResourceLocation = {
    return models(kind.ordinal)
  }
}

package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import me.dpohvar.powernbt.PowerNBT
import me.dpohvar.powernbt.api.NBTCompound
import java.util.logging.Level

/**
 * NBT库,可以用来操作NBT
 * 具体设置参照Wiki: <a>https://minecraft.gamepedia.com/Attribute</a>
 * @sss me.dpohvar.powernbt.api.NBTManager
 */
@Suppress("unused")
class NBT(override vararg val defaultData: AttributeModifier) : Feature<Array<out NBT.AttributeModifier>>(), Feature.OnPostLoad {
    enum class AttributeType(val attributeName: String, val max: Double) {
        MaxHealth("generic.maxHealth", 1024.0),
        FollowRange("generic.followRange", 20485.0),
        KnockbackResistance("generic.knockbackResistance", 1.0),
        MovementSpeed("generic.movementSpeed", 1024.0),
        AttackDamage("generic.attackDamage", 2048.0),
        Armor("generic.armor", 30.0),
        ArmorToughness("generic.armorToughness", 20.0),
        AttackSpeed("generic.attackSpeed", 1024.0),
        Luck("generic.luck", 1024.0),
        JumpStrength("horse.jumpStrength", 2.0),
        FlyingSpeed("generic.flyingSpeed", 1024.0),
        SpawnReinforcements("zombie.spawnReinforcements", 1.0)
    }

    enum class AttributeOperation {
        Additive,
        MultiplicativeSum,
        MultiplicativeLast
    }

    enum class UseSlot {
        MainHand, OffHand, Feet, Legs, Chest, Head
    }

    /**
     * @param type 修改的属性
     * @param amount 修改值
     * @param operation 修改方式
     * @param slot 生效的槽位(默认(null)代表所有槽位)
     */
    data class AttributeModifier(
            val type: AttributeType,
            val amount: Double,
            val operation: AttributeOperation,
            val slot: UseSlot? = null
    )

    override fun onPostLoad(main: Main) {
        val nbt = api.read(item.get<ItemInfo>().itemStackTemplate).compound("tag").list("AttributeModifiers")
        data.forEach {
            if (it.amount < it.type.max) {
                val node = NBTCompound()
                node["AttributeName"] = it.type.attributeName
                node["Name"] = "SuperItem NBT ${it.type.name}"
                it.slot?.let { node["Slot"] = it.name.toLowerCase() }
                node["Operation"] = it.operation.ordinal
                node["Amount"] = it.amount
                node["UUIDLeast"] = 894654
                node["UUIDMost"] = 2872
                nbt.add(node)
            } else {
                main.logger.log(Level.WARNING, "错误的NBT属性: $it")
            }
        }
    }

    companion object {
        val api = PowerNBT.getApi()!!
    }
}
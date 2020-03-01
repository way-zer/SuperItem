package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.Metadatable

object DataStore {
    interface DataStore<T>{
        fun set(o: Metadatable,value: T?)
        fun <T> get(o:Metadatable,defaultValue:T):T
    }
    class MetaStore<T>(override val defaultData: Nothing) :Feature<Nothing>(),DataStore<T>{
        private val key:String
            get() = "SIMS_${item.name}"
        override fun <T> get(o: Metadatable, defaultValue: T):T {
            @Suppress("UNCHECKED_CAST")
            return if(o.hasMetadata(key)) o.getMetadata(key)[0].value() as T
            else defaultValue
        }

        override fun set(o: Metadatable, value: T?) {
            if(value==null)o.removeMetadata(key, Main.main)
            else o.setMetadata(key,FixedMetadataValue(Main.main,value))
        }
    }
    //TODO other Store: SQL and File
}
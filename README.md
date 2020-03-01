# Superitem
An extensible custom item minecraft plugin
* written in Kotlin(Support Java)
* every item is a class file(runtime load)
* can write custom item feature
* every item has Automatically generated detailed configuration(from name to potion effect)
* support **kotlin DSL** and **kotlin Script(alse Superitem.kts)**
    (see wiki for example)
## First Glance
Save it as hello.superitem.kts in plugins/Superitem/items to use
```kotlin
require(ItemInfo(Material.NAME_TAG,
    "&cHello SuperItem", 
    listOf("&aWelcome to use SuperItem",
        "&a欢迎使用Superitem",
        "&c This Item doesn't have other effect")))
```
## For More information
see [Wiki]
## For Chinese
see README.zh.md
## Thanks
* [LibraryManager] (shadowed) For runtime dependencies management
* [PowerNBT] (shadowed) For NBT support
## License
you should leave link to [this] and keep the author name in command help  
feel free to fork and pull requests

[this]: https://github.com/way-zer/SuperItem/
[Wiki]: https://github.com/way-zer/SuperItem/wiki
[LibraryManager]: https://github.com/way-zer/LibraryManager
[PowerNBT]: https://github.com/steakteam/PowerNBT
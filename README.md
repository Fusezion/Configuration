> [!IMPORTANT]
> **NOTE:** AI WAS HEAVILY USED (I was lazy with this)

Configuration is a lightweight-ish minecraft config manager library built on top of Bukkit's api to help me with the size issue of my plugins.
I love kotlin's KotlinXSerialization however its size when used in plugins is almost unmanageable and some forks for yaml make it all the more apparent.

I loved the YamlKT, however not only has it not been updated breaking with polymorphic designs but also added at minimum 1-2mb of bloat
Following this I tried Kaml a fork of KTaml however this was even worst than yaml making an already bloated plugin reach 8mb just for yaml

As such I've decided to use ai to design a simple library that handled majority of my use cases though not polymorphic systems.
This new library uses kotlin's properties api to allow defining and accessing parts of configs without relying on kotlin's `kotlin-reflect` library

This plugin uses reflect at most in 2 places 
1. to access `ObjectSerializer` defined in a companion
2. when retrieving the serializer for a BukkitSerializable object

---

TODO: when I get time create a full example on how to use this as its different compared to my old library ConfigSerialization

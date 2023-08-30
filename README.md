# Welcome on StaffProtect github 
Here you can look for some resources
- https://www.spigotmc.org/resources/authors/diskotekastarm.781481/
- https://github.com/ExperiencePowered

In case you want to use Developer API, it is recommended to develop plugin as addon, so it is directly run from plugin
build.gradle
```groovy
repositories {
    maven {
        url = 'https://jitpack.io'
    }
}
```
If your plugin is directly addon, then compile it, otherwise shade it
```groovy
dependencies {
    implementation 'com.github.ExperiencePowered:StaffProtect:1.0-SNAPSHOT'
}
```

pom.xml
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```
<dependency>
    <groupId>com.github.ExperiencePowered</groupId>
    <artifactId>StaffProtect</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

# How to make your own addon?
Firstly create project as it would be plugin, then add StaffProtect api to your build system (compile it),
you can also still have Paper/Spigot there and use it. In your main class, instead of extending JavaPlugin, extend ```AbstractAddon```,
it works similar to JavaPlugin class, although addons have one global config used by any addon,
although you can still make your own config, is it not recommended and not supported. To schedule anything,
use MinecraftScheduler (```getScheduler()``` in main class), to register listener or command, you have methods like
```registerCommand(MinecraftCommand)``` and else..
### Also dont forget, you need to rename plugin.yml to addon.yml, look for more on example addon

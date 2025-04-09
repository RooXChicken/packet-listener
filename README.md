# Packet Listener
This API allows you to listen to and modify `NMS` (NetMinecraftServer) packets

It uses Bukkit events to allow plugins to listen to `PacketReceiveEvent` and `PacketSendEvent`, allowing direct access to the packet and even cancelling it

### _(Please be careful! This can and will mess with plugins. You have been warned!)_


## Reflection
You will need to use [Reflection](https://www.baeldung.com/java-reflection) to modify `NMS` packets 99% of the time.

There is an example below if you've never done this before

## Examples:

```java
public void modifyServerPlayerPosition(PacketReceiveEvent event) {
  // if the packet is not our desired packet type, we return
  if(!(event.getPacket() instanceof ServerboundMovePlayerPacket packet))
      return;

  try {
    // we want to modify the x variable in this packet
    Field setX = packet.getClass().getField("x");
    setX.setAccessible(true); // allows this private final field to be accessible to us

    // set all player move packets to be offset by 1 on the x axis
    setX.set(packet, packet.x + 1);
  }
  catch(Exception e) {
    // in case our field doesn't exist, log it so we know
    Bukkit.getLogger().log(Level.SEVERE, "Failed to modify `ServerboundMovePlayerPacket` packet! " + e);
  }
}
```

## How to use:
Put the following in your maven file

```html
<dependency>
  <groupId>org.loveroo</groupId>
  <artifactId>packetlistener</artifactId>
  <version>1.0-SNAPSHOT</version>
  <scope>system</scope>
  <systemPath>[PATH TO JAR FILE]</systemPath>
</dependency>
```

and make sure to depend on it in your `plugin.yml`! 

```yml
depend: [ PacketListener ]
```

(if you use `ProtocolLib`, you may need to put `PacketListener`'s depend above it)

and make sure the plugin is in your `plugins` folder! (you can download the latest version from [here](https://github.com/RooXChicken/packet-listener/releases))

## NMS
In order to get access to `NMS` packets and look at their data, we need to include the Spigot mappings for `NMS`.

Put the following in your maven file

```html
<dependency>
  <groupId>org.spigotmc</groupId>
  <artifactId>spigot</artifactId>
  <version>[DESIRED VERSION]</version>
  <!-- this is the important part! this tells maven to get the mojang mappings -->
  <classifier>remapped-mojang</classifier>
</dependency>

<!-- (this is optional, but may be needed. if you can't find a class, import this) -->
<dependency>
  <groupId>org.bukkit</groupId>
  <artifactId>craftbukkit</artifactId>
  <version>[DESIRED VERSION]</version>
  <scope>provided</scope>
</dependency>
```

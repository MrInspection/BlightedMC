# BlightedMC

BlightedMC is a open-source project inspired by Hypixel Skyblock for some core mechanics. It aims to create some similar experience, adapted for Minecraft Survival with more punishing mobs, custom items, bosses and more...


> [!IMPORTANT]
> This project is still under development, the entire gameplay isn't finished yet.

### Why This Project Exists

This project exists for a single reason, Minecraft isn't difficult enough. I play the game in **HARD** difficulty when you're good with the game, it feels not really hard. Therefore, I'm creating this custom experience to add some difficulty to the game without removing the Vanilla experience.

This project is also a way for me to practice in java with design patterns, efficient code, etc... I'm mainly a web developer, but Java is one of my first programming language. I don't usually code a lot in java, so this project will help me do that, in a fun manner.

### Features:
* Custom crafting system
* Custom mobs creation system
* Custom items creation system w/ full set bonus & item abilities
* Some fun commands and some OP commands (not to abuse, can crash a server)

### Development Environment

* Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) with JDK 23.
* Clone the GitHub repository
* Modify the code as desired
* Go to `File > Project Structures > Artifacts` then
  * New `JAR` from `module with dependencies` and set the `output directory` to your server's plugins directory
  * Make sure to check `Include in project build`
* Compile the code and start your development server.
* Enjoy!

### Server Setup
* Download and install Spigot's build tools for version `1.21.8` from [here](https://www.spigotmc.org/wiki/buildtools/)
* From the build tools, compile the desired spigot version.
  * Create a directory and place the **compiled spigot jar**, then rename it to `server.jar`
  * Create `start.bat` file and add the following content:

    ```shell
    java -Xmx1G -jar server.jar nogui
    PAUSE
    ```
  * Launch the script to download the server elements, you will be prompted to accept Minecraft EULA by setting `eula=true` in `eula.txt`.
  * Once the server files are created, put the BlightedMC jar to the `plugins` directory and restart the server.
  * Voilà, you're ready to start your Blighted Survival.

---

## Technical Stuff

### BlightedMC's Custom Items

The creation of a custom item is pretty straightforward, thanks to the `ItemManager` class powering everything.
The `ItemManager` is an extension of the `ItemBuilder` class with additional features like ability binding and management, item rules (to prevent certain interactions with the items), full set bonus system for armors and more...

```java

ItemManager glimmeringEye = new ItemManager(
      "GLIMMERING_EYE",
      ItemType.UNCATEGORIZED,
      ItemRarity.RARE,
      Material.ENDER_EYE,
      "Glimmering Eye"
    );
    glimmeringEye.addEnchantmentGlint();
    glimmeringEye.addAbility(new Ability(new InstantTransmissionAbility(), "Instant Transmission", AbilityType.RIGHT_CLICK));
    glimmeringEye.addRule(new PreventInteractionRule());
    glimmeringEye.addRule(new PreventProjectileLaunchRule());

  ItemsRegistry.addItem(glimmeringEye);
```

### BlightedMC's Custom Mobs

The creation of a custom mob is powered by the [`BlightedEntity`](src/main/java/fr/moussax/blightedMC/core/entities/BlightedEntity.java) abstract class, providing a lot of useful methods to create a custom mob. It also provides advanced concepts such as "EntityAttachment" which links 2 entities together (damage applied to both, if one dies, the other too).

```java
public class Dummy extends BlightedEntity {
  public Dummy() {
    super("Dummy", 40, 6, EntityType.HUSK);
    setDroppedExp(15);
    setNameTagType(EntityNameTag.BLIGHTED);
  }

  @Override
  public String getEntityId() {
    return "DUMMY";
  }

  @Override
  protected void applyEquipment() {
    this.itemInMainHand = new ItemStack(Material.IRON_HOE);
    this.armor = new ItemStack[]{
      new ItemStack(Material.IRON_BOOTS),
      new ItemStack(Material.IRON_LEGGINGS),
      new ItemStack(Material.IRON_CHESTPLATE),
      new ItemStack(Material.IRON_HELMET)
    };
    super.applyEquipment();
  }

  @Override
  public LivingEntity spawn(Location location) {
    LivingEntity mob = super.spawn(location);

    ZombieHorse horse = (ZombieHorse) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ZOMBIE_HORSE);
    horse.setCustomName("§8Dummy's Steed");
    horse.setCustomNameVisible(true);
    horse.setTamed(true);

    Objects.requireNonNull(horse.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(maxHealth);
    horse.setHealth(maxHealth);

    mob.leaveVehicle();
    horse.addPassenger(mob);

    EntityAttachment attachment = new EntityAttachment(horse, this);
    addAttachment(attachment);

    return mob;
  }

  @Override
  public void kill() {
    if (entity != null && entity.isInsideVehicle()) {
      entity.leaveVehicle();
    }
    super.kill();
  }
}
```

### Demonstration

https://moussax.vercel.app/videos/mc_attachment.mp4

![Icon](https://github.com/Xannosz/MinecartPeripherals/blob/master/src/main/resources/icon.png?raw=true "Icon")

Simple minecraft computer-craft add-on for handling minecarts.
The mod includes a detector block for computers and a new peripheral for turtles.
This mod is made for [Better Minecarts](https://www.curseforge.com/minecraft/mc-mods/xannoszs-better-minecarts) but can be used without it.

## minecart detector

![Icon](https://github.com/Xannosz/MinecartPeripherals/blob/master/pictures/detector.png?raw=true "Minecart Detector")

#### usage:

![Icon](https://github.com/Xannosz/MinecartPeripherals/blob/master/pictures/detector_in_place.png?raw=true "Minecart Detector Usage")

Place the minecart detector under any type of rail.
The detector will see all minecarts and rails within its detection range (default 25 blocks, configurable). The detector range works across the rails,
so you will need to connect the rails together or use multiple detectors. You can connect your computer to either side of the detector and use it with wires.

Example code: [railGui.lua](https://pastebin.com/zrh2UtXQ)

#### crafting:

![Icon](https://github.com/Xannosz/MinecartPeripherals/blob/master/pictures/detector_craft.png?raw=true "Minecart Detector Crafting")

#### LUA api:

|             method             |     return     | description                                                                                                             |
|:------------------------------:|:--------------:|-------------------------------------------------------------------------------------------------------------------------|
|          listRails()           | list of tables | List all rail in the detector range.                                                                                    |
|        listMinecarts()         | list of tables | List all minecart in the detector range.                                                                                |
|        getRail(x, y, z)        |     table      | Get details for a rail, with the relative coordinates.                                                                  |
|        getMinecart(id)         |     table      | Get details for a minecart with the Id.                                                                                 |
| activateMinecart(id, activate) |      void      | Activate the minecart (like an activator rail do it)                                                                    |
|  moveMinecart(id, direction)   |      void      | Push the minecart with a little amount of energy, for a specific direction. Valid directions: north, south, west, east. |
|        stopMinecart(id)        |      void      | Stop the minecart.                                                                                                      |
|  getMinecartInventoryList(id)  |     table      | Get all item from the minecart inventory, with some details. Return with an integer key table.                          |

#### additional functions when better minecarts installed:

|            method             | return | description                                                              |
|:-----------------------------:|:------:|--------------------------------------------------------------------------|
| setLocomotiveSpeed(id, speed) |  void  | Set up locomotive speed, [1,2,3] forward, 0 pause, -1 stop, -2 backward. |
|   toggleLocomotiveLamp(id)    |  void  | Toggle locomotive lamp.                                                  |
|  toggleLocomotiveSignal(id)   |  void  | Toggle locomotive redstone signal.                                       |
|          whistle(id)          |  void  | Use the locomotive whistle.                                              |
|       connect(id1, id2)       |  void  | Connect two minecart together, the first will be the parent.             |
|     disconnect(id1, id2)      |  void  | Disconnect two minecart, the first should be the parent.                 |
|       revert(id1, id2)        |  void  | Revert train direction, the two minecart should be in the same train.    |

## minecart loader

![Icon](https://github.com/Xannosz/MinecartPeripherals/blob/master/pictures/loader_turtle.png?raw=true "Minecart Loader")

#### usage:

![Icon](https://github.com/Xannosz/MinecartPeripherals/blob/master/pictures/turtle_in_place.png?raw=true "Minecart Loader Usage")

A turtle can control a minecart if it is directly in front of the turtle. If the minecart has inventory, the turtle can directly access the minecart's inventory.
It can place a minecart on the track or pick it up.

Example code: [minecartCli.lua](https://pastebin.com/tmTShRFj)

#### crafting:

![Icon](https://github.com/Xannosz/MinecartPeripherals/blob/master/pictures/loader_craft.png?raw=true "Minecart Loader Crafting")

#### LUA api:

|                 method                 | return  | description                                                                                                                  |
|:--------------------------------------:|:-------:|------------------------------------------------------------------------------------------------------------------------------|
|         isMinecartAtTheFront()         | boolean | True, when a minecart direct at the front of the turtle.                                                                     |
|           isValidInventory()           | boolean | True, when the minecart has inventory.                                                                                       |
|            inventorySize()             | integer | Get how many slots are in the minecart inventory.                                                                            |
|            inventoryList()             |  table  | Get all item from the minecart inventory, with some details. Return with an integer key table.                               |
|          getItemDetail(slot)           |  table  | Get all detail for the selected item stack.                                                                                  |
|           getItemLimit(slot)           | integer | Get item limit for a slot.                                                                                                   |
| pushItems(fromSlot[, limit[, toSlot]]) | integer | Move items from the turtle's inventory to the minecart's inventory.                                                          |
| pullItems(fromSlot[, limit[, toSlot]]) | integer | Move items from the minecart's inventory to the turtle's inventory.                                                          |
|      addMinecart(direction, slot)      |  void   | Place a minecart in front of the turtle, from the slot, with specific direction. Valid directions: north, south, west, east. |
|          removeMinecart(slot)          |  void   | Remove the minecart at front of the turtle and put in the selected slot.                                                     |
|          renameMinecart(name)          |  void   | Rename minecart with a new name.                                                                                             |
|      toggleMinecartName(showName)      |  void   | Toggle minecart name visibility.                                                                                             |

#### additional functions when better minecarts installed:

|               method                | return | description                                                                                                           |
|:-----------------------------------:|:------:|-----------------------------------------------------------------------------------------------------------------------|
| colorizeMinecart(slot [, isBottom]) |  void  | Use a dye from the specific slot on the minecart, if isBottom=false change the top color, otherwise the bottom color. |
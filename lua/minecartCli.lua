local args = {...};

local per = peripheral.find("minecartLoader");

if args[1] == "isMinecartAtTheFront" then
    print(per.isMinecartAtTheFront())
    return
end

if args[1] == "isValidInventory" then
    print(per.isValidInventory())
    return
end

if args[1] == "inventorySize" then
    if per.isValidInventory() then
        print(per.inventorySize())
    else
        print("not a valid inventory")
    end
    return
end

if args[1] == "inventoryList" then
    if per.isValidInventory() then
        print(textutils.serialize(per.inventoryList()))
    else
        print("not a valid inventory")
    end
    return
end

if args[1] == "getItemDetail" then
    if per.isValidInventory() then
        if args[2] == nil then
            print("add the slot number")
            return
        end
        local slot = tonumber(args[2])
        if slot == nil then
            print(args[2] .. " is not a number")
            return
        end
        local detail = per.getItemDetail(slot);
        if detail == nil then
            print("{}")
            return
        end
        print(textutils.serialize(detail))
    else
        print("not a valid inventory")
    end
    return
end

if args[1] == "renameMinecart" then
    if per.isMinecartAtTheFront() then
        if args[2] == nil then
            print("add a name")
            return
        end
        per.renameMinecart(args[2]);
        per.toggleMinecartName(true);
    else
        print("no minecart")
    end
    return
end

if args[1] == "hideMinecartName" then
    if per.isMinecartAtTheFront() then
        per.toggleMinecartName(false);
    else
        print("no minecart")
    end
    return
end

print("Valid commands:\n\nisMinecartAtTheFront\nisValidInventory\ninventorySize\ninventoryList\ngetItemDetail <slot>\nrenameMinecart <name>\nhideMinecartName")
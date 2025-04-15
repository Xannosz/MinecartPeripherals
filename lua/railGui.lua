-- Globals --
messageTimerKey = nil;
message = "";
selectedMinecartId = nil;
minecartDetailsMode = false;
helpMode = false;
minecartDetector = peripheral.find("minecartDetector");
rails = nil;
minecarts = nil;

-- Functions --
function addMessage(messageIn)
    message = messageIn;
    messageTimerKey = os.startTimer(3);
end

function getSelectedMinecartRowNumber()
    for i, minecart in pairs(minecarts) do
        if (minecart.Id == selectedMinecartId) then
            return i;
        end
    end
    return -1;
end

function detect()
    rails = minecartDetector.listRails();
    minecarts = minecartDetector.listMinecarts();

    if getSelectedMinecartRowNumber() == -1 then
        if #minecarts==0 then
            selectedMinecartId = nil;
        else
            selectedMinecartId = minecarts[1].Id;
        end
    end
end

function previousMinecart()
    detect();
    local rowNumber = getSelectedMinecartRowNumber();
    if rowNumber == -1 then
        return
    end

    if rowNumber < 2 then
        selectedMinecartId = minecarts[1].Id;
    else
        selectedMinecartId = minecarts[rowNumber - 1].Id;
    end
end

function nextMinecart()
    detect();
    local rowNumber = getSelectedMinecartRowNumber();
    if rowNumber == -1 then
        return
    end

    if rowNumber >= #minecarts then
        selectedMinecartId = minecarts[#minecarts].Id;
    else
        selectedMinecartId = minecarts[rowNumber + 1].Id;
    end
end

function getColorForRail(rail)
    if rail.X == 0 and rail.Z == 0 then
        return colors.cyan;
    end
    return colors.blue;
end

-- Draw UI --
function draw()
    detect();
    pixels = {};

    for _, rail in pairs(rails) do
        local key = "" .. rail.X .. ":" .. rail.Z;
        pixels[key] = {};
        pixels[key].bg = getColorForRail(rail);
        pixels[key].x = rail.X;
        pixels[key].z = rail.Z;
        pixels[key].c = " ";
        pixels[key].fg = colors.white;
    end

    for _, minecart in pairs(minecarts) do
        local key = "" .. math.floor(minecart.X) .. ":" .. math.floor(minecart.Z);
        if pixels[key] == nil then
            pixels[key] = {};
            pixels[key].bg = colors.black;
            pixels[key].x = math.floor(minecart.X);
            pixels[key].z = math.floor(minecart.Z);
        end
        if minecart.Id == selectedMinecartId then
            pixels[key].c = string.char(127);
        else
            pixels[key].c = string.char(143);
        end
        pixels[key].fg = colors.white;
    end

    local w, h = term.getSize();

    if helpMode then
        term.setBackgroundColor(colors.black);
        term.setTextColor(colors.blue);
        term.setCursorPos(w / 2 - 13, h / 2 - 4);
        term.write("+------------------------+");
        term.setCursorPos(w / 2 - 13, h / 2 - 3);
        term.write("|   H  : show this page  |");
        term.setCursorPos(w / 2 - 13, h / 2 - 2);
        term.write("|   T  : exit to shell   |");
        term.setCursorPos(w / 2 - 13, h / 2 - 1);
        term.write("|  QE  : switch minecart |");
        term.setCursorPos(w / 2 - 13, h / 2);
        term.write("| WASD : move minecart   |");
        term.setCursorPos(w / 2 - 13, h / 2 + 1);
        term.write("|   X  : stop minecart   |");
        term.setCursorPos(w / 2 - 13, h / 2 + 2);
        term.write("|   Z  : show minecart   |");
        term.setCursorPos(w / 2 - 13, h / 2 + 3);
        term.write("+------------------------+");
    elseif minecartDetailsMode and selectedMinecartId ~= nil then
        local minecart = minecartDetector.getMinecart(selectedMinecartId);
        term.setBackgroundColor(colors.black);
        term.setTextColor(colors.blue);
        term.setCursorPos(w / 2 - 21, h / 2 - 4);
        term.write("+------------------------------------------+");
        term.setCursorPos(w / 2 - 21, h / 2 - 3);
        term.write("|        Id       :                        |");
        term.setCursorPos(w / 2 - 1, h / 2 - 3);
        term.write(minecart.Id);
        term.setCursorPos(w / 2 - 21, h / 2 - 2);
        term.write("|       Type      :                        |");
        term.setCursorPos(w / 2 - 1, h / 2 - 2);
        term.write(minecart.Type);
        term.setCursorPos(w / 2 - 21, h / 2 - 1);
        term.write("|       UUID      :                        |");
        term.setCursorPos(w / 2 - 1, h / 2 - 1);
        term.write(string.sub(minecart.UUID,1,23));
        term.setCursorPos(w / 2 - 21, h / 2);
        term.write("| Is Name Visible :                        |");
        term.setCursorPos(w / 2 - 1, h / 2);
        term.write(minecart.IsNameVisible);
        term.setCursorPos(w / 2 - 21, h / 2 + 1);
        term.write("|       Name      :                        |");
        term.setCursorPos(w / 2 - 1, h / 2 + 1);
        term.write(minecart.Name);
        term.setCursorPos(w / 2 - 21, h / 2 + 2);
        term.write("+------------------------------------------+");
    else
        term.setBackgroundColor(colors.black);
        term.clear();
        for _, pixel in pairs(pixels) do
            term.setCursorPos(pixel.x + w / 2, pixel.z + h / 2);
            term.setBackgroundColor(pixel.bg);
            term.setTextColor(pixel.fg);
            term.write(pixel.c);
        end
    end
    term.setCursorPos(1, h);
    term.setBackgroundColor(colors.black);
    term.setTextColor(colors.blue);
    term.write(message);
end

-- Main loop --
loopTimerKey = os.startTimer(1); -- sometimes stopped
while true do
    draw();
    local event, key, id = os.pullEvent();
    if event == "timer" then
        if key == loopTimerKey then
            loopTimerKey = os.startTimer(1);
        end
        if key == messageTimerKey then
            message = "";
        end
    end
    if event == "minecart_stepped_in" then
        addMessage("Minecart detected: " .. id);
    end
    if event == "minecart_stepped_out" then
        addMessage("Minecart lost: " .. id);
    end
    if event == "key" then
        if minecartDetailsMode or helpMode then
            minecartDetailsMode = false;
            helpMode = false;
        else
            if key == keys.t then
                break ;
            elseif key == keys.z then
                minecartDetailsMode = true;
            elseif key == keys.h then
                helpMode = true;
            elseif key == keys.x and selectedMinecartId ~= nil then
                minecartDetector.stopMinecart(selectedMinecartId);
            elseif key == keys.w and selectedMinecartId ~= nil then
                minecartDetector.moveMinecart(selectedMinecartId, "north");
            elseif key == keys.a and selectedMinecartId ~= nil then
                minecartDetector.moveMinecart(selectedMinecartId, "west");
            elseif key == keys.s and selectedMinecartId ~= nil then
                minecartDetector.moveMinecart(selectedMinecartId, "south");
            elseif key == keys.d and selectedMinecartId ~= nil then
                minecartDetector.moveMinecart(selectedMinecartId, "east");
            elseif key == keys.q then
                previousMinecart();
            elseif key == keys.e then
                nextMinecart();
            else
                addMessage("Press H for help, T for exit");
            end
        end
    end
end

-- catch T as char event --
os.pullEvent();
term.setCursorPos(1, 1);
term.setBackgroundColor(colors.black);
term.clear();
package dev.yeruza.plugin.permadeath.api.commands;

import java.util.LinkedList;
import java.util.List;

public abstract class PDGroupCommand  {
    private final List<PDSubCommand> subCommands = new LinkedList<>();
}

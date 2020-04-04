package com.github.MrMks.skillbar.bukkit.task;

public interface Task {
    void run();
    boolean isFinish();
    boolean shouldRun(long nTime);
}

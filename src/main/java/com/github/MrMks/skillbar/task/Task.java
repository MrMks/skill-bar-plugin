package com.github.MrMks.skillbar.task;

public interface Task {
    void run();
    boolean isFinish();
    boolean shouldRun(long nTime);
}
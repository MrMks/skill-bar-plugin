package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.BlackList;

public class AutoSaveTask extends RepeatTask {

    public AutoSaveTask(){
        super(5*1000, 300 * 1000);
    }

    @Override
    protected void runTask() {
        BlackList list = BlackList.getInstance();
        if (list != null) {
            list.writeToDisk();
        }
    }

    @Override
    public boolean isFinish() {
        return true;
    }

    @Override
    public void clear() {

    }
}

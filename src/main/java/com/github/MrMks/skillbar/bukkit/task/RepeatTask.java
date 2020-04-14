package com.github.MrMks.skillbar.bukkit.task;

public abstract class RepeatTask implements Task{

    private long time = System.currentTimeMillis();
    private int interval;
    public RepeatTask(int delay, int interval){
        time += delay;
        this.interval = Math.max(interval, 500);
    }

    public void run(){
        runTask();
        time += interval;
    }

    @Override
    public boolean shouldRun(long nTime) {
        return nTime >= time;
    }

    protected abstract void runTask();
    public abstract boolean isFinish();
}
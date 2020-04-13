package com.github.MrMks.skillbar.task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoopThread extends Thread {
    private boolean enable = true;
    private long time = System.currentTimeMillis();

    private List<Task> tasks = new LinkedList<>();

    @Override
    public void run() {
        while (enable){
            ArrayList<Task> re = new ArrayList<>();
            for (Task task : tasks) {
                try {
                    if (task.shouldRun(time)) task.run();
                    if (task.isFinish()) re.add(task);
                } catch (Throwable tr){
                    tr.printStackTrace();
                }
            }
            for (Task task : re) tasks.remove(task);
            try {
                time += 500;
                sleep(Math.max(1L, time - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void enable(){
        this.enable = true;
        this.start();
    }

    public void disable(){
        this.enable = false;
        tasks.clear();
    }

    public void addTask(RepeatTask task){
        tasks.add(task);
    }
}
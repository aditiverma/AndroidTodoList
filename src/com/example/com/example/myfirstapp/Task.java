package com.example.com.example.myfirstapp;

public class Task implements Comparable {
	private int TaskID;
	private String Desc;
	private int IconID;
	public boolean Selected = false;
	public boolean Completed = false;
	
	public Task(int taskID, String desc, int iconID, boolean selected, boolean completed) {
		super();
		TaskID = taskID;
		Desc = desc;
		IconID = iconID;
		Selected = selected;
		Completed = completed;
	}
	public int getIconID() {
		return IconID;
	}
	public int getTaskID() {
		return TaskID;
	}
	public String getDesc() {
		return Desc;
	}
	public boolean isSelected() {
		return Selected;
	}
	public boolean isCompleted() {
		return Completed;
	}
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

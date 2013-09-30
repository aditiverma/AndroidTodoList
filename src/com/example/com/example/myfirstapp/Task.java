package com.example.com.example.myfirstapp;

public class Task {
	private int TaskID;
	private String Desc;
	private int IconID;
	
	public Task(int taskID, String desc, int iconID) {
		super();
		TaskID = taskID;
		Desc = desc;
		IconID = iconID;
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
	
}

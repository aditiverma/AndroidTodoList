package com.example.com.example.myfirstapp;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;

import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnGesturePerformedListener{
	
	private GestureLibrary gestureLibrary;
	private static List<Task> ToDoList = new ArrayList<Task>();
	static int index=1;
	ArrayAdapter<Task> adapter;
	Button bt;
	public View row;
	TextView tv;
	public static List<Task> selectedTasks = new ArrayList<Task>();
	public static List<Task> undoTasks = new ArrayList<Task>();
	public static List<Task> redoTasks = new ArrayList<Task>();
	public String last_task = null;	
	public String undo_task = null;	
	public Map<Task, Integer> mapDeletedTaskWithIndex = new HashMap<Task, Integer>();
	final Context context = this;
	ListView lv;

	int count=0;
	static int dayShift=0;
	public int selectMode =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Inflate the menu; this adds items to the action bar if it is present.
		setContentView(R.layout.activity_main);
		//Get gesture library from res/raw/gestures

		undoTasks.clear();
		redoTasks.clear();
		mapDeletedTaskWithIndex.clear();
		bt = (Button) findViewById(R.id.button);
		tv = (TextView) findViewById(R.id.editText);
		lv = (ListView) findViewById(R.id.task_list);
		gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);

		//Load the gesture library
		gestureLibrary.load();

		//Get the GestureOverlayView
		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.item_gestureOverlay);
		//Add the Listener for when a gesture is performed
		gestures.addOnGesturePerformedListener(this);
		populateListView();
		bt.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v)
			{
				EditText et = (EditText)findViewById(R.id.editText);
				String input = et.getText().toString();

				if(input!=null && input.length() > 0)
				{
					ToDoList.add(new Task(index,input,R.drawable.phone,false, false));
					adapter.notifyDataSetChanged();
					lv.setSelection(adapter.getCount() - 1);
					index++;
					saveTasksToFile();
					et.setText("");
				}

			}
		});//bt.setOnClickListener
		gotoDate(getDate(dayShift));

	}
	 @Override
     public boolean onCreateOptionsMenu(Menu menu)
     {
      menu.add("Help");
      return true;
     }
	 @Override
	 public boolean onMenuItemSelected(int featureId, MenuItem item) {
	  // custom dialog
	     final Dialog dialog = new Dialog(this);	
	     dialog.setContentView(R.layout.custom);
             dialog.setTitle("Gestures Help");
	  // set the custom dialog components - text, image and button
//	  			TextView text = (TextView) dialog.findViewById(R.id.text);
	  			TextView text1 = (TextView) dialog.findViewById(R.id.text1);
	  			TextView text2 = (TextView) dialog.findViewById(R.id.text2);
	  			TextView text3 = (TextView) dialog.findViewById(R.id.textView3);
	  			TextView text4 = (TextView) dialog.findViewById(R.id.text4);
	  			TextView text5 = (TextView) dialog.findViewById(R.id.text5);
     	  		Button dialogButton = (Button) dialog.findViewById(R.id.buttonok);
	  			dialogButton.setOnClickListener(new OnClickListener() {
	  				@Override
	  				public void onClick(View v) {
	  					dialog.dismiss();
	  				}
	  			});
	  		        dialog.setCancelable(true);
	  			dialog.show();
	     
	     
 	     return true;
	 }

	public String getDate(int dayShift){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, dayShift);
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c.getTime());
		return formattedDate;
	}

	public void gotoNextDate(){
		++dayShift;
		gotoDate(getDate(dayShift));
	}

	public void gotoPreviousDate(){
		--dayShift;
		gotoDate(getDate(dayShift));
	}

	public void goToHome()
	{
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c.getTime());
		dayShift=0;
		gotoDate(formattedDate);
	}

	public void gotoDate(String date){
		TextView textDate= (TextView) findViewById(R.id.textViewDate);
		textDate.setText(date);
		loadTasksFromFile();

	}

	//saves all tasks in the current shown list into a seprate file for that date
	private void saveTasksToFile(){
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDoX");
		dir.mkdirs();
		File file = new File(dir, getDate(dayShift) +".txt");
			try {
				FileOutputStream f = new FileOutputStream(file,false);
				PrintWriter pw = new PrintWriter(f);

				for(Task task: ToDoList){
					pw.println(""+task.getDesc()+" "+task.Completed);
					//pw.println(task.Completed);
				}

				pw.flush();
				pw.close();
				f.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadTasksFromFile(){
		ToDoList.clear();
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDoX");
			dir.mkdirs();
			File file = new File(dir, getDate(dayShift) +".txt");
			try{
				FileInputStream f = new FileInputStream(file);
				DataInputStream in = new DataInputStream(f);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				boolean comp = false;
				//Read File Line By Line
				while ((strLine = br.readLine()) != null) {
					String[] splitted = strLine.split("\\s+");
					if(splitted.length>0)
						//if(splitted[1].equals("true"))
						if(splitted[splitted.length-1].equals("true"))//test last word 	
						  {comp = true;}
						else
						{
							comp= false;//4. else not complete
						}
						//start-add this to include all words
						String result = splitted[0];
						for(int i=1; i< splitted.length-1;i++)
						{
							result+=" "+splitted[i];
						}		
						//end
					//ToDoList.add(new Task(1,splitted[0],R.drawable.phone,false,comp));
					ToDoList.add(new Task(1,result,R.drawable.phone,false,comp));//3 add entire string
				}
				in.close();
				f.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		adapter.notifyDataSetChanged();
		ListView list = (ListView) findViewById(R.id.task_list);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Task selectedTask = ToDoList.get(position); 
				//	Toast.makeText(getBaseContext(), "selected "+mapDeletedTaskWithIndex.size()+" Task(s)", Toast.LENGTH_SHORT).show();
				if(!selectedTasks.contains(selectedTask))
				{
					selectMode=1;	
					selectedTasks.add(selectedTask);
					selectedTask.Selected = true;
					
				}
				else
				{
					selectedTasks.remove(selectedTask);
					if(selectedTasks.size()==0)
						selectMode=0;
					selectedTask.Selected = false;	
					
				}
				adapter.notifyDataSetChanged();

			}
		});
	}

	@Override
	public void onStart()
	{
		super.onStart();
		bt = (Button) findViewById(R.id.button);
		tv = (TextView) findViewById(R.id.editText);
		//tv.setCursorVisible(false);
	}
	//aditi end

	private void blink(){
	    final Handler handler = new Handler();
	    new Thread(new Runnable() {
	    	
	        @Override	        
	        public void run() {
	        int timeToBlink = 200;    //in milissegunds
	        try{Thread.sleep(timeToBlink);}catch (Exception e) {}
	            handler.post(new Runnable() {
	                @Override
	                    public void run() {
	                    TextView txt = (TextView) findViewById(R.id.textViewDate);
	                    if(txt.getVisibility() == View.VISIBLE){
	                        txt.setVisibility(View.INVISIBLE);
	                    }else{
	                        txt.setVisibility(View.VISIBLE);
	                    }
	                    count+=1;
	                    if(count < 7)
	                    	blink();
	                    else {
	                    	txt.setVisibility(View.VISIBLE);
	                    	count =0;
	                    }
	                }
	                });
	            }
	        }).start();
	    	
	    }

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		//Try to recognize the gesture
		ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
		// Did we get any predictions?
		if (predictions.size() > 0) {
			//note: the list of predictions is already sorted
			//fetch best prediction
			Prediction prediction = predictions.get(0);
			// Anything over a score of 1.0 is considered a good match
			if (prediction.score > 1.0) {
				String predictionName = prediction.name;
				//Toast toast;
				//				if(prediction.name.equals("clockwise_circle")) {
				//					mp.start();
				//					Toast toast = Toast.makeText(getBaseContext(), prediction.name+", "+prediction.score , Toast.LENGTH_SHORT);
				
				if(selectMode == 1) {
					mapDeletedTaskWithIndex.clear();

					if(predictionName.equals("cross"))
					{
					Collections.sort(selectedTasks, new Comparator<Task>(){
					public int compare(Task t1, Task t2)
					{
						if(ToDoList.indexOf(t1)> ToDoList.indexOf(t2))
						{
							return 1;
						}
						else if(ToDoList.indexOf(t1)< ToDoList.indexOf(t2))
						{
							return -1;
						}
						else
						{
							return 0;
						}
					}
					
					}
					);
						
						if(selectedTasks!=null && selectedTasks.size()!=0)
						{
							for(int i1=0;i1<selectedTasks.size();i1++)
							{							
								mapDeletedTaskWithIndex.put(selectedTasks.get(i1), ToDoList.indexOf(selectedTasks.get(i1)));
								ToDoList.remove(selectedTasks.get(i1));								
								ToDoList.indexOf(selectedTasks.get(i1));

							}							
							adapter.notifyDataSetChanged();
							Toast.makeText(getBaseContext(), "Deleted "+selectedTasks.size()+" Task(s)", Toast.LENGTH_SHORT).show();
							undoTasks.clear(); //reset
							for(Task t: selectedTasks){
								t.Selected = false;
								undoTasks.add(t);
							}
							last_task = "delete";							

						}
						saveTasksToFile();
						selectMode=0;
						selectedTasks.clear();
					} else if(predictionName.equals("tick")) {
						if(selectedTasks!=null && selectedTasks.size()!=0)
						{
							for(int i1=0;i1<selectedTasks.size();i1++)
							{							
								ToDoList.get(ToDoList.indexOf(selectedTasks.get(i1))).Completed=true;								
								ToDoList.get(ToDoList.indexOf(selectedTasks.get(i1))).Selected=false;
							}

							adapter.notifyDataSetChanged();

							Toast.makeText(getBaseContext(), "Task(s) Completed", Toast.LENGTH_SHORT).show();
							undoTasks.clear(); //reset
							for(Task t: selectedTasks){
								t.Selected = false;
								undoTasks.add(t);
							}
							last_task = "complete";

						}
						saveTasksToFile();
						selectMode=0;
						mapDeletedTaskWithIndex.clear();
						selectedTasks.clear();
					}
					else	{

						for(int i1=0;i1<selectedTasks.size();i1++)
						{										
							ToDoList.get(ToDoList.indexOf(selectedTasks.get(i1))).Selected=false;
						}
						selectedTasks.clear();
						adapter.notifyDataSetChanged();
						Toast.makeText(getBaseContext(), "Invalid Gesture", Toast.LENGTH_SHORT).show();
						selectMode=0;
						
					}
				}
				else{
					if(predictionName.equals("left")){
						undoTasks.clear(); //reset
						redoTasks.clear();
						gotoPreviousDate();
						blink();
					} else if(predictionName.equals("right")){
						undoTasks.clear(); //reset
						redoTasks.clear();
						gotoNextDate();
						blink();
					} else if(predictionName.equals("anticlockwise") || predictionName.equals("undo")){
						if(undoTasks!=null && undoTasks.size()>0 )
						{				

							if (last_task.equals("delete")){
								//ToDoList.addAll(undoTasks);

								// tesing selected tasks
								String tasks =null;
								for(Task t: mapDeletedTaskWithIndex.keySet())
								{
									tasks+=t.getDesc();
								}	
								Toast.makeText(getBaseContext(), ""+ mapDeletedTaskWithIndex.size(), Toast.LENGTH_SHORT).show();
								for(Map.Entry<Task, Integer> entry: mapDeletedTaskWithIndex.entrySet())
								{
									ToDoList.add(entry.getValue(), entry.getKey());
								}

							} else if(last_task.equals("complete")) {
								Toast.makeText(getBaseContext(),"Undo", Toast.LENGTH_SHORT).show();
								for(int i=0; i<undoTasks.size();i++){
									ToDoList.get((ToDoList.indexOf(undoTasks.get(i)))).Completed = false;

								}														
							}
							for(Task t: undoTasks){
								redoTasks.add(t);
							}
							undoTasks.clear();
							undo_task = last_task;
							last_task = null;
							adapter.notifyDataSetChanged();
							saveTasksToFile();
						}							
					} else if(predictionName.equals("clockwise") || predictionName.equals("redo")){
						if(redoTasks!=null && redoTasks.size()>0 )
						{										
							if (undo_task.equals("delete")){
								for(int i1=0;i1<redoTasks.size();i1++)
								{							
									ToDoList.remove(redoTasks.get(i1));									
								}								
								Toast.makeText(getBaseContext(),"Redo", Toast.LENGTH_SHORT).show();												
							} else if(undo_task.equals("complete")) {
								Toast.makeText(getBaseContext(),"Redo", Toast.LENGTH_SHORT).show();
								for(int i=0;i<redoTasks.size();i++){
									ToDoList.get((ToDoList.indexOf(redoTasks.get(i)))).Completed = true;
								}														
							}
							for(Task t: redoTasks){
								undoTasks.add(t);
							}
							redoTasks.clear();
							last_task = undo_task;
							undo_task = null;							
							adapter.notifyDataSetChanged();
							saveTasksToFile();
						}
					}
					else if(predictionName.equals("home"))
					{
						undoTasks.clear(); //reset
						redoTasks.clear();
						goToHome();
						blink();
					}
				}
			}
		}
	}
	private void populateListView(){
		adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.task_list);
		list.setAdapter(adapter);
	}

	private class MyListAdapter extends ArrayAdapter<Task>{
		ListView list = (ListView) findViewById(R.id.task_list);
		public MyListAdapter(){
			super(MainActivity.this, R.layout.item_view, ToDoList);

		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			LayoutInflater li = LayoutInflater.from(getBaseContext());
			if(itemView == null)
				itemView = li.inflate(R.layout.item_view, parent, false);
			Task currentTask = ToDoList.get(position);

			TextView Desctext = (TextView) itemView.findViewById(R.id.item_desc);
			Desctext.setText(currentTask.getDesc());

			if(currentTask.isSelected())
				itemView.setBackgroundResource(android.R.color.holo_blue_bright);
			else
				itemView.setBackgroundResource(0);

			if(currentTask.isCompleted()){
				Desctext.setTextColor(Color.GREEN);
				//idtext.setTextColor(Color.GREEN);
			}
			else{
				Desctext.setTextColor(Color.BLACK);
				//idtext.setTextColor(Color.BLACK);
			}

			return itemView;
		}

	}



}

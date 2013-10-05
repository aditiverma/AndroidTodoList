package com.example.com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Inflate the menu; this adds items to the action bar if it is present.
		setContentView(R.layout.activity_main);
		//Get gesture library from res/raw/gestures

		bt = (Button) findViewById(R.id.button);
		gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		
		//Load the gesture library
		gestureLibrary.load();
	
		//Get the GestureOverlayView
		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.item_gestureOverlay);
		//Add the Listener for when a gesture is performed
		gestures.addOnGesturePerformedListener(this);
		
		populateToDoList();
		populateListView();
		
	 bt.setOnClickListener(new Button.OnClickListener() {
				
			    public void onClick(View v)
				{
				EditText et = (EditText)findViewById(R.id.editText);
			    String input = et.getText().toString();
			    
			    if(input!=null && input.length() > 0)
			    {
			    	
			    	ToDoList.add(new Task(index,input,R.drawable.phone));
			        adapter.notifyDataSetChanged();
			        index++;
			      //  ListView list = (ListView) findViewById(R.id.task_list);
					//list.setAdapter(adapter);
			        et.setText("");
			    }
				
				}
			   });//bt.serOnClickListener
		
		
	}
	
	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		//Try to recognize the gesture
		ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);		
		// Did we get any predictions?
		if (predictions.size() > 0) {			
			//note: the list of predictions is already sorted
			Prediction prediction = predictions.get(0);
			// Anything over a score of 1.0 is considered a good match
			//if (prediction.score > 7.5) {
				//if(prediction.name.equals("clockwise_circle")) {				
					//mp.start();
					Toast toast = Toast.makeText(getBaseContext(), prediction.name+", "+prediction.score , Toast.LENGTH_SHORT);
					toast.show();
				//}
				//}
			//else if (prediction.score > 6 && (prediction.name.equals("hyphen"))){
			//	Toast toast = Toast.makeText(getBaseContext(), prediction.name, Toast.LENGTH_SHORT);
			//	toast.show();
			//}
		}
		
	}
	
	private void populateToDoList(){
		//EditText text = (EditText)findViewById(R.id.item_id);
		//String abc= text .getText().toString(); 
		
		ToDoList.add(new Task(1,"abc", R.drawable.shopping));
		ToDoList.add(new Task(2,"Study NUI", R.drawable.study));
		ToDoList.add(new Task(3,"Clean Kitchen", R.drawable.cleaning));
		ToDoList.add(new Task(4,"Call Home",R.drawable.phone));
		ToDoList.add(new Task(5,"Call Home1",R.drawable.phone));
		ToDoList.add(new Task(6,"Call Home2",R.drawable.phone));
		ToDoList.add(new Task(7,"Call Home3",R.drawable.phone));
		ToDoList.add(new Task(8,"Call Home4",R.drawable.phone));
		ToDoList.add(new Task(9,"Call Home5",R.drawable.phone));
		ToDoList.add(new Task(10,"Call Home6",R.drawable.phone));
		ToDoList.add(new Task(11,"Call Home7",R.drawable.phone));
		ToDoList.add(new Task(12,"Call Home8",R.drawable.phone));
	}
	
	private void populateListView(){
		adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.task_list);
		list.setAdapter(adapter);
	}
	
	private class MyListAdapter extends ArrayAdapter<Task>{
		
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
		
			ImageView imageview = (ImageView)itemView.findViewById(R.id.item_icon);
			imageview.setImageResource(currentTask.getIconID());
			
			TextView Desctext = (TextView) itemView.findViewById(R.id.item_desc);
			Desctext.setText(currentTask.getDesc());
			
			TextView idtext = (TextView) itemView.findViewById(R.id.item_id);
			idtext.setText(currentTask.getTaskID()+".");			
			
						
			return itemView;
		}
	
	}
	
 
 
}

package com.game.pileon;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Reader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import junit.framework.Assert;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

@TargetApi(11)
public class MainGame extends Activity
implements View.OnTouchListener
{

	private GameEngine mGameEngine;
	private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
	private DragLayer mDragLayer;             // The ViewGroup that supports drag-drop.
	private PointTracker mPointTracker;
	private TextView mPointView;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mGameEngine = new GameEngine();
		
		setContentView(R.layout.activity_game_screen);
		mDragController = new DragController(this);
		
		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
	    mDragLayer.setDragController(mDragController);
	    
	    setupViews();
	    
	    //must be run after setupViews has initialized the text view for the point tracker
	    mPointTracker = new PointTracker(mPointView); 
	    mGameEngine.setPointTracker(mPointTracker);
	    
	    //test
	    writeTestData();
	    readTestData();
	}
	/**
	 * One-time setup of initial PileViews and HandViews. After this is done, the GameEngine will update
	 * the PileViews and HandViews with the underlying card(s) as the game progresses
	 */
	public void setupViews()
	{
		//setup PileViews - three total
		PileView pileView0 = new PileView(this, mGameEngine.Pile0);
		pileView0.setGameEngine(mGameEngine);
	    DragLayer.LayoutParams pileView0params = new DragLayer.LayoutParams(pileView0.getDrawable().getIntrinsicWidth(), 
	    		pileView0.getDrawable().getIntrinsicHeight(), 120, 320);
	    mDragLayer.addView(pileView0, pileView0params);
	    mDragController.addDropTarget(pileView0);
	    
	    PileView pileView1 = new PileView(this, mGameEngine.Pile1);
	    pileView1.setGameEngine(mGameEngine);
	    DragLayer.LayoutParams pileView1params = new DragLayer.LayoutParams(pileView1.getDrawable().getIntrinsicWidth(), 
	    		pileView1.getDrawable().getIntrinsicHeight(), 280, 320);
	    mDragLayer.addView(pileView1, pileView1params);
	    mDragController.addDropTarget(pileView1);
	    
	    PileView pileView2 = new PileView(this, mGameEngine.Pile2);
	    pileView2.setGameEngine(mGameEngine);
	    DragLayer.LayoutParams pileView2params = new DragLayer.LayoutParams(pileView2.getDrawable().getIntrinsicWidth(), 
	    		pileView2.getDrawable().getIntrinsicHeight(), 420, 320);
	    mDragLayer.addView(pileView2, pileView2params);
	    mDragController.addDropTarget(pileView2);

	    //setup HandViews - five total
	    HandView handView0 = new HandView(this, mGameEngine.Hand0);
		handView0.setOnTouchListener(this);
	    DragLayer.LayoutParams handView0params = new DragLayer.LayoutParams(handView0.getDrawable().getIntrinsicWidth(), 
	    		handView0.getDrawable().getIntrinsicHeight(), 60, 120);
	    mDragLayer.addView(handView0, handView0params);
		handView0.setHand(mGameEngine.Hand0);
		
		HandView handView1 = new HandView(this, mGameEngine.Hand1);
		handView1.setOnTouchListener(this);
	    DragLayer.LayoutParams handView1params = new DragLayer.LayoutParams(handView1.getDrawable().getIntrinsicWidth(), 
	    		handView1.getDrawable().getIntrinsicHeight(), 180, 120);
	    mDragLayer.addView(handView1, handView1params);
	    handView1.setHand(mGameEngine.Hand1);
		
	    HandView handView2 = new HandView(this, mGameEngine.Hand2);
	    handView2.setOnTouchListener(this);
	    DragLayer.LayoutParams handView2params = new DragLayer.LayoutParams(handView2.getDrawable().getIntrinsicWidth(), 
	    		handView2.getDrawable().getIntrinsicHeight(), 300, 120);
	    mDragLayer.addView(handView2, handView2params);
	    handView2.setHand(mGameEngine.Hand2);
	    
	    HandView handView3 = new HandView(this, mGameEngine.Hand3);
	    handView3.setOnTouchListener(this);
	    DragLayer.LayoutParams handView3params = new DragLayer.LayoutParams(handView3.getDrawable().getIntrinsicWidth(), 
	    		handView3.getDrawable().getIntrinsicHeight(), 420, 120);
	    mDragLayer.addView(handView3, handView3params);
	    handView3.setHand(mGameEngine.Hand3);
	    
	    HandView handView4 = new HandView(this, mGameEngine.Hand4);
	    handView4.setOnTouchListener(this);
	    DragLayer.LayoutParams handView4params = new DragLayer.LayoutParams(handView4.getDrawable().getIntrinsicWidth(), 
	    		handView4.getDrawable().getIntrinsicHeight(), 540, 120);
	    mDragLayer.addView(handView4, handView4params);
	    handView4.setHand(mGameEngine.Hand4);
	    
	    //setup the point tracker view
	    mPointView = new TextView(this);
	    int pointViewWidth = 200;
	    int pointViewHeight = 60;
	    mPointView.setText("test text please ignore");
	    DragLayer.LayoutParams mPointViewParams = new DragLayer.LayoutParams(pointViewWidth, pointViewHeight, 300, 500 );
	    mDragLayer.addView(mPointView, mPointViewParams);
	}
	
	public void backToMain(View view)
	{
		//TODO insert code to save the state of the game
		Intent intent = new Intent(MainGame.this, GameMenu.class);
		startActivity(intent);
	}
	
	public boolean onTouch(View v, MotionEvent ev) 
	{
	    final int action = ev.getAction();

	    if (action == MotionEvent.ACTION_DOWN) {
	       startDrag(v);
	    }
	    
	    return true;
	}
	
	public void updatePoints(Card cardPlayed, Card pileCard){
		mPointTracker.processMove(cardPlayed, pileCard);
	}
	
	public void writeTestData(){
		Serializer serializer = new Persister();
		Example example = new Example("Test message", 123);
//		File result = new File("/data/com.game.pileon/assets/example.xml");
		
		try
		{
			FileOutputStream result = openFileOutput("example.xml", Context.MODE_PRIVATE);
			try
			{
				serializer.write(example, result);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
	}
	
	public void readTestData(){
		Serializer serializer = new Persister();
		//File source = new File("/data/com.game.pileon/assets/example.xml");
		
		TextView testTextView = new TextView(this);
	    int testTextViewWidth = 200;
	    int testTextViewHeight = 60;
	    DragLayer.LayoutParams testTextViewParams = new DragLayer.LayoutParams(testTextViewWidth, testTextViewHeight, 300, 600 );
	    mDragLayer.addView(testTextView, testTextViewParams);
	    
		try
		{
			FileInputStream source = openFileInput("example.xml");
			try
			{
				Example example = serializer.read(Example.class, source);
			    testTextView.setText(example.toString());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}	
	}
	
	/**
	 * Start dragging a view.
	 *
	 */    

	public boolean startDrag (View v)
	{
	    // Let the DragController initiate a drag-drop sequence.
	    // I use the dragInfo to pass along the object being dragged.
	    // I'm not sure how the Launcher designers do this.
	    Object dragInfo = v;
	    Log.i("PO Drag", "startDrag in MainGame runs");
	    mDragController.startDrag (v, mDragLayer, dragInfo, DragController.DRAG_ACTION_MOVE);
	    return true;
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
	}



}
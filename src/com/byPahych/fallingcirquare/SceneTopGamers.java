package com.byPahych.fallingcirquare;

import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class SceneTopGamers extends CameraScene implements IScrollDetectorListener, IOnAreaTouchListener, IOnSceneTouchListener{

	 private static final float FREQ_D = 120.0f;
	 
	 private static final int STATE_WAIT = 0;
	 private static final int STATE_SCROLLING = 1;
	 private static final int STATE_MOMENTUM = 2;
	 private static final int STATE_DISABLE = 3;
	 
	 private static int WRAPPER_HEIGHT = 2000;
	 private static final float MAX_ACCEL = 5000;
	 private static final double FRICTION = 1.0f;
	 
	 private TimerHandler thandle;
	 private int mState = STATE_DISABLE;
	 private double accel, accel1, accel0;
	 private float mCurrentY;
	 private IEntity mWrapper;
	 private SurfaceScrollDetector mScrollDetector;
	 public SceneManager parentScene;
	 public HUD myHUD = null;
	 private Sprite HUD_sprite;
	 private Sprite spriteConnection;
	 private long t0;
	 
	 public SceneTopGamers(SceneManager parentScene)
	 {
		super(MainGameActivity.camera);
		this.parentScene = parentScene;
		setBackgroundEnabled(false);	
	    this.mScrollDetector = new SurfaceScrollDetector(this);     
        this.mScrollDetector.setEnabled(true);
		this.setOnSceneTouchListener(this);	
		this.setOnAreaTouchListener(this);		
	 }
	 
	 private void InitTimer()
	 {
		 thandle = new TimerHandler(1.0f / FREQ_D, true, new ITimerCallback() {
			   @Override
			   public void onTimePassed(final TimerHandler pTimerHandler) {
				   doSetPos();
				   //pTimerHandler.reset();
			   }
		  });		 
	 }
	 	 
	 private void CreateTimerConnection()
	 {
		 final TimerHandler timer = new TimerHandler(0.1f, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				ConnectionInitialization();
				unregisterUpdateHandler(pTimerHandler);
			}
		});
		registerUpdateHandler(timer);
	 }
	 
	public void Show()
	{			
		setVisible(true);
		setIgnoreUpdate(false);
		Initial();	
		CreateTimerConnection();
	}

	public void Hide()
	{	
		ReleaseGame();
		setVisible(false);
		setIgnoreUpdate(true);		
	}
	 
	 private void Initial()
	 {		 
		 Text text = new Text(0, 20, MainGameActivity.mStrokeFontGame, "TOP GAMERS", MainGameActivity._main.getVertexBufferObjectManager());
		 text.setPosition(MainGameActivity.CAMERA_WIDTH/2.0f - text.getWidth()/2.0f, 10.0f);
		 text.setColor(Color.BLUE);
		 CreateHUD(MainGameActivity._main.getVertexBufferObjectManager());
		 mWrapper = new Entity(0, 0);
		 myHUD.attachChild(text);
		 //mScrollDetector.setEnabled(true);	
		 AddConnectionSprite();
		 spriteConnection.setVisible(false);
	 }
	 
	 private void ConnectionInitialization()
	 {
		 //AddConnectionSprite();
		 spriteConnection.setVisible(true);
		 ReadDataFromServer();
	 }
	 
	 private void AddConnectionSprite()
	 {
		 spriteConnection = new Sprite(100, 300, 350, 120, MainGameActivity.mRegionConnect, MainGameActivity._main.getVertexBufferObjectManager());
		 spriteConnection.setX(MainGameActivity.CAMERA_WIDTH/2.0f - spriteConnection.getWidth()/2.0f);
		 //spriteConnection.setScale(2.0f);
		 attachChild(spriteConnection);
		 MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
		    {
			 public void run()
			    {
				 setOnSceneTouchListener(null);	
				 setOnAreaTouchListener(null);
			    }
	    });
	 }
	 
	 private void RemoveConnectionSprite()
	 {
		 spriteConnection.setVisible(false);	
		 MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
		    {
			 public void run()
			    {
				  setOnSceneTouchListener(SceneTopGamers.this);	
				  setOnAreaTouchListener(SceneTopGamers.this);
			    }
	    });
	  }
	 
	 private void ReadDataFromServer()
	 {
			MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
		    {
		        public void run()
		        {
		        	try
		        	{
		        	ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
		        	query.orderByDescending("score");
		        	query.setLimit(50);
		        	List<ParseObject> scoreList = query.find();
		        	String[] Names = null;
		        	int[] Scores = null;
		        	if (scoreList != null && scoreList.size() > 0)
		        	{
		        		Names = new String[scoreList.size()];
		        		Scores = new int[scoreList.size()];
		        		for (int i = 0; i < scoreList.size(); i++)
		        		{
		        			ParseObject ob = scoreList.get(i);
		        			Names[i] = (String)ob.get("name");
		        			Scores[i] = ob.getInt("score");
		        		}
		        	}
		        	RemoveConnectionSprite();
		        	if (scoreList == null)
		        		CreateBox(0, Names, Scores, MainGameActivity._main.getVertexBufferObjectManager());
		        	else
		        		CreateBox(scoreList.size(), Names, Scores, MainGameActivity._main.getVertexBufferObjectManager());
		        	}
		        	catch (Exception e)
		        	{
		        		MainGameActivity._main.runOnUiThread(new Runnable() {		
							@Override
							public void run() {		
				        		MainGameActivity._main.onCreateDialog(1).show();				        		
		    		        }
		    	  	    });
		        		parentScene.ShowMainMenu();
		        	}
		        }
	  	    });
	 }
	 
	 private void SaveDataInServer(final String Name)
	 {		
		 MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
		 {
			 public void run()
		        {
				 try
		        	{
					 if (Integer.valueOf(MainGameActivity.Record) <= Integer.valueOf(MainGameActivity.Record_to_server))
					 {
						 MainGameActivity._main.runOnUiThread(new Runnable() {		
								@Override
								public void run() {		
								 MainGameActivity._main.onCreateDialog(2).show();								 
								  }
				  	     });
						 return;		
					 }
					 spriteConnection.setVisible(true);
		        	 ParseObject gameScore = new ParseObject("GameScore");
		        	 gameScore.put("name", Name);
		        	 gameScore.put("score", Integer.valueOf(MainGameActivity.Record));
		        	 gameScore.save();
		        	 RemoveConnectionSprite();
		        	 MainGameActivity.Record_to_server = MainGameActivity.Record;
		        	 MainGameActivity._main.SaveGameInfo();
		        	 RefreshData();
		        	}
		        	catch (Exception e)
		        	{
		        		MainGameActivity._main.runOnUiThread(new Runnable() {		
							@Override
							public void run() {	
									MainGameActivity._main.onCreateDialog(1).show();
							 }
				  	     });		
		        		if (spriteConnection != null)
		        			RemoveConnectionSprite();
		        	}
		        }
  	     });		       
	 }
	 
	 private void RefreshData()
	 {
		 MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
		 {
		    public void run()
		    {
				 mWrapper.detachChildren();
				 detachChild(mWrapper);
				 ConnectionInitialization();
		    }
		 });
	 }
	 
	 private void ReleaseGame()
	 {
		 MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
			 {
			    public void run()
			    {
					 //mScrollDetector.setEnabled(false);
					 RemoveHUD();
					 if (thandle != null)
						 unregisterUpdateHandler(thandle);	
					 if (mWrapper != null)
					 {
						 
								 mWrapper.detachChildren();
								 mWrapper.clearUpdateHandlers();	
								 mWrapper.clearEntityModifiers();
								 mWrapper.dispose();
						   
						 detachChild(mWrapper);
					 }
					 detachChildren();
				 }
			 });		 
	 }
	 
	 private void CreateHUD(VertexBufferObjectManager vertexBufferObjectManager)
	 {
		myHUD = new HUD();
		HUD_sprite = new Sprite(0, 0, MainGameActivity.mRegionHUD, vertexBufferObjectManager)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {				
				return true;
			}
		};
		HUD_sprite.setWidth(MainGameActivity.CAMERA_WIDTH);
		HUD_sprite.setHeight(MainGameActivity.CAMERA_HEIGHT * 0.1f);
		myHUD.attachChild(HUD_sprite);
		CreateButton(vertexBufferObjectManager);
		MainGameActivity.camera.setHUD(myHUD);
	 }
	 
	 private void CreateBox(int count, String[] Names, int[] Scores, VertexBufferObjectManager pVertexBufferObjectManager)
	 {
		 if (count == 0)
		 {
			 WRAPPER_HEIGHT = 700;
			 Rectangle r = new Rectangle(17.5f, 100, 445, 80, pVertexBufferObjectManager);
			 r.setColor(1, 1, 0);
			 Text num = new Text( 20.0f, 100 + 10.0f, MainGameActivity.mFontMenu, "EMPTY", pVertexBufferObjectManager);
			 num.setX(r.getX() + r.getWidth()/2.0f - num.getWidth()/2.0f);
		     mWrapper.attachChild(r); 
			 mWrapper.attachChild(num);	
		 }
		 else
		 {
			 WRAPPER_HEIGHT = 90 * count;
			 for (int i = 0; i < count; i++) 
			 {
				  Rectangle r = new Rectangle(17.5f, i * 100 + 100, 445, 80, pVertexBufferObjectManager);
				  r.setColor(1, 1, 0);
				  Text num = new Text( 20.0f, i * 100 + 100 + 10.0f, MainGameActivity.mFontMenu,
						  String.valueOf(i+1) + ".\tName: " + Names[i] + "\n\tScore: " + Scores[i], pVertexBufferObjectManager);
				  mWrapper.attachChild(r); 
				  mWrapper.attachChild(num);			   
			  }
		 }
		  attachChild(mWrapper);
		  InitTimer();
		  registerUpdateHandler(thandle);		  		  
		  mState = STATE_WAIT;
		  
	 }
	 
	 private void CreateButton(VertexBufferObjectManager pVertexBufferObjectManager)
	 {
		 TiledSprite spriteAdd = new TiledSprite(10, 0.0f, MainGameActivity.mRegionHUD_element, pVertexBufferObjectManager)
		 {
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					if(pSceneTouchEvent.isActionDown())
					{
						showTextInput();
						return true;
					}
					else if(pSceneTouchEvent.isActionUp())
					{										
						return true;
					}
					return false;
				}
		};		 
		spriteAdd.setWidth(MainGameActivity.CAMERA_HEIGHT * 0.1f);
		spriteAdd.setHeight(MainGameActivity.CAMERA_HEIGHT * 0.1f);
		spriteAdd.setCurrentTileIndex(1);
		registerTouchArea(spriteAdd);
		myHUD.attachChild(spriteAdd);			
		 TiledSprite spriteRefresh = new TiledSprite(125.0f, 0.0f, MainGameActivity.mRegionHUD_element, pVertexBufferObjectManager)
		 {
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					if(pSceneTouchEvent.isActionDown())
					{
						RefreshData();
						return true;
					}
					else if(pSceneTouchEvent.isActionUp())
					{										
						return true;
					}
					return false;
				}
		};		 		
		spriteRefresh.setWidth(MainGameActivity.CAMERA_HEIGHT * 0.1f);
		spriteRefresh.setHeight(MainGameActivity.CAMERA_HEIGHT * 0.1f);
		spriteRefresh.setX(MainGameActivity.CAMERA_WIDTH - spriteRefresh.getWidth() - 10.0f);
		registerTouchArea(spriteRefresh);
		myHUD.attachChild(spriteRefresh);	
		registerTouchArea(HUD_sprite);
	 }
	 
	private void RemoveHUD()
	{
		if (myHUD == null)
			return;
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
		{
			public void run()
	        {	
				myHUD.detachChildren();
				MainGameActivity.camera.setHUD(new HUD());
	        }  
		});		
	}
	 
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (mState == STATE_DISABLE)
			   return true;
			  
			  if (mState == STATE_MOMENTUM) {
			   accel0 = accel1 = accel = 0;
			   mState = STATE_WAIT;
			  }
			
			  mScrollDetector.onTouchEvent(pSceneTouchEvent);
			  return true;
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		  t0 = System.currentTimeMillis();
		  mState = STATE_SCROLLING;		
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		  long dt = System.currentTimeMillis() - t0;
		  if (dt == 0)
		   return;
		  double s =  pDistanceY / (double)dt * 1000.0;  // pixel/second
		  accel = (accel0 + accel1 + s) / 3;
		  accel0 = accel1;
		  accel1 = accel;
		  
		  t0 = System.currentTimeMillis();
		  mState = STATE_SCROLLING;		
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		mState = STATE_MOMENTUM;
		
	}

	protected synchronized void doSetPos() {
		  
		  if (accel == 0) {
		   return;
		  }
	  
		  if (mCurrentY > 0) {
		   mCurrentY = 0;
		   mState = STATE_WAIT;
		   accel0 = accel1 = accel = 0;
		  }
		  if (mCurrentY < -WRAPPER_HEIGHT) {
		   mCurrentY = -WRAPPER_HEIGHT;
		   mState = STATE_WAIT;
		   accel0 = accel1 = accel = 0;
		  }
		  mWrapper.setPosition(0, mCurrentY);
		  
		  if (accel < 0 && accel < -MAX_ACCEL)
		   accel0 = accel1 = accel = - MAX_ACCEL;
		  if (accel > 0 && accel > MAX_ACCEL)
		   accel0 = accel1 = accel = MAX_ACCEL;
		  
		  double ny = accel / FREQ_D;
		  if (ny >= -1 && ny <= 1) {
		   mState = STATE_WAIT;
		   accel0 = accel1 = accel = 0;
		   return;
		  }
		  if (! (Double.isNaN(ny) || Double.isInfinite(ny)))
		   mCurrentY += ny;
		  accel = (accel * FRICTION);
	 }
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void showTextInput() {
        MainGameActivity._main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(MainGameActivity._main);

                        alert.setTitle("Name");
                        alert.setMessage("Input Your Name");

                        final EditText editText = new EditText(MainGameActivity._main);
                        editText.setTextSize(20f);
                        editText.setGravity(Gravity.CENTER_HORIZONTAL);
                        alert.setView(editText);

                        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                        String Name = editText.getText().toString();
                                        if (Name != null && Name.length() > 0)
                                        	SaveDataInServer(Name);
                                }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                        });

                        final AlertDialog dialog = alert.create();
                        dialog.setOnShowListener(new OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                        editText.requestFocus();
                                        final InputMethodManager imm = (InputMethodManager) MainGameActivity._main.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                                }
                        });
                        dialog.show();
                }
        });	
	}
}

package com.byPahych.fallingcirquare;

import java.util.Iterator;
import java.util.Stack;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class SceneGame extends CameraScene implements SensorEventListener, IOnAreaTouchListener, IOnSceneTouchListener {

	public static int STATE_GAME_INITIAL = 0;
	public static int STATE_GAME_START = 1;
	public static int STATE_GAME_FINISH = 2;
	public int STATE_GAME = 0;	
	
	public static PhysicsWorld mPhysicsWorld;	
	private boolean _BtnTouched_left = false;
	private boolean _BtnTouched_right = false;
	
	SceneManager parentScene;	
	private SensorManager sensorManager;
	ButtonSpriteWithText buttonGO = null;
	TiledSprite buttonTRANSform = null;
	
	Ball ball = null;	
	public float position_roof = 0;
	
	public HUD myHUD = null;
	private Sprite HUD_sprite2;
	private TimerHandler TimerTimeTick;
	private IUpdateHandler UpdateScene;	
	private BoardManager mBoardManager;
	public Text TextValue;
	public Text TextCountCoin;
	private int value = 0;
	public int value_coin = 0;
	public float g_gravity;
	private Sprite FON;
	private Border border_manager;
	public int length_way = 0;		// ïðîéäåííûé øàðèêîì ïóòü, èíêðåìèðóåòñÿ â BoardManager 
	private int length_way_old = 0;
	private int delta_length = 0;
	private float center_x;
	PauseScene pauseScene;
	private TiledSprite[] circleBoomIndicate;
	public Sprite spriteBoomTitle;
	public int count_bomb = 0;
	
	public SceneGame(SceneManager parentScene) {
		super(MainGameActivity.camera);
		this.setCamera(MainGameActivity.camera);
		this.parentScene = parentScene;
		setBackgroundEnabled(false);	
		this.setOnAreaTouchListener(this);
		sensorManager = (SensorManager)MainGameActivity._main.getSystemService(Context.SENSOR_SERVICE);	
		GameMainFunctions.scene = this;		
		pauseScene = new PauseScene(this);
		FON = new Sprite(0, 0, MainGameActivity.mRegionFON, MainGameActivity._main.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
					return true;
				else if(pSceneTouchEvent.isActionMove())
					return true;
				return false;
			}
		};				
		FON.setHeight(MainGameActivity.CAMERA_HEIGHT);
		FON.setWidth(MainGameActivity.CAMERA_WIDTH);
		FON.setAlpha(0.8f);
		center_x = getX() + MainGameActivity.CAMERA_WIDTH/2.0f;
	}
	
	public void Show()
	{	
		InitialGame();
		setVisible(true);
		setIgnoreUpdate(false);
	}

	public void Hide()
	{	
		ReleaseGame();
		setVisible(false);
		setIgnoreUpdate(true);		
	}
				
	public void SetGravity(float value)
	{
		g_gravity = value;
		SceneGame.mPhysicsWorld.setGravity(new Vector2(0, value));
	}
	
	public void CreateStartScene(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		myHUD.setVisible(false);
		registerTouchArea(FON);
		attachChild(FON);
		CreateStartButton(pVertexBufferObjectManager);
		registerTouchArea(buttonGO);
		FON.attachChild(buttonGO);
		Text Title = new Text(0, 0, MainGameActivity.mStrokeFontGame, "Ready", pVertexBufferObjectManager);
		Title.setColor(0, 1, 1);
		Title.setScale(1.5f);
		Title.setX(MainGameActivity.CAMERA_WIDTH/2.0f - Title.getWidth()/2.0f);
		Title.setY(30.0f);
		FON.attachChild(Title);		
		Text Record = new Text(buttonGO.getX(), Title.getY() + Title.getHeight() + 40.0f, MainGameActivity.mStrokeFontGame, "Record: " + MainGameActivity.Record, pVertexBufferObjectManager);
		Record.setColor(0, 1, 1);
		FON.attachChild(Record);
	}
	
	public void RemoveStartScene()
	{
		unregisterTouchArea(buttonGO);
		unregisterTouchArea(FON);		
		FON.detachChildren();
		detachChild(FON);
		myHUD.setVisible(true);
	}
	
	public void CreateGameOverScene(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		MainGameActivity._main.displayInterstitial();
		myHUD.setVisible(false);
		registerTouchArea(FON);
		attachChild(FON);
		CreateStartButton(pVertexBufferObjectManager);
		registerTouchArea(buttonGO);
		FON.attachChild(buttonGO);
		Text Title = new Text(0, 0, MainGameActivity.mStrokeFontGame, "Game Over", pVertexBufferObjectManager);
		Title.setColor(0, 1, 1);
		Title.setScale(1.5f);
		Title.setX(MainGameActivity.CAMERA_WIDTH/2.0f - Title.getWidth()/2.0f);
		Title.setY(30.0f);
		FON.attachChild(Title);		
		Text Result = new Text(buttonGO.getX(), Title.getY() + Title.getHeight() + 40.0f, MainGameActivity.mStrokeFontGame, "Your result: " + value, pVertexBufferObjectManager);
		Result.setColor(0, 1, 1);
		FON.attachChild(Result);
		Text Record = new Text(buttonGO.getX(), Result.getY() + Result.getHeight() + 10.0f, MainGameActivity.mStrokeFontGame, "Record: " + MainGameActivity.Record, pVertexBufferObjectManager);
		Record.setColor(0, 1, 1);
		FON.attachChild(Record);
		Text textCountBoard = new Text(buttonGO.getX(), buttonGO.getY() + buttonGO.getHeight() + 20.0f, MainGameActivity.mStrokeFontGame, "Boards: " + length_way, pVertexBufferObjectManager);
		textCountBoard.setColor(0, 1, 1);
		FON.attachChild(textCountBoard);
	}

	public void InitialGame()
	{
		STATE_GAME = STATE_GAME_INITIAL;		
		setPosition(0, 0);
		count_bomb = 0;
		this.length_way = 0;
		this.length_way_old = 0;
		this.delta_length = GameOptions.DEFAULT_START_DELTA_LENGTH;
		VertexBufferObjectManager pVertexBufferObjectManager = MainGameActivity._main.getVertexBufferObjectManager();
		g_gravity = GameOptions.DEFAULT_START_GRAVITY;
		GameMainFunctions.CreatePhysicsWorld(new Vector2(0, g_gravity));
		CreateHUD(pVertexBufferObjectManager);
		CreateRectangles(pVertexBufferObjectManager);	
		
		_BtnTouched_left = false;
		_BtnTouched_right = false;
		CreateStartScene(pVertexBufferObjectManager);
	}
	
	public void Start()
	{				
		STATE_GAME = STATE_GAME_START;
		CreateBall(MainGameActivity._main.getVertexBufferObjectManager());
		CreateUpdateScene();
		if (GameOptions.IsAccelerometer)
		{			
	        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
			this.setOnSceneTouchListener(null);
		}
		else
		{
			sensorManager.unregisterListener(this);
			this.setOnSceneTouchListener(this);	
		}
		CreateBoardManager();
		CreateTimeTickHandler();		
		RemoveStartScene();	
		// .... ÇÀÃÐÓÇÊÀ ÌÅÆÑÒÐÀÍÈ×ÍÎÃÎ ÎÁÚßÂËÅÍÈÅ ....
		MainGameActivity._main.LoadAdvert();
	}	
	
	public void Pause()
	{
		if (STATE_GAME != STATE_GAME_INITIAL)
			pauseScene.Pause();
	}
	
	public void ReleaseGame()
	{				
		pauseScene.Resume();
		parentScene.setBackground(new Background(1.0f, 1.0f, 1.0f));
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {	        	
	        	RemoveTimeTick();	        	
		    	RemoveUpdateScene();
				RemoveBall();
				RemoveBoardManager();
				RemoveRectangles();	
				RemoveStartScene();	
				RemoveHUD();				
				GameMainFunctions.RemovePhysicsWorld();				
				clearEntityModifiers();		
				clearTouchAreas();
				detachChildren();
				clearUpdateHandlers();		
				if (GameOptions.IsAccelerometer)	
	    			sensorManager.unregisterListener(SceneGame.this);	
	    		else
	    			SceneGame.this.setOnSceneTouchListener(null);
	        }
	    });
		this.value = 0;
		this.value_coin = 0;
		this.length_way = 0;
		this.length_way_old = 0;
		count_bomb = 0;
		this.delta_length = GameOptions.DEFAULT_START_DELTA_LENGTH;
		setPosition(0, 0);
		position_roof = 0;
		STATE_GAME = STATE_GAME_INITIAL;
		g_gravity = GameOptions.DEFAULT_START_GRAVITY;
		System.gc();
	}	
	
	public void NewGame()
	{
		STATE_GAME = STATE_GAME_INITIAL;
		parentScene.setBackground(new Background(1.0f, 1.0f, 1.0f));
		if (GameOptions.IsAccelerometer)	
			sensorManager.unregisterListener(this);	
		else
			this.setOnSceneTouchListener(null);
		count_bomb = 0;
		setPosition(0, 0);
		position_roof = 0;
		//RemoveTimeTick();
		RemoveUpdateScene();
		RemoveBoardManager();	
		RemoveRectangles();		
		if (Integer.valueOf(MainGameActivity.Record) < value)
		{
			MainGameActivity.Record = String.valueOf(value);
			MainGameActivity._main.SaveGameInfo();
		}
		VertexBufferObjectManager pVertexBufferObjectManager = MainGameActivity._main.getVertexBufferObjectManager();
		CreateRectangles(pVertexBufferObjectManager);				
		CreateGameOverScene(pVertexBufferObjectManager);
		for (int i = 0; i < 3; i++)
			circleBoomIndicate[i].setCurrentTileIndex(0);
		//	rectangleBoomIndicate[i].setColor(Color.WHITE);
		this.length_way = 0;
		this.length_way_old = 0;
		this.delta_length = GameOptions.DEFAULT_START_DELTA_LENGTH;
		buttonTRANSform.setCurrentTileIndex(1);
		value = 0;
		value_coin = 0;
		_BtnTouched_left = false;
		_BtnTouched_right = false;
		if (TextValue != null)
		{
			TextValue.setText(Integer.toString(value));
			TextValue.setColor(Color.BLACK);
		}
		if (TextCountCoin != null)
			TextCountCoin.setText(":" + Integer.toString(value_coin));		
		g_gravity = GameOptions.DEFAULT_START_GRAVITY;
		SceneGame.mPhysicsWorld.setGravity(new Vector2(0, g_gravity));
	}
	
	private void UpdateValueState()
	{
		if (ball == null || ball.STATE_BALL == Ball.STATE_BALL_TRANSFORM)
			return;
		float speed = ball.GetSpeedY();
		if (speed < 1.0f)
			return;
		if (speed < GameOptions.array_speed_point_bonus[0])
			value += 1;
		else if (speed < GameOptions.array_speed_point_bonus[1])
			value += 3;
		else if (speed < GameOptions.array_speed_point_bonus[2])
			value += 6;
		else if (speed < GameOptions.array_speed_point_bonus[3])
			value += 10;
		else
			value += 20;
		TextValue.setText(Integer.toString(value));
	}
	
	public void UpdateValueState(int value)
	{
		this.value += value;
		TextValue.setText(Integer.toString(this.value));
	}
	
	// Âûçûâàåòñÿ Coin ïî çàâåðøåíèè àíèìàöèè
	public void UpdateTextCoin()
	{
		if (TextCountCoin == null)
			return;
		value_coin += 1;
		if (value_coin == 10)
			buttonTRANSform.setCurrentTileIndex(6);
		else if (value_coin == 20)
			buttonTRANSform.setCurrentTileIndex(9);
		else if (value_coin == 40)
			buttonTRANSform.setCurrentTileIndex(11);
		else if (value_coin == 70)
			buttonTRANSform.setCurrentTileIndex(15);
		if (value_coin >= 100 && ball.STATE_BALL == Ball.STATE_BALL_CIRCLE)
		{
			Transformer();
		}
		TextCountCoin.setText(":" + Integer.toString(value_coin));
	}
	
	private void CreateBoardManager()
	{
		mBoardManager = new BoardManager(0, MainGameActivity.CAMERA_WIDTH, MainGameActivity.CAMERA_HEIGHT,
				GameOptions.DEFAULT_START_BOARD_SPEED, GameOptions.DISTANCE_CREATE_BOARD, this);
	}
	
	private void RemoveBoardManager()
	{
		if (mBoardManager == null)
			return;
		mBoardManager.Release();
		mBoardManager = null;
	}
	
	public void CreateBall(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		ball = new Ball(100, 150, MainGameActivity.mRegionUsualBall, pVertexBufferObjectManager, this);		
	}
	
	public void RemoveBall()
	{
		if (ball == null)
			return;
		ball.Release();
	}
	
	private void CreateRectangles(VertexBufferObjectManager vertexBufferObjectManager)
	{
		border_manager = new Border(vertexBufferObjectManager, this, 0, 
				MainGameActivity.CAMERA_WIDTH, HUD_sprite2.getHeight(), MainGameActivity.CAMERA_HEIGHT - HUD_sprite2.getHeight());
	}
		
	private void RemoveRectangles()
	{
		if (border_manager == null)
			return;
		border_manager.Remove();
	}
		
	private void CreateStartButton(VertexBufferObjectManager vertexBufferObjectManager)
	{
		buttonGO = new ButtonSpriteWithText(MainGameActivity.CAMERA_WIDTH/2.0f - 200.0f, MainGameActivity.CAMERA_HEIGHT/2.0f - 100.0f, MainGameActivity.mRegionButton,  vertexBufferObjectManager,
				"START", MainGameActivity.mFontMenu, 400, 200)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
				{
					this.setCurrentTileIndex(1);
					return true;
				}
				else if(pSceneTouchEvent.isActionUp())
				{
					this.setCurrentTileIndex(0);
					if (STATE_GAME == STATE_GAME_INITIAL)
						Start();
					return true;
				}
				return false;
			}
		};
	}
	
	private void CreateHUD(VertexBufferObjectManager vertexBufferObjectManager)
	{
		myHUD = new HUD();
		HUD_sprite2 = new Sprite(0, 0, MainGameActivity.mRegionHUD, vertexBufferObjectManager)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {				
				return true;
			}
		};
		HUD_sprite2.setWidth(MainGameActivity.CAMERA_WIDTH);
		HUD_sprite2.setHeight(MainGameActivity.CAMERA_HEIGHT * 0.1f);
		myHUD.attachChild(HUD_sprite2);
		MainGameActivity.camera.setHUD(myHUD);
		
		buttonTRANSform = new TiledSprite(10.0f, 5.0f, MainGameActivity.mRegionUsualBall, vertexBufferObjectManager)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
				{					
					if (value_coin < GameOptions.MIN_COUNT_TRANSFORM_COINS || ball.STATE_BALL == Ball.STATE_BALL_SQUARE)
						return true;
					Transformer();
					return true;
				}
				else if(pSceneTouchEvent.isActionUp())
				{					
					return true;
				}
				return false;
			}
		};
		buttonTRANSform.setWidth(70);
		buttonTRANSform.setHeight(70);
		buttonTRANSform.setCurrentTileIndex(1);
		registerTouchArea(buttonTRANSform);
		myHUD.attachChild(buttonTRANSform);		
		Text TextTitleValue = new Text(MainGameActivity.CAMERA_WIDTH - 120.0f, 4.0f, MainGameActivity.mFontMenu, 
				"Score:", vertexBufferObjectManager);
		TextTitleValue.setScale(1.2f);
		TextValue = new Text(MainGameActivity.CAMERA_WIDTH - 120.0f, 36.0f, MainGameActivity.mFontMenu, "0000000", 
				vertexBufferObjectManager);
		TextValue.setColor(Color.BLACK);
		TextValue.setText("0");		
		TextValue.setScale(1.2f);
		myHUD.attachChild(TextTitleValue);
		myHUD.attachChild(TextValue);
		TiledSprite spritePause = new TiledSprite (TextTitleValue.getX() - 80.0f, buttonTRANSform.getY() + 5.0f,
				60.0f, 60.0f, MainGameActivity.mRegionPause, vertexBufferObjectManager)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
				{
					Pause();
					return true;
				}
				return false;
			}
		};
		myHUD.attachChild(spritePause);
		registerTouchArea(spritePause);	
		Sprite TileCoin = new Sprite(buttonTRANSform.getX() + buttonTRANSform.getWidth() + 20.0f, 12.0f,
				MainGameActivity.mRegionCoin, vertexBufferObjectManager);
		TileCoin.setWidth(30.0f);
		TileCoin.setHeight(30.0f);
		TextCountCoin = new Text(buttonTRANSform.getX() + buttonTRANSform.getWidth() + 20.0f, 36.0f, MainGameActivity.mFontMenu, "000", 
				vertexBufferObjectManager);
		TextCountCoin.setText(":0");
		myHUD.attachChild(TileCoin);
		myHUD.attachChild(TextCountCoin);
		CreateBoomIndicator(TileCoin.getX() + TileCoin.getWidth() + 50.0f, 5.0f, vertexBufferObjectManager);
		registerTouchArea(HUD_sprite2);
		
	}
		
	private void CreateBoomIndicator(float x, float y, VertexBufferObjectManager vertexBufferObjectManager)
	{
		spriteBoomTitle = new Sprite(x , y, MainGameActivity.mRegionIndicate, vertexBufferObjectManager)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
				{
					Boom();
					return true;
				}
				return false;
			}
		};
		spriteBoomTitle.setWidth(70.0f);
		spriteBoomTitle.setHeight(70.0f);
		myHUD.attachChild(spriteBoomTitle);
		registerTouchArea(spriteBoomTitle);	
		circleBoomIndicate = new TiledSprite[3];
		circleBoomIndicate[0] = new TiledSprite(spriteBoomTitle.getX() + 22.0f, spriteBoomTitle.getY() + 5.0f,
				24, 24, MainGameActivity.mRegionIndicate, vertexBufferObjectManager);
		circleBoomIndicate[1] = new TiledSprite(spriteBoomTitle.getX() + 9.0f, spriteBoomTitle.getY() + 32.0f,
				24, 24, MainGameActivity.mRegionIndicate, vertexBufferObjectManager);
		circleBoomIndicate[2] = new TiledSprite(spriteBoomTitle.getX() + 36.0f, spriteBoomTitle.getY() + 32.0f,
				24, 24, MainGameActivity.mRegionIndicate, vertexBufferObjectManager);

		myHUD.attachChild(circleBoomIndicate[0]);
		myHUD.attachChild(circleBoomIndicate[1]);
		myHUD.attachChild(circleBoomIndicate[2]);
	}
	
	private void RemoveHUD()
	{
		if (myHUD == null)
			return;
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
		{
			public void run()
	        {					
				myHUD.clearTouchAreas();
				myHUD.clearUpdateHandlers();
				myHUD.detachChildren();
				MainGameActivity.camera.setHUD(new HUD());
	        }  
		});		
	}
	
	public void RefreshTileBoomState()
	{
		switch (count_bomb)
		{
		case 0:
			circleBoomIndicate[0].setCurrentTileIndex(0);
			circleBoomIndicate[1].setCurrentTileIndex(0);
			circleBoomIndicate[2].setCurrentTileIndex(0);
			break;
		case 1:
			circleBoomIndicate[0].setCurrentTileIndex(1);
			circleBoomIndicate[1].setCurrentTileIndex(0);
			circleBoomIndicate[2].setCurrentTileIndex(0);
			break;
		case 2:
			circleBoomIndicate[0].setCurrentTileIndex(1);
			circleBoomIndicate[1].setCurrentTileIndex(1);
			circleBoomIndicate[2].setCurrentTileIndex(0);
			break;
		case 3:
			circleBoomIndicate[0].setCurrentTileIndex(1);
			circleBoomIndicate[1].setCurrentTileIndex(1);
			circleBoomIndicate[2].setCurrentTileIndex(1);
			break;
			default:
				break;
		}
	}
	
	private void Boom()
	{
		if (count_bomb == 0 || STATE_GAME == STATE_GAME_FINISH)
			return;
		count_bomb--;
		RefreshTileBoomState();
		mBoardManager.BoomActivated(ball);
	}
	
	private void CreateTimeTickHandler()
	{
		TimerTimeTick = new TimerHandler(0.2f, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{				
				UpdateValueState();
				pTimerHandler.reset();				
			}
		});		
		this.registerUpdateHandler(TimerTimeTick);		
	}
	
	private void RemoveTimeTick()
	{
		GameMainFunctions.RemoveTimerHandler(TimerTimeTick);
	}
	
	private void UpdateCameraState()
	{
		// ÑÌÅÙÅÍÈÅ ÊÀÌÅÐÛ
		if (ball.getY() - position_roof > 600.0f)
		{
			float delta = ball.getY() - position_roof - 600.0f;					
			setPosition(getX(), getY() - delta);
			position_roof += delta;
			border_manager.UpdateBorder(delta/32.0f);
			mBoardManager.UpdateY(delta);
		}		
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {		
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			if (pSceneTouchEvent.getY() > 600.0f)
			{
				Boom();
			}
			if (pSceneTouchEvent.getX() < center_x)
				_BtnTouched_left = true;
			else
				_BtnTouched_right = true;
		}
		else if(pSceneTouchEvent.isActionUp())
		{
			_BtnTouched_right = false;
			_BtnTouched_left = false;
		}
		else if(pSceneTouchEvent.isActionMove())
		{			
			if (pSceneTouchEvent.getX() < center_x)
			{
				_BtnTouched_right = false;
				_BtnTouched_left = true;
			}
			else
			{
				_BtnTouched_left = false;
				_BtnTouched_right = true;
			}
		}
		return false;
	}
	
	public void Transformer()
	{
		if (mBoardManager == null)
			return;
		buttonTRANSform.setCurrentTileIndex(16);
		ball.Transformer(value_coin);
		value_coin = 0;
		TextCountCoin.setText(":0");
	}	
	
	private void CreateUpdateScene()
	{		
		registerUpdateHandler(UpdateScene = new IUpdateHandler() {		
			@Override
			public void reset() {			
			}		
			@Override
			public void onUpdate(float pSecondsElapsed) 
			{			
				if (STATE_GAME == STATE_GAME_FINISH)
				{									
					return;
				}						
				if (border_manager.IsBallCollideRoof(ball))
				{
					if (value_coin >= GameOptions.MIN_COUNT_TRANSFORM_COINS)
					{
						ball.STATE_BALL = Ball.STATE_BALL_TRANSFORM;
						ball.setY(ball.getY() + 10.0f);
						Transformer();
					}
					else if (ball.STATE_BALL == Ball.STATE_BALL_CIRCLE)
					{
						RemoveBall();
						RemoveTimeTick();
						STATE_GAME = STATE_GAME_FINISH;
						if (GameOptions.IsVibrations)
							MainGameActivity._main.mVibrator.vibrate(800);
						//MainGameActivity._main.LoadAdvert();
						parentScene.setBackground(new Background(0.8f, 0.3f, 0.3f));
						TimerHandler timer_end = new TimerHandler(3.0f, new ITimerCallback() {							
							@Override
							public void onTimePassed(final TimerHandler pTimerHandler) {	
								MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
							    {
							        public void run()
							        {	
										parentScene.setBackground(new Background(1.0f, 1.0f, 1.0f));
										NewGame();															
										unregisterUpdateHandler(pTimerHandler);	
							        }
							    });																
							}
						});
						registerUpdateHandler(timer_end);
					}
				}																	
				UpdateCameraState();				
				if (_BtnTouched_left)
				{		
					ball.MoveLeft();
				}
				else if (_BtnTouched_right)
				{	
					ball.MoveRight();
				}
				// ÅÑËÈ ìû óæå èãðàåì (ïëàòôîðìû äâèæóòñÿ)
				if (STATE_GAME == STATE_GAME_START)
				{
					mBoardManager.ChangeCollision(ball);	
					if (mBoardManager.IsPossibleAdd())
					{
						mBoardManager.AddBoard(MainGameActivity._main.getVertexBufferObjectManager());
						mBoardManager.Refresh();
						UpdateBoardManager();
					}
				}
				UpdateSpeed();		
			}
		});
	}
	
	private void RemoveUpdateScene()
	{
		GameMainFunctions.RemoveUpdateHandler(UpdateScene);
	}
	
	private void UpdateBoardManager()
	{
		switch (length_way)
		{
			case 100:
			{
				mBoardManager.UpdateDistanceBoard(1);
				break;
			}
			case 300:
			{
				mBoardManager.UpdateDistanceBoard(2);
				break;
			}
			case 500:
			{
				mBoardManager.UpdateDistanceBoard(3);
				break;
			}
			case 800:
			{
				mBoardManager.UpdateDistanceBoard(4);
				break;
			}
			default:
				break;
		}
	}
	
	private void UpdateSpeed()
	{
		if (length_way - length_way_old < delta_length)
			return;
		if (length_way >= 1000)
			delta_length = 120;
		else if (length_way >= 800)
			delta_length = 60;
		else if (length_way >= 600)
			delta_length = 50;
		else if (length_way >= 300)
			delta_length = 30;
		length_way_old = length_way;
		ball.UpdateSpeed(GameOptions.ball_delta_acceleration);
		mBoardManager.UpdateSpeed(GameOptions.board_delta_acceleration);
		SetGravity(g_gravity + GameOptions.gravity_delta);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		if (Math.abs(x) < 0.8f)
		{
			_BtnTouched_right = false;
			_BtnTouched_left = false;
		}
		else if(x > 0)
		{
			_BtnTouched_right = false;
			_BtnTouched_left = true;
		}
		else
		{
			_BtnTouched_left = false;
			_BtnTouched_right = true;
		}					
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
}

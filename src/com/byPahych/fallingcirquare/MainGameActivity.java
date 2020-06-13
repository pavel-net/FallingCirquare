package com.byPahych.fallingcirquare;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Currency;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.engine.splitscreen.SingleSceneSplitScreenEngine;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.PixelFormat;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.Parse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class MainGameActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener{


	public static final int CAMERA_WIDTH = 480;
	public static final int CAMERA_HEIGHT = 800;
	public static Camera camera;
		
	public static MainGameActivity _main;
	public static SceneManager _sceneManager;	
	
	private static BitmapTextureAtlas mAtlasButton;
	private static BitmapTextureAtlas mAtlasUsualBall;
	private static BitmapTextureAtlas mAtlasBoard;
	private static BitmapTextureAtlas mAtlasHUD;
	private static BitmapTextureAtlas mAtlasPause;
	private static BitmapTextureAtlas mAtlasHUD_element;
	private static BitmapTextureAtlas mAtlasConnect;
	private static BitmapTextureAtlas mAtlasGameObject;
	private static BitmapTextureAtlas mAtlasFON;
	private static BitmapTextureAtlas mAtlasIndicate;
	public static TiledTextureRegion mRegionButton;
	public static TiledTextureRegion mRegionUsualBall;
	public static TiledTextureRegion mRegionCoin;
	public static TiledTextureRegion mRegionSpeedBall;
	public static TiledTextureRegion mRegionDynamite;
	public static TiledTextureRegion mRegionHUD_element;
	public static TiledTextureRegion mRegionIndicate;
	public static TextureRegion mRegionBoard;
	public static TiledTextureRegion mRegionPause;
	public static TextureRegion mRegionConnect;
	public static TextureRegion mRegionFON;
	public static TextureRegion mRegionHUD;
	
	public static TiledTextureRegion mRegionBOOM;
	private static BitmapTextureAtlas mAtlasBOOM;
	public static TiledTextureRegion mRegionBOOM2;
	private static BitmapTextureAtlas mAtlasBOOM2;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;
	public static ITextureRegion mParticleTextureRegion;
	private BitmapTextureAtlas mBitmapTextureAtlasBoom;
	public static ITextureRegion mParticleTextureRegionBoom;
	
	public static Font mFontMenu;
	public static StrokeFont mStrokeFontGame;
	public static AnimatedSprite _sprite;
	public Vibrator mVibrator;	
	public static String FILENAME = "data_file_release";
	public static String Record;
	public static String Record_to_server;
	private InterstitialAd interstitial;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		Parse.initialize(this, "Ej4zXvtim2PVzgZcXE4yBeO5Sa6QYO47TK899fbr", "GOYvylP8BJB24jfWm8eEMJi1SkbEniEjJ1xikaqe");
		_main = this;
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,  new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	
	@Override
	public void onCreateResources() {
		mAtlasButton = new BitmapTextureAtlas(this.getTextureManager(), 512, 64, TextureOptions.BILINEAR);
		mAtlasIndicate = new BitmapTextureAtlas(this.getTextureManager(), 128, 64, TextureOptions.BILINEAR);
		mAtlasUsualBall = new BitmapTextureAtlas(this.getTextureManager(), 1680, 80, TextureOptions.BILINEAR);
		mAtlasBoard = new BitmapTextureAtlas(this.getTextureManager(), 256, 32, TextureOptions.BILINEAR);
		mAtlasPause = new BitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		mAtlasConnect = new BitmapTextureAtlas(this.getTextureManager(), 400, 100, TextureOptions.BILINEAR);
		mAtlasFON = new BitmapTextureAtlas(this.getTextureManager(), 480, 720, TextureOptions.BILINEAR);
		mAtlasHUD = new BitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		mAtlasGameObject = new BitmapTextureAtlas(this.getTextureManager(), 64, 192, TextureOptions.BILINEAR);
		mAtlasHUD_element = new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		mAtlasBOOM = new BitmapTextureAtlas(this.getTextureManager(), 1920, 128, TextureOptions.BILINEAR);
		mAtlasBOOM2 = new BitmapTextureAtlas(this.getTextureManager(), 1920, 128, TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mRegionBOOM = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasBOOM, this, "BOOM_1.png", 0, 0, 15, 1);
		mRegionBOOM2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasBOOM2, this, "BOOM_2.png", 0, 0, 15, 1);
		mRegionHUD_element = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasHUD_element, this, "hud_element.png", 0, 0, 2, 1);
		mRegionUsualBall = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasUsualBall, this, "usual_ball.png", 0, 0, 21, 1);
		mRegionButton = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasButton, this, "button.png", 0, 0, 2, 1);
		mRegionBoard = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAtlasBoard, this, "board.png", 0, 0);
		mRegionPause = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasPause, this, "paused.png", 0, 0, 2, 1);
		mRegionConnect = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAtlasConnect, this, "connect.png", 0, 0);
		mRegionFON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAtlasFON, this, "fon.png", 0, 0);
		mRegionHUD = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAtlasHUD, this, "my_HUD.png", 0, 0);
		mRegionCoin = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasGameObject, this, "coin.png", 0, 0, 1, 1);
		mRegionIndicate = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasIndicate, this, "indicate2.png", 0, 0, 2, 1);
		mRegionSpeedBall = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasGameObject, this, "fast.png", 0, 64, 1, 1);
		mRegionDynamite = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasGameObject, this, "bomba.png", 0, 128, 1, 1);
				
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mParticleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "particle.png", 0, 0);
		this.mBitmapTextureAtlas.load();
		this.mBitmapTextureAtlasBoom = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mParticleTextureRegionBoom = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlasBoom, this, "particle_point.png", 0, 0);
		this.mBitmapTextureAtlasBoom.load();
		
		mAtlasBOOM.load();
		mAtlasBOOM2.load();
		mAtlasButton.load();
		mAtlasUsualBall.load();
		mAtlasBoard.load();
		mAtlasIndicate.load();
		mAtlasPause.load();
		mAtlasConnect.load();
		mAtlasHUD.load();
		mAtlasHUD_element.load();
		mAtlasGameObject.load();
		mAtlasFON.load();
		
		FontFactory.setAssetBasePath("font/");
		final ITexture fontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		final ITexture strokeFontTexture2 = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);			
		mFontMenu = FontFactory.createFromAsset(this.getFontManager(), fontTexture, this.getAssets(), "ARIAL.TTF", 32.0f, true, Color.BLACK.getARGBPackedInt());
		mFontMenu.load();
		mStrokeFontGame  = FontFactory.createStrokeFromAsset(this.getFontManager(), strokeFontTexture2,this.getAssets(), 
				"Tepeno Sans Bold.ttf", 54.0f, true, 
				Color.WHITE.getARGBPackedInt(), 2.5f, Color.BLACK.getARGBPackedInt());	
		mStrokeFontGame.load();		
		InitialFileData();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());		
		_sceneManager = new SceneManager(this.getVertexBufferObjectManager());
		_sceneManager.setBackground(new Background(1.0f, 1.0f, 1.0f));
		_sceneManager.setOnAreaTouchListener(this);
		_sceneManager.setOnSceneTouchListener(this);
		return _sceneManager;
	}		
	
	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event)
	{
		if (KeyCode == KeyEvent.KEYCODE_BACK)
		{
			if (_sceneManager != null)
			{
				_sceneManager.KeyBackPressed(KeyCode, event);
			}
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public Handler toaster = new Handler(){      
    	@Override
        public void handleMessage(Message msg) {
         Toast.makeText(getBaseContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
    	}
	};
	
	public void makeToast(String str){
	     Message status = MainGameActivity.this.toaster.obtainMessage();
	     Bundle datax = new Bundle();
	     datax.putString("msg", str);
	     status.setData(datax);
	     MainGameActivity.this.toaster.sendMessage(status);
	 }

	public void SaveGameInfo()
	{
		String value_game_options = "";
		if (GameOptions.IsAccelerometer)
			value_game_options += "Y\n";
		else
			value_game_options += "N\n";
		if (GameOptions.IsVibrations)
			value_game_options += "Y";
		else
			value_game_options += "N";
		writeFile(Record + "\n" + Record_to_server + "\n" + value_game_options);
	}
	
	public void InitialFileData()
	{
		File file = new File(this.getFilesDir(), FILENAME);		
		if (!file.exists())
			CreateInitialFile();
		else
			readFile();
	}
	
	public void CreateInitialFile()
	{
		//writeFile("0\n-1\nN\nY");
		Record = "0";
		Record_to_server = "-1";
	}
	
	public void writeFile(final String data) {
		_main.runOnUiThread(new Runnable() {		
			@Override
			public void run() {	
				try {
					if (data == null)
						return;
				  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				      openFileOutput(FILENAME, MODE_PRIVATE)));		     
				  bw.write(data);		 
				  bw.close();
				} 
				catch (FileNotFoundException e) {
				  e.printStackTrace();
				} 
				catch (IOException e) {
				  e.printStackTrace();
				}
			 }
 	     });	
	}
	
	void readFile() {
		try 
		{
		  BufferedReader br = new BufferedReader(new InputStreamReader(
		      openFileInput(FILENAME)));
		  String str = "";
		  if((str = br.readLine()) != null)
			  Record = str;
		  else
			  Record = "0";
		  if((str = br.readLine()) != null)
			  Record_to_server = str;
		  else
			  Record_to_server = "-1";
		  if((str = br.readLine()) != null)
		  {		// Accelerometer
			  if (str.equalsIgnoreCase("Y"))
				  GameOptions.IsAccelerometer = true;
			  else
				  GameOptions.IsAccelerometer = false;
		  }
		  if((str = br.readLine()) != null)
		  {		// Vibrations
			  if (str.equalsIgnoreCase("Y"))
				  GameOptions.IsVibrations = true;
			  else
				  GameOptions.IsVibrations = false;
		  }
		} catch (FileNotFoundException e) {
		  e.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		}
	}
		
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	  	super.onCreate(savedInstanceState);
	    	
	  	 // Создание межстраничного объявления.
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId("ca-app-pub-9840453190384801/1002491777");	
	  }
	
	
	 public void LoadAdvert()
	 {	   
		 MainGameActivity._main.runOnUiThread(new Runnable() {
				@Override
				public void run() {		
					 // Создание запроса объявления.
				    AdRequest adRequest = new AdRequest.Builder()
				    .build();
				
				    // Запуск загрузки межстраничного объявления.
				    interstitial.loadAd(adRequest);		
				}				
			});	       
	 }
	  // Вызовите displayInterstitial(), когда будете готовы показать межстраничное объявление.
	  public void displayInterstitial() {
		  MainGameActivity._main.runOnUiThread(new Runnable() {
				@Override
				public void run() {		
					if (interstitial.isLoaded()) {
				      interstitial.show();
				    }		
				}				
			});	    
	  }
	
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    _sceneManager.Pause();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	switch(id)
    	{
    		case 1:
    			builder.setMessage("Problems with Internet connection")
    				.setPositiveButton("OK", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					}).setCancelable(false);
    			dialog = builder.create();
    			break;  		
    		case 2:
    			builder.setMessage("Beat your record to continue")
    				.setPositiveButton("OK", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					}).setCancelable(false);
    			dialog = builder.create();
    			break;  		
			default:
    	}
    	return dialog;
	}	
}

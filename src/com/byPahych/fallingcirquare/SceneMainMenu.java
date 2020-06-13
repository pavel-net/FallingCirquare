package com.byPahych.fallingcirquare;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;



public class SceneMainMenu extends CameraScene {

	SceneManager _parentManager;
	ButtonSpriteWithText Button_start = null;
	ButtonSpriteWithText Button_accelerometer = null;
	ButtonSpriteWithText Button_vibrate = null;
	ButtonSpriteWithText Button_go_top = null;
	ITiledTextureRegion pTextureRegionButton = null;
	Text text = null;
	
	public SceneMainMenu(VertexBufferObjectManager pVertexBufferObjectManager, SceneManager _parentManager) {
		super(MainGameActivity.camera);
		setBackgroundEnabled(false);
		this._parentManager = _parentManager;
		this.pTextureRegionButton = MainGameActivity.mRegionButton;
		Button_start = new ButtonSpriteWithText(MainGameActivity.CAMERA_WIDTH/2.0f, MainGameActivity.CAMERA_HEIGHT/2.0f - 150, this.pTextureRegionButton, pVertexBufferObjectManager,
				"Play", MainGameActivity.mFontMenu, 300, 100)
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
					SceneMainMenu.this._parentManager.ShowGame();
					return true;
				}
				return false;
			}
		};		
		Button_start.setX(MainGameActivity.CAMERA_WIDTH/2.0f - Button_start.getWidth()/2.0f);
		registerTouchArea(Button_start);
		attachChild(Button_start);	
		String text_value = "";
		if (GameOptions.IsAccelerometer)
			text_value = "Accelerometer: ON";
		else
			text_value = "Accelerometer: OFF";
		Button_accelerometer = new ButtonSpriteWithText(MainGameActivity.CAMERA_WIDTH/2.0f, Button_start.getY() + Button_start.getHeight() + 10.0f, this.pTextureRegionButton, pVertexBufferObjectManager,
				text_value, MainGameActivity.mFontMenu, 300, 100)
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
					GameOptions.IsAccelerometer = !GameOptions.IsAccelerometer;
					if (GameOptions.IsAccelerometer)
						Button_accelerometer.SetText("Accelerometer: ON");
					else
						Button_accelerometer.SetText("Accelerometer: OFF");
					MainGameActivity._main.SaveGameInfo();
					return true;
				}
				return false;
			}
		};		
		Button_accelerometer.setX(MainGameActivity.CAMERA_WIDTH/2.0f - Button_accelerometer.getWidth()/2.0f);
		registerTouchArea(Button_accelerometer);
		attachChild(Button_accelerometer);		
		text_value = "Vibrations: ON";
		if (!GameOptions.IsVibrations)
			text_value = "Vibrations: OFF";
		Button_vibrate = new ButtonSpriteWithText(MainGameActivity.CAMERA_WIDTH/2.0f, Button_accelerometer.getY() + Button_accelerometer.getHeight() + 10.0f, this.pTextureRegionButton, pVertexBufferObjectManager,
				text_value, MainGameActivity.mFontMenu, 300, 100)
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
					GameOptions.IsVibrations = !GameOptions.IsVibrations;
					if (GameOptions.IsVibrations)
						Button_vibrate.SetText("Vibrations: ON");
					else
						Button_vibrate.SetText("Vibrations: OFF");
					MainGameActivity._main.SaveGameInfo();
					return true;
				}
				return false;
			}
		};		
		Button_vibrate.setX(MainGameActivity.CAMERA_WIDTH/2.0f - Button_vibrate.getWidth()/2.0f);
		registerTouchArea(Button_vibrate);
		attachChild(Button_vibrate);		
		Button_go_top = new ButtonSpriteWithText(MainGameActivity.CAMERA_WIDTH/2.0f, Button_vibrate.getY() + Button_vibrate.getHeight() + 10.0f, this.pTextureRegionButton, pVertexBufferObjectManager,
				"High Scores", MainGameActivity.mFontMenu, 300, 100)
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
					SceneMainMenu.this._parentManager.ShowTopGamers();
					return true;
				}
				return false;
			}
		};		
		Button_go_top.setX(MainGameActivity.CAMERA_WIDTH/2.0f - Button_go_top.getWidth()/2.0f);
		registerTouchArea(Button_go_top);
		attachChild(Button_go_top);		
	}
	
	private void CreateRecordText(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		text = new Text(20, 100, MainGameActivity.mStrokeFontGame, "Record: " + MainGameActivity.Record, pVertexBufferObjectManager);
		text.setX(Button_accelerometer.getX());
		text.setColor(0,1,1);
		attachChild(text);
	}	
	
	private void RemoveText()
	{
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	SceneMainMenu.this.detachChild(text);
	        }
	    });		
	}
	
	public void Show()
	{
		CreateRecordText(MainGameActivity._main.getVertexBufferObjectManager());
		setVisible(true);
		setIgnoreUpdate(false);			
	}
	
	public void Hide()
	{
		RemoveText();
		setVisible(false);
		setIgnoreUpdate(true);
	}
	
}

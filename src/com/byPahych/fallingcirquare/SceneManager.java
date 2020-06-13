package com.byPahych.fallingcirquare;

import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.entity.scene.Scene;

import android.view.KeyEvent;

public class SceneManager extends Scene{

	public static final int SCENE_MAIN_MENU_STATE = 0; 
	public static final int SCENE_GAME_STATE = 2;
	public static final int SCENE_TOP_GAMERS = 3;
	public static int GAME_STATE = 0; 
	
	SceneMainMenu _sceneMainMenu;
	SceneGame _sceneGame;
	SceneTopGamers _sceneTop;
	public SceneManager(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		_sceneMainMenu = new SceneMainMenu(pVertexBufferObjectManager, this);
		_sceneGame = new SceneGame(this);
		_sceneTop = new SceneTopGamers(this);
		attachChild(_sceneMainMenu);
		attachChild(_sceneGame);
		attachChild(_sceneTop);
		ShowMainMenu();
	}
	
	public void ShowMainMenu()
	{
		if (GAME_STATE == SCENE_GAME_STATE)
			_sceneGame.Hide();
		else
			_sceneTop.Hide();
		_sceneMainMenu.Show();		
		GAME_STATE = SCENE_MAIN_MENU_STATE;
	}
		
	public void ShowGame()
	{
		_sceneMainMenu.Hide();	
		_sceneGame.Show();
		GAME_STATE = SCENE_GAME_STATE;
	}
			
	public void ShowTopGamers()
	{
		_sceneMainMenu.Hide();	
		_sceneTop.Show();
		GAME_STATE = SCENE_TOP_GAMERS;
	}
	
	@Override
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
		switch (GAME_STATE)
		{
			case SCENE_MAIN_MENU_STATE:
				_sceneMainMenu.onSceneTouchEvent(pSceneTouchEvent);
				break;
			case SCENE_GAME_STATE:
				_sceneGame.onSceneTouchEvent(pSceneTouchEvent);
				break;
			case SCENE_TOP_GAMERS:
				_sceneTop.onSceneTouchEvent(pSceneTouchEvent);
				break;
		}
		return super.onSceneTouchEvent(pSceneTouchEvent);
	}	
	
	public void KeyBackPressed(int KeyCode, KeyEvent event)
	{
		switch (GAME_STATE)
		{
			case SCENE_MAIN_MENU_STATE:
				MainGameActivity._main.onDestroy();
				break;
			case SCENE_GAME_STATE:
				ShowMainMenu();
				break;
			case SCENE_TOP_GAMERS:
				ShowMainMenu();
				break;
		}
	}
	
	public void Pause()
	{
		if (GAME_STATE == SCENE_GAME_STATE)
			_sceneGame.Pause();
	}
}

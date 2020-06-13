package com.byPahych.fallingcirquare;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;

public class PauseScene extends CameraScene {

	SceneGame scene;
	boolean isPaused = false;
	TiledSprite pauseButton;
	public PauseScene(SceneGame scene)
	{
		super(MainGameActivity.camera);
		this.scene = scene;
		setBackgroundEnabled(false);
		createPauseSprite(MainGameActivity.CAMERA_WIDTH/2.0f - 100.0f, MainGameActivity.CAMERA_HEIGHT/2.0f);
	}
	
	public void Pause()
	{
		setVisible(true);
		if(!isPaused) 
	    {
            isPaused = true;
            scene.setIgnoreUpdate(true);
            scene.setChildScene(this, false, true, true);
	    }
	}
	
	public void Resume()
	{
		if (isPaused)
		{
			isPaused = false;
            scene.clearChildScene();
            scene.setIgnoreUpdate(false);
		}
	}
	
	private void createPauseSprite(float pX, float pY) 
	{
		final PauseScene pause_scene = this;
		pauseButton = new TiledSprite(pX, pY, MainGameActivity.mRegionPause, MainGameActivity._main.getVertexBufferObjectManager()){
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {                                                 
                    switch(pSceneTouchEvent.getAction()) {
                            case TouchEvent.ACTION_DOWN:
                                    if(isPaused) {
                                            isPaused = false;
                                            scene.clearChildScene();
                                            scene.setIgnoreUpdate(false);
                                    }
                                    else {
                                            isPaused = true;
                                            scene.setIgnoreUpdate(true);
                                            scene.setChildScene(pause_scene, false, true, true);
                                    }
                                    break;
                            case TouchEvent.ACTION_MOVE:
                                    break;
                            case TouchEvent.ACTION_UP:
                                    break;
                    }
                    return true;
            }
		};
		pauseButton.setCurrentTileIndex(1);
		pauseButton.setPosition(MainGameActivity.CAMERA_WIDTH/2.0f - pauseButton.getWidth()/2.0f, 
				MainGameActivity.CAMERA_HEIGHT/2.0f- pauseButton.getHeight()/2.0f);
        pause_scene.registerTouchArea(pauseButton);
        pause_scene.attachChild(pauseButton);
	}
	
}

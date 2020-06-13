package com.byPahych.fallingcirquare;

import java.util.Iterator;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.os.storage.StorageManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class GameMainFunctions {

	public static Scene scene;
	
	public static void CreatePhysicsWorld(Vector2 vector)
	{
		SceneGame.mPhysicsWorld = new PhysicsWorld(vector, false);
		scene.registerUpdateHandler(SceneGame.mPhysicsWorld);
	}
	
	public static void RemovePhysicsWorld()
	{		
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	if (SceneGame.mPhysicsWorld == null)
	        		return;
            	SceneGame.mPhysicsWorld.clearForces();
            	SceneGame.mPhysicsWorld.clearPhysicsConnectors();
	        }
	    });
	}
	
	public static void RemoveObject(final IEntity entity, final Body body, final PhysicsConnector connector)
	{
		if (entity == null || !entity.hasParent())
			return;
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	RemovePhysicsObject(body, connector);
	        	scene.detachChild(entity);
		        entity.clearUpdateHandlers();	
		        entity.clearEntityModifiers();
		        entity.dispose();
	        }
  	    });
	}
	
	public static void RemovePhysicsObject(final Body body, final PhysicsConnector connector)
	{		
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	if (body == null || connector == null)
	        		return;
				SceneGame.mPhysicsWorld.unregisterPhysicsConnector(connector);
		        body.setActive(false);
		        SceneGame.mPhysicsWorld.destroyBody(body);		      
	        }
  	    });
	}
	
	public static void RemovePhysicsObject(final PhysicsConnector connector)
	{		
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	if (connector == null)
	        		return;
	        	Body body = connector.getBody();
	        	if (body == null || !body.isActive())
	        		return;
				SceneGame.mPhysicsWorld.unregisterPhysicsConnector(connector);
		        body.setActive(false);
		        SceneGame.mPhysicsWorld.destroyBody(body);		      
	        }
  	    });
	}
	
	public static void RemoveUpdateHandler(IUpdateHandler handler)
	{
		if (handler == null)
			return;
		scene.unregisterUpdateHandler(handler);
	}
	
	public static void RemoveTimerHandler(TimerHandler timer)
	{
		if (timer == null)
			return;
		scene.unregisterUpdateHandler(timer);
	}
	
	public static void RemoveText(final Text text, final SceneGame game)
	{
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	game.detachChild(text);
	        }
	    });		
	}
	
	public static void RemoveText(final Text text, final HUD game_hud)
	{
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	game_hud.detachChild(text);
	        }
	    });		
	}

	public static void AddTextMulti(CharSequence text, float scale, float ToScale, final HUD game_hud, Color color, IFont pFont,
			float time, float x, float y, float toY, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		final Text _text = new Text(x, y, pFont, text, pVertexBufferObjectManager);	
		_text.setScale(scale);
		_text.setColor(color);
		AlphaModifier alpha_mod = new AlphaModifier(time, 1.0f, 0.0f);
		MoveModifier move_mod = new MoveModifier(time, x, 350.0f, y, toY);
		ScaleModifier scale_mod = new ScaleModifier(time, scale, ToScale);
		ParallelEntityModifier ParalModif = new ParallelEntityModifier(alpha_mod, move_mod, scale_mod);
		IEntityModifier pEntityModifier = new SequenceEntityModifier(ParalModif)
		{			
	        @Override
	        protected void onModifierFinished(IEntity pItem)
	        {	       
	            super.onModifierFinished(pItem);
	            RemoveText(_text, game_hud);
	        }
		};				
		_text.registerEntityModifier(pEntityModifier);  	
		game_hud.attachChild(_text);		
	}
	
	public static void AddTextSpeed(CharSequence text, float y, float scale, float ToScale, final HUD game_hud, Color color, IFont pFont,
			float time, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		final Text _text = new Text(-300, y, pFont, text, pVertexBufferObjectManager);	
		_text.setScale(scale);
		_text.setColor(color);

		float toX = MainGameActivity.CAMERA_WIDTH/2.0f - _text.getWidth()/2.0f;
		AlphaModifier alpha_mod = new AlphaModifier(time, 1.0f, 0.0f);
		MoveXModifier move_mod = new MoveXModifier(0.5f, -300, toX);
		ScaleModifier scale_mod = new ScaleModifier(time, 1.0f, 1.5f);
		RotationModifier rotate = new RotationModifier(0.5f, 330.0f, 360.0f);
		ParallelEntityModifier ParalModif = new ParallelEntityModifier(move_mod, rotate);
		ParallelEntityModifier ParalModif2 = new ParallelEntityModifier(alpha_mod, scale_mod);
		IEntityModifier pEntityModifier = new SequenceEntityModifier(ParalModif, ParalModif2)
		{			
	        @Override
	        protected void onModifierFinished(IEntity pItem)
	        {	       
	            super.onModifierFinished(pItem);
	            RemoveText(_text, game_hud);
	        }
		};				
		_text.registerEntityModifier(pEntityModifier);   	
		game_hud.attachChild(_text);		
	}
	
	public static void AddTextCOMBO(CharSequence text, float y, final HUD game_hud, Color color, IFont pFont,
			float time, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		final Text _text = new Text(-300, y, pFont, text, pVertexBufferObjectManager);	
		_text.setColor(color);		
		_text.setX(MainGameActivity.CAMERA_WIDTH/2.0f - _text.getWidth()/2.0f);
		_text.setY(y + MainGameActivity.CAMERA_HEIGHT/2.0f - _text.getHeight()/2.0f);
		
		AlphaModifier alpha_mod = new AlphaModifier(1.0f, 0.0f, 0.5f);
		ScaleModifier scale_mod = new ScaleModifier(1.0f, 1.5f, 2.0f);
		RotationModifier rotate = new RotationModifier(0.5f, 345.0f, 375.0f);
		RotationModifier rotate2 = new RotationModifier(0.5f, 375.0f, 0.0f);
		AlphaModifier alpha_mod2 = new AlphaModifier(time, 0.5f, 1.0f);
		ScaleModifier scale_mod2 = new ScaleModifier(time, 2.0f, 3.0f);
		ParallelEntityModifier ParalModif = new ParallelEntityModifier(alpha_mod, scale_mod, rotate);
		ParallelEntityModifier ParalModif2 = new ParallelEntityModifier(alpha_mod2, scale_mod2, rotate2);
		IEntityModifier pEntityModifier = new SequenceEntityModifier(ParalModif, ParalModif2)
		{			
	        @Override
	        protected void onModifierFinished(IEntity pItem)
	        {	       
	            super.onModifierFinished(pItem);
	            RemoveText(_text, game_hud);
	        }
		};				
		_text.registerEntityModifier(pEntityModifier);   	
		game_hud.attachChild(_text);		
	}
}

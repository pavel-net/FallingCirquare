package com.byPahych.fallingcirquare;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Ball extends AnimatedSprite {

	public static int STATE_BALL_CIRCLE = 0;
	public static int STATE_BALL_SQUARE = 1;
	public static int STATE_BALL_TRANSFORM = 2;
	public int STATE_BALL = 0;
	private float MAX_SPEED_VALUE = GameOptions.MAX_SPEED_BALL;
	private float x;
	private float y;
	
	Body body;
	PhysicsConnector connector;
	SceneGame scene;
	int count_destroy_board = 0;
	boolean IsComboActivated = false;
	float acceleration = GameOptions.start_acceleration_ball;
	
	public Ball(float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, SceneGame scene) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.setScale(0.5f);		
		this.scene = scene;
		CreateCircleBody();
		this.scene.attachChild(this);
		MainGameActivity.camera.setChaseEntity(this);	    		
	}
	
	private void CreateCircleBody()
	{
		RemoveBody();
		STATE_BALL = STATE_BALL_CIRCLE;
		float width = this.getWidth();
		body = PhysicsFactory.createCircleBody(SceneGame.mPhysicsWorld, this.getX() + width/2.0f, this.getY() + width/2.0f,
				0.5f*width/2.0f, BodyType.DynamicBody, GameOptions.BALL_FIXTURE_DEF);
		body.setAngularDamping(10.0f);
		body.setLinearDamping(0.8f);
		MassData mass = new MassData();
		mass.mass = 10.0f;
		mass.I = 1.0f;	
		body.setMassData(mass);
		connector = new PhysicsConnector(this, body, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector);
		Stop();		
	}
	
	private void CreateSquareBody()
	{
		STATE_BALL = STATE_BALL_SQUARE;
		float width = this.getWidth();	
		body = PhysicsFactory.createBoxBody(SceneGame.mPhysicsWorld, this.getX() + width/2.0f, this.getY() + width/2.0f,
				0.3f*width/2.0f, 0.3f*width/2.0f, BodyType.DynamicBody, GameOptions.WALL_FIXTURE_DEF);
		MassData mass = new MassData();
		mass.mass = 1.0f;
		body.setMassData(mass);
		connector = new PhysicsConnector(this, body, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector);	
		Stop();
	}
	
	private void RemoveBody()
	{
		GameMainFunctions.RemovePhysicsObject(body, connector);
	}
		
	public void Transformer(final int point_value)
	{	
		scene.parentScene.setBackground(new Background(0.5f, 1.0f, 1.0f));
		StartTransformAnimation(point_value);
		this.IsComboActivated = true;
		Color color = new Color(0, 1, 1);
		GameMainFunctions.AddTextSpeed("TRANSFORM", scene.myHUD.getY() + 300.0f, 1.0f, 1.5f, 
				scene.myHUD, color, MainGameActivity.mStrokeFontGame, 2.0f, MainGameActivity._main.getVertexBufferObjectManager());
    }
		
	public void Release()
	{		
		final Ball ball = this;
		GameMainFunctions.RemoveObject(ball, body, connector);
	}
	
	public void UpdateSpeed(float delta_acceleration)
	{
		this.acceleration += delta_acceleration;
		this.MAX_SPEED_VALUE += 4.0f;
	}
	
	public void MoveLeft()
	{
		if (STATE_BALL == STATE_BALL_TRANSFORM)
			return;			
		Vector2 dir = body.getLinearVelocity();
		if (dir.x > -MAX_SPEED_VALUE)
			dir.x -= acceleration;				
		body.setLinearVelocity(dir);
	}
	public void MoveRight()
	{
		if (STATE_BALL == STATE_BALL_TRANSFORM)
			return;
		Vector2 dir = body.getLinearVelocity();
		if (dir.x < MAX_SPEED_VALUE)
			dir.x += acceleration;				
		body.setLinearVelocity(dir);
	}

	public float GetSpeedY()
	{
		float speed_y =  body.getLinearVelocity().y;
		if (speed_y <= 0)
			return 0;
		return speed_y;
	}	
	
	public Vector2 GetCenter()
	{
		return new Vector2(this.mX + this.getWidth()/2.0f, this.mY + this.getHeight()/2.0f);
	}
	
	public void Stop()
	{
		if (body != null)
			body.setLinearVelocity(0, 0);
	}
	
	private void StartTransformAnimation(final int point_value)
	{			
		STATE_BALL = STATE_BALL_TRANSFORM;
		GameOptions.IsAcceptAddSpeedBall = false;
		StartAnimation(point_value);
	}
	
	public void StartAnimation(final int point_value)
	{
		RemoveBody();
		count_destroy_board = 0;
		this.animate(50, false, new IAnimationListener() {

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {				
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {				
			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {	
			}

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {			
				setCurrentTileIndex(20);
				final float square_gravity = 3.0f;
				final float delta_gravity = scene.g_gravity - square_gravity;
				final float circle_acceleration = acceleration;
				acceleration = 1.0f;
				scene.SetGravity(square_gravity);
				CreateSquareBody();
				TimerHandler TimerTimeTick = new TimerHandler(((float)point_value)/5.0f, new ITimerCallback()
    			{
    				@Override
    				public void onTimePassed(final TimerHandler pTimerHandler)
    				{				
    					GameOptions.IsAcceptAddSpeedBall = true;
    					scene.SetGravity(scene.g_gravity + delta_gravity);
    					acceleration = circle_acceleration + acceleration - 1.0f;
    					setCurrentTileIndex(0);
    					CreateCircleBody();
    					StopDestroyBoard(scene.myHUD.getY() + 350);
    					unregisterUpdateHandler(pTimerHandler);		
    					scene.parentScene.setBackground(new Background(1.0f, 1.0f, 1.0f));
    				}
    			});   			
    			registerUpdateHandler(TimerTimeTick);
			}	        
	    });
	}
	
	public void AddDestroyBoard(int value)
	{
		this.count_destroy_board += value;
		if (this.count_destroy_board > 100 && this.IsComboActivated)
		{
			Combo();
			IsComboActivated = false;
		}
		float start_x = MainGameActivity.CAMERA_WIDTH/2.0f + 30.0f + count_destroy_board;
		Color color = Color.GREEN;
		GameMainFunctions.AddTextMulti("X" + String.valueOf(count_destroy_board), 0.8f, 1.0f, scene.myHUD, color, 
				MainGameActivity.mStrokeFontGame, 1.0f, start_x, 
				scene.TextValue.getY() + 200.0f, scene.TextValue.getY() + 50.0f, MainGameActivity._main.getVertexBufferObjectManager());		
	}
	
	private void StopDestroyBoard(float start_y)
	{
		Color color = Color.GREEN;
		scene.UpdateValueState(count_destroy_board * 10);
		GameMainFunctions.AddTextMulti("+" + String.valueOf(count_destroy_board * 10), 1.0f, 1.4f, scene.myHUD, color, 
				MainGameActivity.mStrokeFontGame, 3.0f, scene.myHUD.getX() + 300.0f, 
				start_y, scene.TextValue.getY() - 20.0f, MainGameActivity._main.getVertexBufferObjectManager());				
	}
	
	private void Combo()
	{
		Color color = new Color(0.0f, 1.0f, 1.0f);
		scene.UpdateValueState(1000);
		GameMainFunctions.AddTextCOMBO("COMBO", scene.myHUD.getY(), scene.myHUD, color, MainGameActivity.mStrokeFontGame,
				4.0f, MainGameActivity._main.getVertexBufferObjectManager());
		GameMainFunctions.AddTextMulti("+1000", 1.0f, 1.4f, scene.myHUD, color, 
				MainGameActivity.mStrokeFontGame, 3.0f, scene.myHUD.getX() + 300.0f, 
				scene.myHUD.getY() + 500.0f, scene.TextValue.getY() - 20.0f, MainGameActivity._main.getVertexBufferObjectManager());
	}
	
	public void BoomReaction()
	{
		if (STATE_BALL != STATE_BALL_CIRCLE)
			return;
		StopDestroyBoard(scene.myHUD.getY() + 350);
	}
	
	public void ClearCountDestroyBoard()
	{
		if (STATE_BALL != STATE_BALL_CIRCLE)
			return;
		count_destroy_board = 0;
	}
	
	public void GoAcceleration(final float value, float duration)
	{
		this.acceleration += value;
		TimerHandler TimerTimeTick = new TimerHandler(duration, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{				
				acceleration -= value;
				unregisterUpdateHandler(pTimerHandler);		
			}
		});   
		this.registerUpdateHandler(TimerTimeTick);
	}
}

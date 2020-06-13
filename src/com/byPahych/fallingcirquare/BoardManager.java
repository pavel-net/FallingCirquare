package com.byPahych.fallingcirquare;


import java.util.Iterator;
import java.util.LinkedList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class BoardManager {

	private int MIN_LENGTH = 100;
	private int MAX_LENGTH = 250;
	private float MIN_VALID_WIDTH = GameOptions.DEFAULT_MIN_SIZE_CLIFF;
	
	private float start_x;
	private float end_x;
	private float max_x;	// максимально значение x, для которого будем случайно генерировать платформу
	private float y;
	private float position_roof;
	private float speed;
	private float buffer_value;
	private LinkedList<Board> mListBoard;

	private SceneGame pScene;
	private final float WIDTH_BOARD = 10.0f;
	private GameObjectManager coinManager;
	private GameObjectManager ballSpeedManager;
	private GameObjectManager dynamiteManager;
	
	public BoardManager(float start_x, float end_x, float y, float speed, float buffer_value, SceneGame pScene)
	{
		this.pScene = pScene;
		this.start_x = start_x;
		this.end_x = end_x;
		this.max_x = end_x - MIN_LENGTH;
		this.buffer_value = buffer_value + WIDTH_BOARD;
		this.y = y + 50.0f;
		this.position_roof = y - MainGameActivity.CAMERA_HEIGHT - 50.0f;
		this.speed = speed;
		this.mListBoard = new LinkedList<Board>();
		this.coinManager = new GameObjectManager(pScene, this.position_roof);	
		this.ballSpeedManager = new GameObjectManager(pScene, this.position_roof); 
		this.dynamiteManager = new GameObjectManager(pScene, this.position_roof); 
	}
				
	public void UpdateY(float delta_y)
	{
		this.y += delta_y;
		this.position_roof += delta_y;
		coinManager.UpdateRoof(delta_y);
		ballSpeedManager.UpdateRoof(delta_y);
		dynamiteManager.UpdateRoof(delta_y);
	}
			
	public void UpdateDistanceBoard(int STEP_UPDATE)
	{
		switch (STEP_UPDATE) {
		case 1:
		{
			MIN_LENGTH = 160;
			buffer_value = GameOptions.DISTANCE_CREATE_BOARD + 20.0f;		
			break;
		}
		case 2:
		{
			MIN_LENGTH = 180;
			MAX_LENGTH = 280;
			buffer_value = GameOptions.DISTANCE_CREATE_BOARD + 40.0f;		
			break;
		}
		case 3:
		{
			MIN_LENGTH = 200;
			MAX_LENGTH = 290;
			buffer_value = GameOptions.DISTANCE_CREATE_BOARD + 50.0f;			
			break;
		}
		case 4:
		{
			UpdateSpeed(-0.2f);
			break;
		}
		default:
			break;
		}
	}
	
	public void UpdateSpeed(float delta_value)
	{
		this.speed += delta_value;
		if (mListBoard.isEmpty())
			return;
		Iterator<Board> localIterator = mListBoard.iterator();
		while (localIterator.hasNext())
        {
            final Board board = (Board) localIterator.next();
            board.SetSpeed(speed);
        }
	}
	
	public void AddBoard(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		float part = MathUtils.random(0, 1.0f);
		float x;
		if (part < 0.3)
			x = MathUtils.random(start_x, (max_x - start_x)/3.0f);
//		else if (part < 0.8)
//			x = MathUtils.random(0.5f * max_x, max_x);
		else
			x = MathUtils.random(start_x, max_x);
		float width = MathUtils.random(MIN_LENGTH, MAX_LENGTH);
		if (x + width > end_x)
			width = end_x - x;		
		if (end_x - x - width < MIN_VALID_WIDTH)
			width = end_x - x;
		if (x < MIN_VALID_WIDTH)
			x = start_x;

		Board board = new Board(x, y, width, WIDTH_BOARD, MainGameActivity.mRegionBoard, 
				pVertexBufferObjectManager, pScene, this);
		// определяем, будем ли добавлять монетки
		float prob_coin = MathUtils.random(0, 1.0f);
		if (prob_coin < GameOptions.probability_coin)
		{	// добавляем монетку
			float temp = GameOptions.probability_coin/3.0f;
			if (prob_coin < temp)
				AddCoin(x, x + width, y, 1, pVertexBufferObjectManager);
			else if (prob_coin < 2.0f*temp)
				AddCoin(x, x + width, y, 2, pVertexBufferObjectManager);
			else
				AddCoin(x, x + width, y, 3, pVertexBufferObjectManager);
		}
		else
		{	// иначе попытаемся добавить шарик ускорения или динамит
			float prob_dynamite = MathUtils.random(0, 1.0f);
			if (prob_dynamite < GameOptions.probability_boom)
			{	// динамит
				AddDynamite(x, x + width, y, pVertexBufferObjectManager);
			}			
			else
			{
				float prob_speed_ball = MathUtils.random(0, 1.0f);
				if (GameOptions.IsAcceptAddSpeedBall && prob_speed_ball < GameOptions.probability_ball_speed)
					AddBallSpeed(x, x + width, y, pVertexBufferObjectManager);
			}
			
		}
		board.Start(speed);		
		mListBoard.addLast(board);
		// Увеличиваем пройденный шаром путь на 1.
		pScene.length_way += 1;
	}	
	
	public void Refresh()
	{
		coinManager.RefreshCoins();
		ballSpeedManager.RefreshCoins();
		dynamiteManager.RefreshCoins();
		RefreshBoards();		
	}
	
	private void RefreshBoards()
	{		
		if (mListBoard.isEmpty())
			return;
		Board board = mListBoard.getFirst();
		if (board.GetTailPosition() < position_roof)
		{
			board.Remove();
			mListBoard.removeFirst();
		}
		return;
	}
	
	public void DeleteBoardInList(Board board)
	{
		mListBoard.remove(board);
	}
	
	public void RemoveFirst()
	{
		if (mListBoard.isEmpty())
			return;
		mListBoard.removeFirst();
	}
	
	// возвращает позицию Y последней платформы
	public float GetTailPosition()
	{
		if (mListBoard.isEmpty())
			return 0;
		return mListBoard.getLast().GetTailPosition();
	}	
	
	public boolean IsPossibleAdd()
	{
		if (y - GetTailPosition() > buffer_value)
			return true;
		return false;
	}
	
	public void Release()
	{
		RemoveCoin();
		if (mListBoard.isEmpty())
			return;
		Object[] obj_board = mListBoard.toArray();
		for (int i = obj_board.length - 1; i >= 0; i--)
		{
			Board temp = (Board)obj_board[i];
			temp.Remove();
		}	
		mListBoard.clear();
	}
	
	private void RemoveCoin()
	{
		if (coinManager != null)
			coinManager.Remove();
		if (ballSpeedManager != null)
			ballSpeedManager.Remove();
		if (dynamiteManager != null)
			dynamiteManager.Remove();
	}
	
	public void ChangeCollision(Ball ball)
	{
		if (ball.STATE_BALL == Ball.STATE_BALL_SQUARE)
		{
			Board collide_board = ChangeCollisionBoard(ball);	
			if (collide_board != null)
			{
				ball.AddDestroyBoard(1);
				DeleteBoardInList(collide_board);
				float width = collide_board.getWidth();
				float x = 0;
				float y = collide_board.getY();
				if (ball.getX() < collide_board.getX() + width/4.0f)
					x = collide_board.getX() + width/4.0f;
				else if (ball.getX() < collide_board.getX() + 3.0f*width/4.0f)
					x = collide_board.getX() + width/2.0f;
				else
					x = collide_board.getX() + 3.0f*width/4.0f;
				collide_board.StartDestroyAnimation(x, y);	
			}
		}
		Coin collide_coin = ChangeCollisionCoin(ball);
		if (collide_coin != null)
		{
			collide_coin.StartDestroyAnimation(pScene.TextCountCoin.getX(), this.position_roof);
			coinManager.DeleteCoinInList(collide_coin);			
		}
		BallSpeed collide_ball = ChangeCollisionSpeedBall(ball);
		if (collide_ball != null)
		{	
			float start_y = pScene.TextValue.getY() + collide_ball.getY() - position_roof;
			float start_x = collide_ball.getX();
			ballSpeedManager.DeleteCoinInList(collide_ball);	
			ballSpeedManager.RemoveObject(collide_ball);
			Color color = new Color(0.0f, 1.0f, 1.0f);
			pScene.UpdateValueState(100);
			GameMainFunctions.AddTextMulti("+100", 1.0f, 1.4f, pScene.myHUD, color, 
					MainGameActivity.mStrokeFontGame, 3.0f, start_x, 
					start_y, pScene.TextValue.getY() - 20.0f, MainGameActivity._main.getVertexBufferObjectManager());	
		}
		Dynamite collide_dynamite = ChangeCollisionDynamite(ball);
		if (collide_dynamite != null)
		{
			if (pScene.count_bomb == 3)
				BoomActivated(collide_dynamite, ball);
			else
			{
				pScene.count_bomb++;
				pScene.RefreshTileBoomState();
				collide_dynamite.StartDestroyAnimationWithoutBoom(pScene.spriteBoomTitle.getX(), this.position_roof);
				dynamiteManager.DeleteCoinInList(collide_dynamite);		
			}
		}
	}
	
	private void BoomActivated(Dynamite collide_dynamite, Ball ball)
	{
		Vector2 center = collide_dynamite.GetCenter();
		int count = DestroyIntersectionsBoard(center.x, center.y, GameOptions.DEFAULT_BOOM_RADIUS);
		if (count != 0)
		{
			ball.ClearCountDestroyBoard();
			ball.AddDestroyBoard(count);
			ball.BoomReaction();
		}
		if (GameOptions.IsVibrations)
			MainGameActivity._main.mVibrator.vibrate(1000);
		collide_dynamite.StartDestroyAnimation(center.x, center.y);
		dynamiteManager.DeleteCoinInList(collide_dynamite);			
	}
	
	public void BoomActivated(Ball ball)
	{
		Vector2 center = ball.GetCenter();
		int count = DestroyIntersectionsBoard(center.x, center.y, GameOptions.DEFAULT_BOOM_RADIUS);
		Dynamite.StartAnimationBoom(pScene, center.x, center.y);
		if (count != 0)
		{
			ball.ClearCountDestroyBoard();
			ball.AddDestroyBoard(count);
			ball.BoomReaction();
		}
		if (GameOptions.IsVibrations)
			MainGameActivity._main.mVibrator.vibrate(1000);
	}
	
	private Coin ChangeCollisionCoin(Ball ball)
	{
		return (Coin)coinManager.ChangeCollision(ball);
	}
	
	private BallSpeed ChangeCollisionSpeedBall(Ball ball)
	{
		return (BallSpeed)ballSpeedManager.ChangeCollision(ball);
	}
	
	private Dynamite ChangeCollisionDynamite(Ball ball)
	{
		return (Dynamite)dynamiteManager.ChangeCollision(ball);
	}
	
	private Board ChangeCollisionBoard(Ball ball)
	{
		if (mListBoard.isEmpty())
			return null;	
		Iterator<Board> localIterator = mListBoard.iterator();
		while (localIterator.hasNext())
		{
			Board temp = (Board)localIterator.next();
			if (temp.collidesWith(ball))
				return temp;
		}
		return null;
	}	
	
	private void AddCoin(float start_x, float end_x, float y, int count, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		float width = GameOptions.DEFAULT_WIDTH_COIN;
		float x = MathUtils.random(start_x, end_x);
		for (int i = 0; i < count; i++)
		{
			if (x > end_x - width)
				return;
			Coin coin = new Coin(x, y - width, MainGameActivity.mRegionCoin, pVertexBufferObjectManager, pScene);
			coinManager.AddObject(coin);
			x += width + 2.0f;
		}
	}
	
	private void AddBallSpeed(float start_x, float end_x, float y, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		float width = GameOptions.DEFAULT_WIDTH_SPEED_BALL;
		float x = MathUtils.random(start_x, end_x);
		if (x > end_x - width)
			return;
		BallSpeed ballSpeed = new BallSpeed(x, y - width, MainGameActivity.mRegionSpeedBall, pVertexBufferObjectManager, pScene);
		ballSpeedManager.AddObject(ballSpeed);
	}
	
	private void AddDynamite(float start_x, float end_x, float y, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		float width = GameOptions.DEFAULT_WIDTH_DYNAMITE;
		float x = MathUtils.random(start_x, end_x);
		if (x > end_x - width)
			return;
		Dynamite dynamite = new Dynamite(x, y - width, MainGameActivity.mRegionDynamite, pVertexBufferObjectManager, pScene);
		dynamiteManager.AddObject(dynamite);
	}
	
	private int DestroyIntersectionsBoard(float center_x, float center_y, float radius)
	{
		if (mListBoard.isEmpty())
			return 0;	
		int result = 0;
		Object[] obj_board = mListBoard.toArray();
		for (int i = obj_board.length - 1; i >= 0; i--)
		{
			Board temp = (Board)obj_board[i];
			Vector2 vector = temp.GetCenter();
			if (MathUtils.distance(center_x, center_y, vector.x, vector.y) < radius)
			{
				result++;
				DeleteBoardInList(temp);
				temp.StartDestroyAnimation(vector.x, vector.y);
			}
		}		
		return result;
	}
}

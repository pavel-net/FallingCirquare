package com.byPahych.fallingcirquare;

import java.util.Iterator;
import java.util.LinkedList;


public class GameObjectManager {

	protected LinkedList<GameTileObject> mListObject;
	protected SceneGame scene;
	protected float position_roof;
	protected float position_ground;
	
	public GameObjectManager(SceneGame scene, float position_roof)
	{
		this.scene = scene;
		this.position_roof = position_roof;
		this.position_ground = position_roof + MainGameActivity.CAMERA_HEIGHT + 150.0f;
		mListObject = new LinkedList<GameTileObject>();		
	}
	
	public void UpdateRoof(float delta_y)
	{
		this.position_roof += delta_y;
		this.position_ground += delta_y;
	}
	
	public void RefreshCoins()
	{		
		if (mListObject.isEmpty())
			return;		
		int size = mListObject.size();
		for (int i = size - 1; i >= 0; i--)
		{
			GameTileObject _object = mListObject.get(i);
			float y = _object.GetY();
			if(y < position_roof || y > position_ground)
			{
				mListObject.remove(i);
				_object.Remove();				
			}
		}		
		return;
	}
	
	public GameTileObject ChangeCollision(Ball ball)
	{
		if (mListObject.isEmpty())
			return null;	
		Iterator<GameTileObject> localIterator = mListObject.iterator();
		while (localIterator.hasNext())
		{
			GameTileObject temp = (GameTileObject)localIterator.next();
			if (temp.collidesWith(ball))
				return temp;
		}
		return null;
	}
	
	public void DeleteCoinInList(GameTileObject object)
	{
		mListObject.remove(object);
	}
	
	public void RemoveObject(GameTileObject object)
	{
		object.Remove();
	}
	
	public void Remove()
	{
		if (mListObject.isEmpty())
			return;	
		Iterator<GameTileObject> localIterator = mListObject.iterator();
		while (localIterator.hasNext())
		{
			GameTileObject temp = (GameTileObject)localIterator.next();
			temp.Remove();
		}
		mListObject.clear();
	}
	
	public void AddObject(GameTileObject _object)
	{
		this.mListObject.addLast(_object);
	}
	
}

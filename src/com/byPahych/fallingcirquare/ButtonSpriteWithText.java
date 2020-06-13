package com.byPahych.fallingcirquare;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

public class ButtonSpriteWithText extends TiledSprite {
	private static final float HALF = 2f;
	public Text mText = null;
	public boolean IsActive = false;
	
	public ButtonSpriteWithText(final float pX, final float pY, final ITiledTextureRegion pNormalTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager, final CharSequence pText,
			final Font pFont, float scale){
		super(pX, pY, pNormalTextureRegion, pVertexBufferObjectManager);
		this.setScale(scale);
		this.mText = new Text(pX, pY, pFont, pText, new TextOptions(HorizontalAlign.CENTER), pVertexBufferObjectManager);

		final float hightTxt = mText.getHeight();
		final float hightBtn = this.getHeight();
		
		final float widthTxt = mText.getWidth();
		final float widtBtn = this.getWidth();
		
		final float positionX = widtBtn / HALF - widthTxt / HALF;
		final float positionY = hightBtn / HALF - hightTxt/HALF;
		
		mText.setX(positionX);
		mText.setY(positionY);
		attachChild(mText);
		
	}
	
	public ButtonSpriteWithText(final float pX, final float pY, final ITiledTextureRegion pNormalTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager, final CharSequence pText,
			final Font pFont, float Width, float Height){
		super(pX, pY, pNormalTextureRegion, pVertexBufferObjectManager);
		this.setWidth(Width);
		this.setHeight(Height);
		this.mText = new Text(pX, pY, pFont, pText, new TextOptions(HorizontalAlign.CENTER), pVertexBufferObjectManager);

		final float hightTxt = mText.getHeight();
		final float hightBtn = this.getHeight();
		
		final float widthTxt = mText.getWidth();
		final float widtBtn = this.getWidth();
		
		final float positionX = widtBtn / HALF - widthTxt / HALF;
		final float positionY = hightBtn / HALF - hightTxt/HALF;
		
		mText.setX(positionX);
		mText.setY(positionY);
		attachChild(mText);
		
	}
	
	public void SetText(final CharSequence pText)
	{
		this.mText.setText(pText);
	}
	
	public void setBlendFunction(final int pBlendFunctionSource, final int pBlendFunctionDestination) {
		mText.setBlendFunction(pBlendFunctionSource, pBlendFunctionDestination);
	}
	public void setAlpha(final float pAlpha) {
		super.setAlpha(pAlpha);
		mText.setAlpha(pAlpha);
	}
	public void setColor(final float pRed, final float pGreen, final float pBlue){
		 mText.setColor(pRed, pGreen, pBlue);
	}

		
}

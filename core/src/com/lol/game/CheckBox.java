package com.lol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CheckBox {
    Rectangle rect;
    String text;
    int size = 48;
    boolean checked = false;
    CheckBox(float x, float y, String text){
        rect = new Rectangle(x,y,size,size);
        this.text = text;
    }
    public boolean isClicked(Vector2 mouse){
        if(rect.contains(mouse)){
            checked = !checked;
            return true;
        }else{
            return false;
        }
    }
    public void draw(SpriteBatch batch, TextureRegion tex, BitmapFont font, GlyphLayout layout){
        batch.setColor(Color.DARK_GRAY);
        batch.draw(tex,rect.x,rect.y,rect.width,rect.height);
        if(!checked){
            batch.setColor(Color.WHITE);
        }else{
            batch.setColor(Color.GOLD);
        }
        batch.draw(tex,rect.x,rect.y,rect.width,rect.height);
        layout.setText(font,text);
        font.draw(batch,layout,rect.x + rect.width + 20,rect.y + rect.height/2 + layout.height/2);
    }
}

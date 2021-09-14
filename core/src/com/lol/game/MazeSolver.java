package com.lol.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lol.game.MazeCell;

import java.awt.*;
import java.util.ArrayList;

public class MazeSolver extends ApplicationAdapter implements InputProcessor {
	/** Input Objects **/
	Vector2 mouse, screenMouse;
	/** Primitive Variables **/
	SpriteBatch batch;
	OrthographicCamera camera;
	Viewport viewport;
	GlyphLayout layout = new GlyphLayout();
	boolean highspeed = false;
	/** Initialization Methods **/
	final float FONT_SMOOTHING = 1/8f;
	final int SCREEN_W = 1920;
	final int SCREEN_H = 1080;
	float fontScale = 3;
	float resolution;
	int mazeWidth = 50;
	MazeGenerator mg;
	String modeText = "";
	/** Shaders **/
	DistanceFieldShader fontShader;
	/** Textures **/
	Texture fontTex;
	TextureAtlas uiAtlas, entityAtlas;
	ArrayList<TextureRegion> uiTextures = new ArrayList<>();
	ArrayList<TextureRegion> playerTextures = new ArrayList<>();
	BitmapFont font;
	Runner runner;
	CheckBox[] CBoxArray = new CheckBox[10];
	int count = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false,SCREEN_W,SCREEN_H);
		viewport = new FitViewport(SCREEN_W,SCREEN_H,camera);
		viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(this);
		Gdx.graphics.setUndecorated(true);
		//Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		Gdx.graphics.setWindowedMode(800,450);
		createVectors();
		createCheckBoxes();
		loadTextures();
		fontShader = new DistanceFieldShader();
		mg = new MazeGenerator(mazeWidth,mazeWidth);
		runner = new Runner(mg);
	}
	public void loadTextures(){
		uiAtlas = new TextureAtlas(Gdx.files.internal("textures/UITextures.atlas"));
		uiTextures.add(uiAtlas.findRegion("plainwhite"));
		uiTextures.add(uiAtlas.findRegion("plainred"));
		fontTex = new Texture(Gdx.files.internal("textures/newfont.png"),true);
		fontTex.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
		font = new BitmapFont(Gdx.files.internal("newfont.fnt"), new TextureRegion(fontTex), false);
		font.setUseIntegerPositions(false);
		entityAtlas = new TextureAtlas(Gdx.files.internal("textures/EntityTextures.atlas"));
		playerTextures.add(entityAtlas.findRegion("player_static"));
	}
	public void createVectors(){
		mouse = new Vector2(0,0);
		screenMouse = new Vector2(0,0);
	}
	public void createCheckBoxes(){
		CBoxArray[0] = new CheckBox(1200,1000,"Autorun");
		CBoxArray[1] = new CheckBox(1200,900,"Show Deletions");
		CBoxArray[2] = new CheckBox(1200,800,"Highlight Path");
		CBoxArray[3] = new CheckBox(1200,700,"Highlight Deletions");
		CBoxArray[4] = new CheckBox(1200,600,"Highspeed");
		CBoxArray[5] = new CheckBox(1200,500,"Loop");
		CBoxArray[6] = new CheckBox(1200,400,"New Maze");
	}
	/** Render methods **/
	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
				(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		setMouse();
		updateCheckVariables();
		updateRunner();
		drawScene();
	}
	public void drawScene(){
		batch.begin();
		drawMaze();
		batch.setShader(fontShader);
		fontShader.setSmoothing(FONT_SMOOTHING);
		drawUI();
		batch.setShader(null);
		batch.end();
	}
	public void drawMaze(){
		mg.drawMaze(batch, uiTextures.get(0));
		runner.draw(batch, uiTextures.get(0));
	}
	public void drawUI(){
		for(CheckBox cb: CBoxArray){
			if(cb == null) continue;
			cb.draw(batch,uiTextures.get(0),font,layout);
		}
	}

	public void setMouse(){
		mouse.set(Gdx.input.getX(),Gdx.input.getY());
		viewport.unproject(mouse);
		screenMouse.set(mouse.x-(camera.position.x-SCREEN_W/2f),mouse.y-(camera.position.y-SCREEN_H/2f));
	}
	public void updateCheckVariables(){
		if (!runner.finished) {
			runner.autorun = CBoxArray[0].checked;
		}else{
			CBoxArray[0].checked = false;
		}
		mg.showDeletions = CBoxArray[1].checked;
		mg.highlightPath = CBoxArray[2].checked;
		mg.highlightDeletions = CBoxArray[3].checked;
		highspeed = CBoxArray[4].checked;
		if(!runner.finished){
			runner.loop = CBoxArray[5].checked;
		}else{
			if(CBoxArray[5].checked)runner.reset();
		}
		if(CBoxArray[6].checked){
			CBoxArray[6].checked = false;
			mg.generateNewMaze(mg.maze.length,mg.maze.length);
		}
	}
	public void updateRunner(){
		if(runner.autorun){
			if(highspeed){
				if(!runner.finished){
					runner.update(0);
				}
			}else{
				if(count<2){
					count++;
				}else{
					runner.update(0);
					count = 0;
				}
			}
		}
	}

	/** Update methods **/
	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}

	/** Dispose **/
	@Override
	public void dispose() {
		fontShader.dispose();
		fontTex.dispose();
		uiAtlas.dispose();
		font.dispose();
		batch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.F11){
			if(Gdx.graphics.isFullscreen()){
				Gdx.graphics.setWindowedMode(800,450);
			}else{
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(character=='p'){
			mg.generateNewMaze(mg.maze.length,mg.maze.length);
		}
		if(character==27)Gdx.app.exit();
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for(CheckBox c: CBoxArray){
			if(c == null) continue;
			c.isClicked(mouse);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}

package com.lol.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
	MazeUtils mazeUtils = new MazeUtils();
	Runner runner = new Runner();
	/** Initialization Methods **/
	final float FONT_SMOOTHING = 1/8f;
	float fontScale = 3;
	Maze maze = new Maze();
	int mode = 0;
	String modeText = "";
	/** Shaders **/
	DistanceFieldShader fontShader;
	/** Textures **/
	Texture fontTex;
	TextureAtlas uiAtlas, entityAtlas;
	ArrayList<TextureRegion> uiTextures = new ArrayList<>();
	ArrayList<TextureRegion> playerTextures = new ArrayList<>();
	BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false,1920,1080);
		viewport = new FitViewport(1920,1080,camera);
		viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(this);
		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		createVectors();
		loadTextures();
		maze.maze = mazeUtils.createMaze(21,21);
		fontShader = new DistanceFieldShader();
		runner.setMaze(maze);
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

	/** Render methods **/
	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		setMouse();
		if(runner.autorun){
			updateRunner();
		}
		batch.begin();
		maze.drawMaze(batch,uiTextures.get(0));
		batch.setColor(Color.BLUE);
		batch.draw(uiTextures.get(0),200+runner.x*32,200+runner.y*32,32,32);
		if(runner.showNext){
			batch.setColor(Color.YELLOW);
			batch.draw(uiTextures.get(0),200+runner.nextX*32,200+runner.nextY*32,32,32);
		}
		if(runner.showRight){
			batch.setColor(Color.ORANGE);
			batch.draw(uiTextures.get(0),200+runner.rightX*32,200+runner.rightY*32,32,32);
		}
		drawStrings();
		batch.end();
	}
	public void drawStrings(){
		if(mode==0){
			modeText="Right Hand Rule";
		}else if(mode==1){
			modeText="Depth First Search";
		}else{
			modeText="Random";
		}
		batch.setShader(fontShader);
		fontShader.setSmoothing(1/8f);
		font.getData().setScale(0.75f);
		layout.setText(font,
				"Controls\n" +
				"p: place\n" +
				"s: step\n" +
				"r: autorun\n" +
				"f: fill\n" +
				"d: show deletions: " + maze.showDeletions + "\n" +
				"n: show next: " + runner.showNext + "\n"+
				"w: show right: " + runner.showRight +"\n"+
				"1/2: mode: " + modeText + "\n" +
				"3: loop: " + runner.loopRandom + "\n" +
				"esc: exit");
		font.draw(batch,layout,1000,850);
		batch.setShader(null);

	}
	public void updateRunner(){
		runner.move(mode, maze.getNeighbors(runner.x,runner.y));
	}

	public void setMouse(){
		mouse.set(Gdx.input.getX(),Gdx.input.getY());
		viewport.unproject(mouse);
		screenMouse.set(mouse.x-(camera.position.x-960),mouse.y-(camera.position.y-540));
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
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if(character == 'p'){
			runner.setPosition(mouse);
		}else if(character == 's'){
			updateRunner();
		}else if(character == 'r'){
			runner.autorun = !runner.autorun;
		}else if(character == 'n'){
			runner.showNext = !runner.showNext;
		}else if(character == 'w'){
			runner.showRight = !runner.showRight;
		}else if(character == '3'){
			runner.loopRandom = !runner.loopRandom;
		}else if(character == 'f'){
			maze.maze[runner.x][runner.y]=-1;
			maze.fillFromEnds();
			runner.loopRandom = false;
		}else if(character == 'd'){
			maze.showDeletions = !maze.showDeletions;
		}else if(character=='1'){
			mode=Math.abs(mode-1);
		}else if(character == '2'){
			if(mode!=2){
				mode=2;
			}else mode=0;
		}else if(character==27)Gdx.app.exit();
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(mouse.x >200 && mouse.x < 200+32*21
		&& mouse.y > 200 && mouse.y < 200+32*21){
			runner.setPosition(mouse);
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

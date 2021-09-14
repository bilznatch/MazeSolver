package com.lol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

public class Runner {
    int x, y;
    final int N = 0, S = 1, E = 2, W = 3;
    MazeGenerator mg;
    MazeCell[] neighbors;
    boolean backtracking = false;
    boolean autorun = false;
    boolean loop = false, finished = false;
    Runner(MazeGenerator mg){
        x = 0;
        y = 0;
        this.mg = mg;
    }
    public void update(int mode){
        neighbors = mg.getNeighbors(x,y);
        if(mode==0){
            doDFS(neighbors);
        }else if(mode==1){
            doRHR();
        }else{

        }
        checkForGoal();
    }
    public void checkForGoal(){
        if(mg.maze[x][y].isGoal && loop){
            moveToRandomSpace();
            mg.setRandomGoal();
        }else if(mg.maze[x][y].isGoal){
            autorun = false;
            finished = true;
        }
    }
    public void reset(){
        finished = false;
        moveToRandomSpace();
        mg.setRandomGoal();
    }
    public void doDFS(MazeCell[] neighbors){
        if(!backtracking){
            mg.maze[x][y].visited = true;
            if(hasOpenNeighbor()){
                goToFirstOpenNeighbor();
            }else{
                backtracking = true;
            }
        }else{
            if(hasOpenNeighbor()){
                backtracking = false;
            }else{
                mg.maze[x][y].deleted = true;
                if(hasVisitedNeighbor()){
                    goToFirstVisitedNeighbor();
                }else{
                    backtracking=false;
                }
            }
        }
    }
    public boolean hasOpenNeighbor(){
        for(int i = 0; i < neighbors.length;i++){
            if(neighbors[i]!=null)if(!neighbors[i].visited&&!mg.maze[x][y].walls[i])return true;
        }
        return false;
    }
    public boolean hasVisitedNeighbor(){
        for(int i = 0; i < neighbors.length;i++){
            if(neighbors[i]!=null)if(!neighbors[i].deleted&&!mg.maze[x][y].walls[i])return true;
        }
        return false;
    }
    public void goToFirstOpenNeighbor(){
        for(int i = 0; i < neighbors.length;i++){
            if(neighbors[i]!=null)if(!neighbors[i].visited&&!mg.maze[x][y].walls[i]){
                moveInDir(i);
                return;
            }
        }
    }
    public void goToFirstVisitedNeighbor(){
        for(int i = 0; i < neighbors.length;i++){
            if(neighbors[i]!=null)if(!neighbors[i].deleted&&!mg.maze[x][y].walls[i]){
                moveInDir(i);
                return;
            }
        }
    }
    public void moveInDir(int direction){
        if(direction == N){
            y++;
        }else if(direction == S){
            y--;
        }else if(direction == E){
            x++;
        }else if(direction == W){
            x--;
        }
    }
    public void moveToRandomSpace(){
        mg.resetMaze();
        x = MathUtils.random(mg.maze.length-1);
        y = MathUtils.random(mg.maze[0].length-1);
    }
    public void doRHR(){

    }
    public void draw(SpriteBatch batch, TextureRegion tex){
        float resolution = 35f * (30f/mg.maze.length);
        batch.setColor(Color.CYAN);
        batch.draw(tex,10+(x*resolution)+(resolution*0.1f),10+(y*resolution)+(resolution*0.1f),resolution*0.80f,resolution*0.80f);
    }
}

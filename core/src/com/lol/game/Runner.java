package com.lol.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Runner {
    int x = 1, y = 1;
    int nextX = 0, nextY = 0, rightX = 0, rightY = 0;
    int facing = 0; //0 up, 1 right, 2 down, 3 left
    int forward = 0, right = 1, backward = 2, left = 3;
    int cycle = 0;
    int dfsMode = 0;
    Maze maze;
    boolean finished = false, autorun = false, showNext = false, showRight = false, loopRandom = true;
    boolean exploring = false, backtracking = false;
    public void move(int mode, int[] neighbors){
        if(!finished){
            if(mode==0){
                doRHR(neighbors);
            }else if(mode==1){
                doDFS(neighbors);
            }else{
                doRandomMove(neighbors);
            }
        }
        checkforGoal(neighbors);
    }
    public void doRHR(int[] neighbors){
        getOrientation();
        doMove(neighbors);
        getOrientation();
        checkForMove(maze.getNeighbors(x,y));
        setRight();
    }
    public void checkForMove(int[] neighbors){
        if(neighbors[right] <= 0){
            setNext(right);
        }else if(neighbors[forward] <= 0){
            setNext(forward);
        }else if(neighbors[left] <= 0){
            setNext(left);
        }else{
            setNext(backward);
        }
    }
    public void doMove(int[] neighbors){
        if(neighbors[right] <= 0 || neighbors[right] == 4){
            facing = right;
            moveforward();
        }else if(neighbors[forward] <= 0 || neighbors[forward] == 4){
            moveforward();
        }else if(neighbors[left] <= 0 || neighbors[left] == 4){
            facing = left;
            moveforward();
        }else{
            facing = backward;
            moveforward();
        }
    }

    public void checkforGoal(int[] neighbors){
        if(neighbors[0] == 2){
            y++;
        }else if(neighbors[1] == 2){
            x++;
        }else if(neighbors[2] == 2){
            y--;
        }else if(neighbors[3] == 2){
            x--;
        }else{
            return;
        }
        if(loopRandom){
            maze.resetMaze();
            int rx = MathUtils.random(1,19), ry = MathUtils.random(1,19);
            while(maze.maze[rx][ry] != 0 && maze.maze[rx][ry] != 4){
                rx = MathUtils.random(1,19);
                ry = MathUtils.random(1,19);
            }
            x = rx;
            y = ry;
        }else{
            finished = true;
            autorun = false;
        }
    }

    public void getOrientation(){
        if(facing == 0){
            forward = 0;
            right = 1;
            backward = 2;
            left = 3;
        }else if(facing == 1){
            forward = 1;
            right = 2;
            backward = 3;
            left = 0;
        }else if(facing == 2){
            forward = 2;
            right = 3;
            backward = 0;
            left = 1;
        }else if(facing == 3){
            forward = 3;
            right = 0;
            backward = 1;
            left = 2;
        }
    }
    public void moveforward(){
        if(facing == 0){
            y++;
        }else if(facing == 1){
            x++;
        }else if(facing == 2){
            y--;
        }else if(facing == 3){
            x--;
        }
    }
    public void setRight(){
        if(facing == 0){
            rightX = x+1;
            rightY = y;
        }else if(facing == 1){
            rightX = x;
            rightY = y-1;
        }else if(facing == 2){
            rightX = x-1;
            rightY = y;
        }else if(facing == 3){
            rightX = x;
            rightY = y+1;
        }
    }
    public void setNext(int dir){
        if(dir == 0){
            nextY = y+1;
        }else if(dir == 1){
            nextX = x+1;
        }else if(dir == 2){
            nextY = y-1;
        }else if(dir == 3){
            nextX = x-1;
        }
    }
    public void setMaze(Maze maze){
     this.maze = maze;
    }
    public void doDFS(int[] neighbors){
        int[] count;
        count = countNeighbors(neighbors);
        if(exploring && !backtracking){
            if(count[0]==1){
                maze.maze[x][y]=5;
                if(neighbors[0]==0){
                    y++;
                }else if(neighbors[1]==0){
                    x++;
                }else if(neighbors[2]==0){
                    y--;
                }else if(neighbors[3]==0) {
                    x--;
                }
            }else if(count[0]>1){
                maze.maze[x][y]=4;
                if(neighbors[0]==0){
                    y++;
                }else if(neighbors[1]==0){
                    x++;
                }else if(neighbors[2]==0){
                    y--;
                }else if(neighbors[3]==0) {
                    x--;
                }
            }else{
                maze.maze[x][y]=3;
                if(neighbors[0]==5){
                    y++;
                }else if(neighbors[1]==5){
                    x++;
                }else if(neighbors[2]==5){
                    y--;
                }else if(neighbors[3]==5) {
                    x--;
                }
                backtracking=true;
            }
        }else if(backtracking){
            maze.maze[x][y]=3;
            if(neighbors[0]==5){
                y++;
            }else if(neighbors[1]==5){
                x++;
            }else if(neighbors[2]==5){
                y--;
            }else if(neighbors[3]==5){
                x--;
            }else{
                if(neighbors[0]==4){
                    y++;
                }else if(neighbors[1]==4){
                    x++;
                }else if(neighbors[2]==4){
                    y--;
                }else if(neighbors[3]==4){
                    x--;
                }
                backtracking=false;
            }
        }else{
            if(count[0]==1){
                maze.maze[x][y] = 3;
                if(neighbors[0]==0){
                    y++;
                }else if(neighbors[1]==0){
                    x++;
                }else if(neighbors[2]==0){
                    y--;
                }else if(neighbors[3]==0){
                    x--;
                }
            }
            if(count[0]>1){
                maze.maze[x][y]=4;
                if(neighbors[0]==0){
                    y++;
                }else if(neighbors[1]==0){
                    x++;
                }else if(neighbors[2]==0){
                    y--;
                }else if(neighbors[3]==0){
                    x--;
                }
                exploring = true;
            }
        }
    }
    public void doRandomMove(int[] neighbors){
        int[] count = countNeighbors(neighbors);
        if(count[1]+count[3]>=3)maze.maze[x][y]=3;
        int r = MathUtils.random(0,3);
        if(neighbors[r]==0){
            if(r==0){
                y++;
            }else if(r==1){
                x++;
            }else if(r==2){
                y--;
            }else{
                x--;
            }
        }else{
            doRandomMove(neighbors);
        }
    }
    public int[] countNeighbors(int[] neighbors){
        int[] count = new int[]{0,0,0,0,0,0};
        count[neighbors[0]]++;
        count[neighbors[1]]++;
        count[neighbors[2]]++;
        count[neighbors[3]]++;
        return count;
    }

    public void setPosition(Vector2 mouse){
        x = (int)(mouse.x-200)/32;
        y = (int)(mouse.y-200)/32;
        nextX = x;
        nextY = y;
        finished = false;
        maze.setRunner(x,y);
    }
}

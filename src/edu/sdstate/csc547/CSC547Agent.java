package edu.sdstate.csc547;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.BasicAIAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Sprite;
import ch.idsia.mario.environments.Environment;

/**
 * Created by harshith on 7/23/15.
 */
public class CSC547Agent extends BasicAIAgent implements Agent {

    private enum JumpReason {
        NONE, WALL, ENEMY, GAP;
    }
    private int jumpCount = 0;
    private int jumpHeight = -1;
    private float previousY = 0f;
    private float previousX = 0f;

    private JumpReason jumpReason = JumpReason.NONE;


    public CSC547Agent() {
        super("CSC547Agent");
        reset();
    }

    private int calculateWallHeight(final byte[][] levelScene) {
        int height = 0;
        for (int i = 11; i > 0 && levelScene[i][12] != Sprite.KIND_NONE; i--) {
            height++;
        }
        return height;
    }

    private boolean detectGap(final byte[][] levelScene) {
        for (int y = 12; y < levelScene.length; y++) {
            if (levelScene[y][12] != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean detectEnemiesFront(final byte[][] enemiesScene) {

        for (int i = 9; i <= 12; i++) {
            for (int j = 10; j<= 15; j++) {
                if (enemiesScene[i][j] != Sprite.KIND_NONE && enemiesScene[i][j] != Sprite.KIND_FIREBALL) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectEnemiesAbove(final byte[][] enemiesScene) {

        for (int i = 12; i <= 18; i++) {
            for (int j = 11; j<= 13; j++) {
                if (enemiesScene[i][j] != Sprite.KIND_NONE && enemiesScene[i][j] != Sprite.KIND_FIREBALL) {
                    return true;
                }
            }
        }
        return false;
    }


    private void jump(int size, JumpReason reason) {
        if (reason.equals(JumpReason.WALL)) {
            jumpHeight = Math.max(4, size);
        } else {
            jumpHeight = size;
        }
        jumpCount = 0;
        jumpReason = reason;
    }
/*comment*/
    @Override
    public void reset() {
        action = new boolean[Environment.numberOfButtons];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
    }

    @Override
    public boolean[] getAction(Environment observation) {
        final byte[][] levelScene = observation.getLevelSceneObservation();
        final byte[][] enemiesScene = observation.getEnemiesObservation();
        boolean enemyinFront = detectEnemiesFront(enemiesScene);
        boolean enemyAbove = detectEnemiesAbove(enemiesScene);
        boolean gap = detectGap(levelScene);
        float speed = observation.getMarioFloatPos()[0] - previousX;
//        System.out.println("Enemies: " +enemyDetected);
        if ((observation.isMarioOnGround() || observation.mayMarioJump()) && !jumpReason.equals(JumpReason.NONE)) {
            jump(-1, JumpReason.NONE);
        }
        else if (observation.mayMarioJump()) {
            int wallHeight = calculateWallHeight(levelScene);
            if (gap && speed > 0) {
                jump(speed < 6 ?(int)(9-speed):1, JumpReason.GAP);
            } else if (enemyinFront) {
                jump(7, JumpReason.ENEMY);
            } else if (wallHeight > 0) {
                jump(wallHeight, JumpReason.WALL);
            }
        } else {
            jumpCount++;
        }
        boolean falling = previousY < observation.getMarioFloatPos()[1] && jumpReason.equals(JumpReason.NONE);
        action[Mario.KEY_LEFT] = falling && (gap || (enemyAbove && enemyinFront));
        action[Mario.KEY_JUMP] = jumpCount < jumpHeight && !jumpReason.equals(JumpReason.NONE);
        action[Mario.KEY_SPEED] = !(observation.getMarioMode() == 2 && enemyinFront) && !(jumpReason.equals(JumpReason.ENEMY) && action[Mario.KEY_SPEED] && observation.getMarioMode() == 2);
        action[Mario.KEY_RIGHT] = !falling && !(enemyAbove && jumpReason.equals(JumpReason.WALL));
        previousX = observation.getMarioFloatPos()[0];
        previousY = observation.getMarioFloatPos()[1];
        return action;
    }

    @Override
    public String getName() {
        return "CSC547Agent";
    }

    @Override
    public void setName(String name) {

    }
}

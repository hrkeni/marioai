package edu.sdstate.csc547;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.BasicAIAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Sprite;
import ch.idsia.mario.environments.Environment;

/**
 * Created by harshith on 7/23/15.
 *
 * Controller agent for the class project
 */
public class CSC547Agent extends BasicAIAgent implements Agent {

    private enum JumpReason {
        NONE, WALL, ENEMY, GAP;
    }

    private int jumpCount = 0;
    private int jumpHeight = -1;
    private float previousY = 0f;
    private float previousX = 0f;
    private int stationaryTime = 0;

    private JumpReason jumpReason = JumpReason.NONE;

    /**
     * Constructor
     */
    public CSC547Agent() {
        super("CSC547Agent");
        reset();
    }

    /**
     * Calculates the wall height
     * @param levelScene
     * @return
     */
    private int calculateWallHeight(final byte[][] levelScene) {
        int height = 0;
        for (int i = 11; i > 0 && levelScene[i][12] != Sprite.KIND_NONE; i--) {
            height++;
        }
        return height;
    }

    /**
     * Detects gaps in front
     * @param levelScene
     * @return
     */
    private boolean detectGap(final byte[][] levelScene) {
        for (int y = 12; y < levelScene.length; y++) {
            if (levelScene[y][12] != 0 && levelScene[y][13] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Detects enemies in front
     * @param enemiesScene
     * @return
     */
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

    /**
     * Detects enemies above
     * @param enemiesScene
     * @return
     */
    private boolean detectEnemiesAbove(final byte[][] enemiesScene) {

        for (int i = 5; i <= 11; i++) {
            for (int j = 10; j<= 13; j++) {
                if (enemiesScene[i][j] != Sprite.KIND_NONE && enemiesScene[i][j] != Sprite.KIND_FIREBALL) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Jumps height with reason
     * @param height
     * @param reason
     */
    private void jump(int height, JumpReason reason) {
//        System.out.println("Jumping: " +height + ", " + reason.toString());
        if (reason.equals(JumpReason.WALL)) {
            jumpHeight = Math.max(4, height);
        } else {
            jumpHeight = height;
        }
        jumpCount = 0;
        jumpReason = reason;
    }

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

        //calculate speed
        float speed = observation.getMarioFloatPos()[0] - previousX;
        if (speed <= 0) {
            stationaryTime++;
        } else {
            stationaryTime = 0;
        }

        if ((observation.isMarioOnGround() || observation.mayMarioJump()) && !jumpReason.equals(JumpReason.NONE)) {
            // no need to jump
            jump(-1, JumpReason.NONE);
        }
        else if (observation.mayMarioJump()) {
            int wallHeight = calculateWallHeight(levelScene);
            if (stationaryTime > 30){
                jump(9, JumpReason.WALL);
            }
            else if (gap && speed > 0) {
                // calculate gap width and jump
                jump(speed < 6 ?(int)(9-speed):2, JumpReason.GAP);
            } else if (enemyinFront) {
                // enemy detected, jump
                jump(7, JumpReason.ENEMY);
            } else if (wallHeight > 0) {
                // calculate wall height and jump
                jump(wallHeight, JumpReason.WALL);
            }
        } else {
            jumpCount++;
        }
        // see if falling
        boolean falling = previousY < observation.getMarioFloatPos()[1] && jumpReason.equals(JumpReason.NONE);

        // decide actions
        action[Mario.KEY_LEFT] = falling && (gap || (enemyAbove && enemyinFront)); // need to slow down/go left
        action[Mario.KEY_JUMP] = jumpCount < jumpHeight && !jumpReason.equals(JumpReason.NONE); // need to jump
        action[Mario.KEY_SPEED] = !(observation.getMarioMode() != 2
                                    && enemyinFront) && !(enemyinFront
                                    && action[Mario.KEY_SPEED]); // need to shoot/run
        action[Mario.KEY_RIGHT] = !falling && !(enemyAbove && jumpReason.equals(JumpReason.WALL))
                                && !(gap && !(jumpReason.equals(JumpReason.GAP)
                                || jumpReason.equals(JumpReason.NONE))); // need to proceed
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

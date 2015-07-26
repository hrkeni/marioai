package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Sprite;
import ch.idsia.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: May 9, 2009
 * Time: 9:46:59 AM
 * Package: ch.idsia.ai.agents
 */
public class ScaredAgent extends BasicAIAgent implements Agent {
    public ScaredAgent() {
        super("ScaredAgent");
    }

    int trueJumpCounter = 0;
    int trueSpeedCounter = 0;

    private boolean dangerExists(byte[][] levelScene, byte[][] enemiesScene) {
        if (levelScene[11][12] != Sprite.KIND_NONE)
            return true;
        if (levelScene[12][12] == Sprite.KIND_NONE)
            return true;
        for (int i = 12; i < 15; i++) {
            if (enemiesScene[11][i] != Sprite.KIND_NONE) {
                System.out.println("Enemies scehe: 11," + i + ": " + enemiesScene[11][i]);
                action[Mario.KEY_SPEED] = false;
                return true;
            }

        }

        return false;
    }

    public void reset() {
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
    }

    public boolean[] getAction(Environment observation) {
        byte[][] levelScene = observation.getLevelSceneObservation(/*1*/);
        byte[][] enemiesScene = observation.getEnemiesObservation();
        action[Mario.KEY_SPEED] = true;
        if (dangerExists(levelScene, enemiesScene) )
        {
            if (observation.mayMarioJump()) {
                action[Mario.KEY_JUMP] = true;
                action[Mario.KEY_SPEED] = false;
            } else if (!observation.isMarioOnGround() && action[Mario.KEY_JUMP]) {
                action[Mario.KEY_JUMP] = true;
            }
            ++trueJumpCounter;
        }
        else
        {
            action[Mario.KEY_JUMP] = false;
//            action[Mario.KEY_SPEED] = false;
            trueJumpCounter = 0;
        }

        if (trueJumpCounter > 46)
        {
            trueJumpCounter = 0;
            action[Mario.KEY_JUMP] = false;
//            action[Mario.KEY_SPEED] = true;
        }

        return action;
    }
}

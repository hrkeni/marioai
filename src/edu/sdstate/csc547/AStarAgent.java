package edu.sdstate.csc547;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.BasicAIAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

/**
 * Created by harshith on 7/23/15.
 */
public class AStarAgent extends BasicAIAgent implements Agent {

    public AStarAgent() {
        super("AStarAgent");
        reset();
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
        action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] =  observation.mayMarioJump() || !observation.isMarioOnGround();
        return action;
    }

    @Override
    public AGENT_TYPE getType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }
}

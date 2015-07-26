package ch.idsia.scenarios;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import edu.sdstate.csc547.CSC547Agent;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args) {
        Agent controller = new CSC547Agent();
        if (args.length > 0) {
            controller = AgentsPool.load (args[0]);
            AgentsPool.addAgent(controller);
        }
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        int seed = (int) (Math.random () * Integer.MAX_VALUE);
        for (int i = 0; i < 10; i++) {
            Task task = new ProgressTask(options);
            options.setMaxFPS(false);
            options.setVisualization(true);
            options.setNumberOfTrials(10);
            options.setMatlabFileName("");
            options.setLevelRandSeed(seed+i);
            options.setLevelDifficulty(2);
            options.setLevelType(i % 3);
            task.setOptions(options);

            System.out.println("Score: " + task.evaluate(controller)[0]);
        }
    }
}

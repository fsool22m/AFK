package afk.game;

import afk.frontend.swing.postgame.RobotScoreList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author daniel
 */
public class GameResult
{

    private UUID winner;
    private Map<UUID, Integer> scores;

    public GameResult(UUID winner, Map<UUID, Integer> scores)
    {
        this.winner = winner;
        this.scores = scores;
    }

    public GameResult(final GameResult other)
    {
        this.winner = other.winner;
        this.scores = other.scores;
    }

    public UUID getWinner()
    {
        return winner;
    }

    public int getScore(UUID id)
    {
        return scores.get(id);
    }

    /**
     * Gets the list of participants, in no particular order.
     * @return the list of UUID's of the participants.
     */
    public UUID[] getParticipants()
    {
        return scores.keySet().toArray(new UUID[0]);
    }

    /**
     * Gets the participants, in order from first to last. i.e. in most "normal"
     * cases getTop()[0] will return the same UUID as getWinner().
     * @return the participants, in order from first to last.
     */
    public UUID[] getTop()
    {
        UUID[] parts = getParticipants();
        
        Arrays.sort(parts, new Comparator<UUID>()
        {
            @Override
            public int compare(UUID o1, UUID o2)
            {
                return Integer.compare(scores.get(o2), scores.get(o1));
            }
        });
        
        return parts;
    }
}

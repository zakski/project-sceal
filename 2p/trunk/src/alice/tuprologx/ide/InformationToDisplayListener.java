package alice.tuprologx.ide;
import java.util.*;

/**
 * Listener for information to display in the console events
 *
 * 
 */

public interface InformationToDisplayListener
    extends EventListener
{
    void onInformation(InformationToDisplayEvent e);
}
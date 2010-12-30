/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.compiler.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.DeepCopy;
import net.domesdaybook.automata.nfa.NfaState;

/**
 *
 * @author matt
 */
public class StateWrapper implements DeepCopy {

        NfaState initialState;
        List<NfaState> finalStates;

        public void setIsFinal(final NfaState state, final boolean isFinal) {
            state.setIsFinal(isFinal);
            if (isFinal) {
                if (!finalStates.contains(state)) {
                    finalStates.add(state);
                }
            } else {
                finalStates.remove(state);
            }
        }

        
    public final StateWrapper deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new HashMap<DeepCopy, DeepCopy>();
        return deepCopy(oldToNewObjects);
    }    

    
    @Override
    public final StateWrapper deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
        StateWrapper copy = new StateWrapper();
        oldToNewObjects.put(this, copy);
        copy.initialState = initialState.deepCopy(oldToNewObjects);
        copy.finalStates = new ArrayList<NfaState>();
        for (NfaState finalState : finalStates) {
            final NfaState finalStateCopy = finalState.deepCopy(oldToNewObjects);
            copy.finalStates.add(finalStateCopy);
        }
        return copy;
    }
        
}
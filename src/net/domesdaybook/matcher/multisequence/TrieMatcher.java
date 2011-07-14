/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.automata.AssociatedState;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.wrapper.Trie;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public final class TrieMatcher implements MultiSequenceMatcher {

    private final Trie trie;

    public TrieMatcher(final Trie trie) {
        if (trie == null) {
            throw new IllegalArgumentException("Null Trie passed in to TrieMatcher.");
        }
        this.trie = trie;
    }


    /**
     * @inheritDoc
     */
    @Override
    public List<SequenceMatcher> allMatches(final ByteReader reader, final long matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        final long noOfBytes = reader.length();
        if (matchPosition >= 0 && matchPosition + trie.getMinimumLength() < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one states.
            currentStates.add(trie.getInitialState());
            long currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = reader.readByte(currentPosition++);
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        result.addAll(getAllAssociations(state));
                    }
                }
            }
        }
        return result;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public SequenceMatcher firstMatch(final ByteReader reader, final long matchPosition) {
        final long noOfBytes = reader.length();
        if (matchPosition >= 0 && matchPosition + trie.getMinimumLength() < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one states.
            currentStates.add(trie.getInitialState());
            long currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = reader.readByte(currentPosition++);
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        return getFirstAssociation(state);
                    }
                }
            }
        }
        return null;
    }
    

    /**
     * @inheritDoc
     * 
     */
    @Override    
    public boolean matches(final ByteReader reader, final long matchPosition) {
        return firstMatch(reader, matchPosition) != null;
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override    
    public Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        if (matchPosition >= 0 && matchPosition + trie.getMinimumLength() < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one states.
            currentStates.add(trie.getInitialState());
            int currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = bytes[currentPosition++];
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        result.addAll(getAllAssociations(state));
                    }
                }
            }
        }
        return result;
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override    
    public SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        if (matchPosition >= 0 && matchPosition + trie.getMinimumLength() < noOfBytes) {
            final List<State> currentStates = new ArrayList<State>(1); // only ever zero or one states.
            currentStates.add(trie.getInitialState());
            int currentPosition = matchPosition;
            while (!currentStates.isEmpty() && currentPosition < noOfBytes) {
                final State currentState = currentStates.get(0);
                currentStates.clear();
                final byte currentByte = bytes[currentPosition++];
                currentState.appendNextStatesForByte(currentStates, currentByte);
                for (final State state : currentStates) {
                    if (state.isFinal()) {
                        return getFirstAssociation(state);
                    }
                }
            }
        }
        return null;
    }
    
    
    /**
     * @inheritDoc
     * 
     */
    @Override     
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return firstMatch(bytes, matchPosition) != null;
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override 
    public int getMinimumLength() {
        return trie.getMinimumLength();
    }

    
    /**
     * @inheritDoc
     * 
     */
    @Override 
    public int getMaximumLength() {
        return trie.getMaximumLength();
    }
    
    
    private SequenceMatcher getFirstAssociation(final State state) {
        final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
        return trieState.getAssociations().iterator().next();
    }
    
    
    private Collection<SequenceMatcher> getAllAssociations(final State state) {
        final AssociatedState<SequenceMatcher> trieState = (AssociatedState<SequenceMatcher>) state;
        return trieState.getAssociations();
    }

}

/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.domesdaybook.automata.transition;

import java.util.Map;
import net.domesdaybook.object.copy.DeepCopy;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;

/**
 * An implementation of {@link Transition} which matches bytes using a
 * {@link SingleByteMatcher}.
 * <p>
 * This implementation is immutable, so is thread-safe.  It is possible to
 * get the internal SingleByteMatcher used, but implementations of this interface
 * should also be immutable.
 * <p>
 * Note: "Single"ByteMatcher does not imply that it can only match a single byte
 * value; it implies it only matches a single byte (as opposed to a sequence of bytes),
 * but that match can be on many different byte values.
 * 
 * @author Matt Palmer
 */
public class SimpleTransition implements Transition {

    private final SingleByteMatcher matcher;
    private State toState;


    /**
     * Constructor for the SimpleTransition taking the {@link SingleByteMatcher}
     * to use and the {@link State} this transition links to.
     * 
     * @param matcher The SingleByteMatcher to use to match bytes for this transition.
     * @param toState The state this transition links to.
     */
    public SimpleTransition(final SingleByteMatcher matcher, final State toState) {
        this.matcher = matcher;
        this.toState = toState;
    }
   
    
    /**
     * Copy constructor for the SimpleTransition, taking another
     * SimpleTransition to copy from, and another {@link State} to link to.
     * <p>
     * Since instances of this class are immutable, an identical copy of an instance
     * of this class will always be identical to the original, making a copy constructor
     * essentially useless.
     * <p>
     * This is really a convenience constructor, which copies the matcher out of 
     * an existing SimpleTransition, but specifies a different State to 
     * link to.  It is equivalent to:
     * <code>SimpleTransition(other.getMatcher(), someState);</code>
     * 
     * @param other The SimpleTransition to copy the matcher from.
     * @param toState The State that this transition links to.
     */
    public SimpleTransition(final SimpleTransition other, final State toState) {
        this.matcher = other.matcher;
        this.toState = toState;
    }


    /**
     * @inheritDoc
     */
    @Override
    public final State getStateForByte(byte theByte) {
        return matcher.matches(theByte) ? toState : null;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public final State getToState() {
        return toState;
    }


    /**
     * @inheritDoc
     */
    @Override
    public byte[] getBytes() {
        return matcher.getMatchingBytes();
    }


    /**
     * This method is inherited from the {@link DeepCopy} interface,
     * and is redeclared here with a return type of SimpleTransition
     * (rather than DeepCopy), to make using the method easier.
     *
     * @param oldToNewObjects A map of the original objects to their new deep copies.
     * @return Transition A deep copy of this SimpleTransition and any 
     *                    States and Transitions reachable from this Transition.
     */

    @Override
    public SimpleTransition deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        SimpleTransition transitionCopy = (SimpleTransition) oldToNewObjects.get(this);
        if (transitionCopy == null) {
            oldToNewObjects.put(this, this); // put in a placeholder mapping to prevent an infinite loop.
            final State copyState = (State) toState.deepCopy(oldToNewObjects);
            transitionCopy = new SimpleTransition(this, copyState);
            oldToNewObjects.put(this, transitionCopy); // now put the real transition in.
        }
        return transitionCopy;
    }
    

    /**
     * Returns the SingleByteMatcher used in this Transition.
     * 
     * @return SingleByteMatcher the matcher used in this Transition.
     */
    public final SingleByteMatcher getMatcher() {
        return matcher;
    }
    

    /**
     * Returns a regular-expression representation of the underlying
     * SingleByteMatcher, in byte-seek syntax.
     * 
     * @return String a byteSeek regular expression representation of this Transition.
     */
    @Override
    public String toString() {
        return matcher.toRegularExpression(true);
    }

    
    /**
     * {@inheritDoc}
     * 
     */
    public void setToState(final State stateToPointAt) {
        this.toState = stateToPointAt;
    }

}
/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata;

import java.util.Map;

/**
 * DeepCopy
 *
 * An interface for deep copying of objects.
 * Each object implementing this  interface must return a deep copy of itself
 * and any child objects that are not immutable.
 * It requires an initially empty map of old to new objects to be passed in.
 * This is forwarded to other child objects implementing DeepCopy, in order that
 * only one copy of the same object is ever created.

 * @author matt
 */
public interface DeepCopy {

    /**
     * deepCopy returns a deep copy of the object implementing this interface.
     *
     * @param oldToNewObjects a map of old objects to their copies.
     * @return DeepCopy a deep copy of the object implementing this interface.
     */
    DeepCopy deepCopy(Map<DeepCopy,DeepCopy> oldToNewObjects);
}

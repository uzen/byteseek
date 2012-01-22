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
 */

package net.domesdaybook.compiler.automata;

import java.util.Collection;
import net.domesdaybook.automata.State;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;

/**
 * Compiles an expression or a collection of expressions into a Deterministic
 * Finite-state Automata (DFA).
 * 
 * @author Matt Palmer
 */
public class DfaCompiler implements Compiler<State<?>, String> {

    private static DfaCompiler defaultCompiler;
    
    /**
     * 
     * @param expression
     * @return
     * @throws CompileException
     */
    public static State dfaFrom(final String expression) throws CompileException {
        defaultCompiler = new DfaCompiler();
        return defaultCompiler.compile(expression);
    }
    
    private final Compiler<State<?>, String> nfaCompiler;
    private final Compiler<State<?>, State<?>> dfaFromNfaCompiler;
    
    /**
     * 
     */
    public DfaCompiler() {
        this(null, null);
    }

   
    
    /**
     * 
     * @param dfaFromNfaCompilerToUse
     */
    public DfaCompiler(final Compiler<State<?>, State<?>> dfaFromNfaCompilerToUse) {
        this(null, dfaFromNfaCompilerToUse);
    }    
    
    
    /**
     * 
     * @param nfaCompilerToUse
     * @param dfaFromNfaCompilerToUse
     */
    public DfaCompiler(final Compiler<State<?>, String> nfaCompilerToUse, final Compiler<State<?>, State<?>> dfaFromNfaCompilerToUse) {
        if (nfaCompilerToUse == null) {
            this.nfaCompiler = new NfaCompiler();
        } else {
            this.nfaCompiler = nfaCompilerToUse;
        }
        if (dfaFromNfaCompilerToUse == null) {
            this.dfaFromNfaCompiler = new DfaSubsetCompiler();
        } else {
            this.dfaFromNfaCompiler = dfaFromNfaCompilerToUse;
        }
    }
    
    
    @Override
    public State compile(final String expression) throws CompileException {
        State initialNfaState = nfaCompiler.compile(expression);
        return dfaFromNfaCompiler.compile(initialNfaState);
    }

    
    @Override
    public State compile(Collection<String> expressions) throws CompileException {
        State initialNfaState = nfaCompiler.compile(expressions);
        return dfaFromNfaCompiler.compile(initialNfaState);
    }
    
}

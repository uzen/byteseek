/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * http://www.opensource.org/licenses/BSD-3-Clause
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
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
 *  
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

package net.domesdaybook.compiler.automata;

import java.util.List;
import net.domesdaybook.automata.State;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;

/**
 *
 * @author matt
 */
public class DfaExpressionsCompiler implements Compiler<State, List<String>> {
    
    private static DfaExpressionsCompiler defaultCompiler;
    public static State nfaFrom(List<String> expressions) throws CompileException {
        defaultCompiler = new DfaExpressionsCompiler();
        return defaultCompiler.compile(expressions);
    }
    
    private final Compiler<State, List<String>> multiNfaCompiler;
    private final Compiler<State, State> dfaCompiler;
    
    public DfaExpressionsCompiler() {
        this(null, null);
    }
    
    public DfaExpressionsCompiler(final Compiler<State, List<String>> multiNfaCompilerToUse, Compiler<State, State> dfaCompilerToUse) {
        if (multiNfaCompilerToUse == null) {
            this.multiNfaCompiler = new NfaExpressionsCompiler();
        } else {
            this.multiNfaCompiler = multiNfaCompilerToUse;
        }
        if (dfaCompilerToUse == null) {
            this.dfaCompiler = new DfaNfaSubsetCompiler();
        } else {
            this.dfaCompiler = dfaCompilerToUse;
        }
    }
    
    @Override
    public State compile(List<String> expressions) throws CompileException {
        State nfaState = multiNfaCompiler.compile(expressions);
        return dfaCompiler.compile(nfaState);
    }

    /**
     * Turns a list of expression strings into a single expression string
     * by putting each expression in round brackets and separating them
     * with the | alternative symbol.
     * 
     * @param expressions a list of expressions
     * @return A single expression matching any of them.
     */
    private String getExpressionsAsAlternatives(List<String> expressions) {
        final StringBuilder builder = new StringBuilder();
        char separator = ' ';
        for (String expression : expressions) {
            builder.append(separator);
            builder.append("(");
            builder.append(expression);
            builder.append(")");
            separator = '|';
        }
        return builder.toString();
    }

    
}

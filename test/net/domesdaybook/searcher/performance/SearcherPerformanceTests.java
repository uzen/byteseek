/*
 * Copyright Matt Palmer 2012, All rights reserved.
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
package net.domesdaybook.searcher.performance;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.domesdaybook.searcher.multisequence.WuManberFinalFlagSearcher;
import net.domesdaybook.matcher.multisequence.ListMultiSequenceMatcher;
import net.domesdaybook.searcher.multisequence.WuManberSearcher;
import net.domesdaybook.searcher.multisequence.SetHorspoolFinalFlagSearcher;
import net.domesdaybook.searcher.multisequence.SetHorspoolSearcher;
import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.searcher.multisequence.MultiSequenceMatcherSearcher;
import net.domesdaybook.matcher.multisequence.TrieMultiSequenceMatcher;
import net.domesdaybook.searcher.performance.SearcherProfiler.ProfileResult;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import net.domesdaybook.searcher.performance.SearcherProfiler.ProfileResults;
import net.domesdaybook.searcher.sequence.HorspoolFinalFlagSearcher;
import net.domesdaybook.searcher.sequence.SundayQuickSearcher;
import net.domesdaybook.searcher.sequence.BoyerMooreHorspoolSearcher;
import net.domesdaybook.searcher.sequence.SequenceMatcherSearcher;
import net.domesdaybook.searcher.matcher.MatcherSearcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import net.domesdaybook.searcher.Searcher;
import java.util.Collection;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.sequence.ByteArrayMatcher;
import net.domesdaybook.compiler.sequence.SequenceMatcherCompiler;

/**
 * Runs the searchers against different files and inputs to search for,
 * and collects average search times for searching forwards and backwards
 * over byte arrays and using the Reader interface.
 * <p>
 * Warms up the searchers first before getting final performance results
 * in order that the JIT compiler has reached a steady state before 
 * getting final results.
 * <p>
 * Use the command line option -XX:+PrintCompilation to see whether the JIT
 * compiler is still making changes during the results collections phase.
 * <p>
 * Use the command line option -verbose:gc to observe whether garbage
 * collection is having an impact on performance statistics.
 * <p>
 * Use the command line option -XX:+UseSerialGC to force the use of the
 * serial garbage collector.  This may be better for micro-benchmarks like this.
 * 
 * @author Matt Palmer
 */
public class SearcherPerformanceTests {
    
    public final static int FIRST_WARMUP_TIMES = 5000;
    public final static int SECOND_WARMUP_TIMES = 5000;
    public final static int CYCLE_WARMUP_TIMES = 10;
    public final static int TEST_TIMES = 100;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting profiling");
        SearcherPerformanceTests tests = new SearcherPerformanceTests();
        
        System.out.println("First warm up " + FIRST_WARMUP_TIMES + " times to provoke initial JIT compilation.");
        tests.profile(FIRST_WARMUP_TIMES);
        System.out.println("Ending first warmup.");
        
        System.out.println("Second warm up " + SECOND_WARMUP_TIMES + " times to deal with class-loading JIT issues.");
        tests.profile(SECOND_WARMUP_TIMES);
        System.out.println("Ending second warmup.");
        
        cycleWarmup(tests);
        
        Thread.sleep(250); 
        System.out.println("Running performance tests " + TEST_TIMES);
        
        Thread.sleep(250); // just let things settle down before running axtual tests.        
        tests.profile(TEST_TIMES);
        Thread.sleep(250);
        
        System.out.println("Ending profiling");
    }
    
    private static void cycleWarmup(SearcherPerformanceTests tests) {
        for (int cycle = 0; cycle < CYCLE_WARMUP_TIMES; cycle++) {
            System.out.println("Cycling warmup " +cycle + " of "+ CYCLE_WARMUP_TIMES + " times.");            
            tests.profile(10);
        }
    }
    
    public SearcherPerformanceTests() {
    }
    
    
    public void profile(int numberOfTimes) {
        try {
            profileSequenceSearchers(numberOfTimes);
            profileMultiSequenceSearchers(numberOfTimes);      
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearcherPerformanceTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SearcherPerformanceTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void profileSequenceSearchers(int numberOfTimes) throws FileNotFoundException, IOException {

        // Single long phrase in ascii text:
        profileSequence("I know a banke where the wilde time blowes,", numberOfTimes);
        
        // Single long phrase in ascii text first 4096 bytes:
        profileSequence("Information about Project Gutenberg", numberOfTimes);
        
        // Uncommon string in ascii text:
        profileSequence("Midsommer", numberOfTimes);
        
        // Uncommon string ending in a common character (a space):
        profileSequence("Midsommer ", numberOfTimes);
        
        // Fairly common matching string in ascii text:
        profileSequence("Bottome", numberOfTimes);
        
        // Common short word in ascii text:
        profileSequence("and", numberOfTimes);
        
    }
    
    
    public void profileMultiSequenceSearchers(int numberOfTimes) throws IOException {
        
        profileMultiSequence(numberOfTimes, "Midsommer", "and");
    }    
    
    
    
    private void profileSequence(String sequence, int numberOfTimes) throws IOException {
        profileSequence("'" + sequence + "'", numberOfTimes, new ByteArrayMatcher(sequence));
    }
    
    private void profileSequence(String description, int numberOfTimes, SequenceMatcher matcher) throws FileNotFoundException, IOException {
        profileSearchers(description, numberOfTimes, getSequenceSearchers(matcher));
    }
    

    
    
    private void profileMultiSequence(String description, 
                                      int numberOfTimes,
                                      SequenceMatcher... matcherArgs) throws IOException {
        profileSearchers(description, numberOfTimes, getMultiSequenceSearchers(getMultiSequenceMatcher(matcherArgs)));
    }
    
    private void profileMultiSequence(int numberOfTimes, String... matcherArgs) throws IOException {
        final List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
        final StringBuilder description = new StringBuilder();
        description.append('[');
        boolean first = true;
        for (String str : matcherArgs) {
            matchers.add(new ByteArrayMatcher(str));
            if (!first) description.append(',');
            description.append('\'').append(str).append('\'');
            first = false;
        }
        description.append(']');
        profileSearchers(description.toString(), numberOfTimes, 
                         getMultiSequenceSearchers(getMultiSequenceMatcher(matchers)));
    }
    
    
    public MultiSequenceMatcher getMultiSequenceMatcher(SequenceMatcher... matcherArgs) {
        return getMultiSequenceMatcher(Arrays.asList(matcherArgs));
    }
    
    public MultiSequenceMatcher getMultiSequenceMatcher(Collection<? extends SequenceMatcher> matchers) { 
        return new TrieMultiSequenceMatcher(matchers);
    }    
    
    
    private void profileSearchers(String description, int numberOfTimes, Collection<Searcher> searchers) throws FileNotFoundException, IOException {
        SearcherProfiler profiler = new SearcherProfiler();        
        Map<Searcher, ProfileResults> results = profiler.profile(searchers, numberOfTimes);
        writeResults(description, results);              
    }
    
    
    // bug in backwards searching for sequencesearcher (probably abstract) - infinite loop.
    private Collection<Searcher> getSequenceSearchers(SequenceMatcher sequence) {
        List<Searcher> searchers = new ArrayList<Searcher>();
        searchers.add(new MatcherSearcher(sequence));
        searchers.add(new SequenceMatcherSearcher(sequence));
        searchers.add(new BoyerMooreHorspoolSearcher(sequence));
        searchers.add(new HorspoolFinalFlagSearcher(sequence));
        searchers.add(new SundayQuickSearcher(sequence)); 
        return searchers;
    }
    
    
    private Collection<Searcher> getMultiSequenceSearchers(MultiSequenceMatcher multisequence) {
        List<Searcher> searchers = new ArrayList<Searcher>();
        searchers.add(new MatcherSearcher(multisequence));
        searchers.add(new MultiSequenceMatcherSearcher(multisequence));
        searchers.add(new SetHorspoolSearcher(multisequence));
        searchers.add(new SetHorspoolFinalFlagSearcher(multisequence));
        searchers.add(new WuManberSearcher(multisequence));
        searchers.add(new WuManberFinalFlagSearcher(multisequence));
        return searchers;
    }
    
    
    private void writeResults(String description, Map<Searcher, ProfileResults> results) {
        try {
            Thread.sleep(500); // just so console mode output doesn't get mixed up.
        } catch (InterruptedException ex) {
            Logger.getLogger(SearcherPerformanceTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        String message = "%s\t%s\t%s\t%s\tForward reader:\t%d\t%d\tForward bytes:\t%d\t%d\tBack reader:\t%d\t%d\tBack bytes:\t%d\t%d";
        for (Map.Entry<Searcher, ProfileResults> entry : results.entrySet()) {
            Searcher searcher = entry.getKey();
            ProfileResults info = entry.getValue();
            for (Map.Entry<String, ProfileResult> result : info.getAllResults().entrySet()) {
                String testName = result.getKey();
                ProfileResult testInfo = result.getValue();
                String output = String.format(message, description, 
                                              searcher.getClass().getSimpleName(),
                                              searcher, testName,
                                              testInfo.forwardReaderStats.searchTime,
                                              testInfo.forwardReaderStats.searchMatches.size(),
                                              testInfo.forwardBytesStats.searchTime,
                                              testInfo.forwardBytesStats.searchMatches.size(),
                                              testInfo.backwardReaderStats.searchTime,
                                              testInfo.backwardReaderStats.searchMatches.size(),
                                              testInfo.backwardBytesStats.searchTime,
                                              testInfo.backwardBytesStats.searchMatches.size() );
               System.out.println(output);
            }
        }
    }
    
 
    
    
}

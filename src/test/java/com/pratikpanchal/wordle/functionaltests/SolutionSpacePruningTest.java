package com.pratikpanchal.wordle.functionaltests;

import com.pratikpanchal.wordle.Main;
import com.pratikpanchal.wordle.hintprovider.HintProvider;
import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SolutionSpacePruningTest {

    private final Logger log = LogManager.getLogger(this.getClass());

    @Test
    public void computeEntireSet() {
        log.info("user directory: " + System.getProperty("user.dir"));

        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();

        //Get the super set of all possible words
        List<String> words = wi.importWords(wordFile);

        //Create Trie
        TrieNode root = Trie.addWordsToTrie(words);

        //Solve automatically for all words
        //map: Number of trials to solve -> frequency
        HashMap<Integer, Integer> solutionNumberOfTrialsToFreq = new HashMap<>();

        int vsoCount=0,totalCount=0;
        int lSolved=0;

        int wordNumber = 0;
        for (String word : words) {
            ++wordNumber;
            String trace = ":::" + wordNumber + " Secret word: " + word + "...::";
            String vsoTrace="";
            HintProvider hintProvider = new HintProvider(word);
            InputGrid ig = new InputGrid();
            String hint = "BBBBB";
            int trial = 0;
            boolean captured = false, captured2 = false, captured3 = false;

            while (!hint.equals("GGGGG")) {

                //This is attempt number: trial
                ++trial;

                ComputationalInputs ci = new ComputationalInputs(ig);
                List<String> solutions = new ComputationalEngine().compute(root, ci);

                Advisor advisor = new Advisor(solutions);

                /**
                 * suggestASolution picks the top wso candidate and returns it's word
                 * This is a predictable and reproducible algorithm.
                 */
                String predictedWord = advisor.suggestASolution(solutions, Optional.of(trial));

                /**
                 * suggestASolutionFromAllEquallyLikelySolutions randomly picks a wso that matches in
                 * every metrics (ofcourse except the word) and returs it's word.
                 * This leads to an algorithm whose output can't be reproduced in all cases.
                 *
                 * The goal of creating this method was to highlight that there are still some
                 * metrics that are not yet explored to lead to the best predictable model.
                 *
                 */
//                String predictedWord = advisor.suggestASolutionFromAllEquallyLikelySolutions(solutions, Optional.of(trial));

                /**
                 * Solution Space Preemptive Pruning
                 * If there are some number of positional locks (>0)
                 * and, if this is an attempt #5 (or less) and the current solution space has more than 2 solution candidates,
                 * it is time to invoke the SolutionSpacePreemptivePruner (a.k.a. ViableComputeEngine)
                 */
                if (ci.getNumberOfPositionalLocks() >= 2 && trial <= 5 && solutions.size() > 2) {

                    //List<Character> priorityChars = ci.getListOfPriorityCharactersFromStrings(solutions);
                    List<Character> priorityChars = ci.getStrictListOfPriorityCharactersFromStrings(solutions);
                    //List<Character> priorityChars = ci.getSimpleStrictListOfPriorityCharacters();

                    Map<Character, Double> priorityCharScoreMap = new HashMap<>();
                    for (Character c : priorityChars) {
                        priorityCharScoreMap.put(c, advisor.getCharScore(c));
                    }
                    ViableComputeEngine viableComputeEngine = new ViableComputeEngine(
                            advisor.getCharPercentage(),
                            priorityChars,
                            words);
                    List<ViableComputeEngine.ViableStringObject> vsoList = viableComputeEngine.getViableStringObjectsList();

                    /**
                     * Now, we have a list of viable strings that can help us prune
                     * But we only accept this if the upcc of the top candidate in this list
                     * is more than 1 (otherwise, there is no point in pruning as it won't prune anything
                     * that can already be done by choosing from the the solution set directly)
                     */

                    if (vsoList.size() > 0 && vsoList.get(0).getUpcc() >= 2) {
                        ++vsoCount;
                        predictedWord = vsoList.get(0).getWord();
                    }
                }

                if(trial==5 || trial==6){
                    vsoTrace+="\nTrial: "+trial+"\n"+advisor.getAllWordScoreObjects(solutions, Optional.of(trial)).toString();
                }

                if (predictedWord == null) {
                    log.error("No solution found. Hint=" + hint);
                    trace += "...TERMINATED...";
                    log.error(trace);
                    throw new RuntimeException("Unable to solve for secret word: "+ word);
                }
                ++totalCount;

                //Submit the predicted word and get the hint
                hint = hintProvider.provideHint(predictedWord);

                //add the predicted word and the received hint to input grid
                ig.addRow(predictedWord, hint);

                trace += ">>t=" + trial + " [" + solutions.size() + "]" + predictedWord + "[" + hint + "] ";
            }

            if (trial > 6) {
                ++lSolved;
                log.info(trace);
                Double failureRate = ((double) lSolved * 100) / wordNumber;
                Double successRate = 100-failureRate;
                log.info("Unsolved=" + lSolved + " | Total=" + wordNumber + " | Failure rate=" + failureRate + " | Success Rate="+ successRate);
            }

            solutionNumberOfTrialsToFreq.put(trial, solutionNumberOfTrialsToFreq.getOrDefault(trial, 0) + 1);
        }

        log.info("Trial distribution: "+solutionNumberOfTrialsToFreq.toString());
        log.info("Total invocations: "+ totalCount + " vscInvocations:"+vsoCount);

        int solved = 0, total = 0;
        for(Map.Entry<Integer,Integer> entry : solutionNumberOfTrialsToFreq.entrySet()){
            if(entry.getKey()<=6){
                solved += entry.getValue();
            }
            total += entry.getValue();
        }

        log.info("Solved=" + solved + " | Total=" + total + " | Success rate=" + (((double) solved * 100) / total));
        assertEquals(12951, solved, "Solved "+solved+" word in 6 or less trials out of "+ total+" words");
    }

    /**
     *
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()==3 && trial<=5 && solutions.size()>2)
     * trial distribution:
     * {1=1, 2=161, 3=1837, 4=4444, 5=3692, 6=1843, 7=571, 8=264, 9=138, 10=79, 11=39, 12=23, 13=10, 14=4, 15=1}
     * Solved=11978 | Total=13107 | Success rate=91.38628213931487
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()==4 && trial<=5 && solutions.size()>2)
     * trial distribution:
     * {1=1, 2=169, 3=1930, 4=4346, 5=3634, 6=2076, 7=504, 8=235, 9=127, 10=49, 11=22, 12=7, 13=3, 14=2, 15=1, 16=1}
     * Solved=12156 | Total=13107 | Success rate=92.74433508812085
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()>=3 && ci.getNumberOfPositionalLocks()<=4 && trial<=5 && solutions.size()>2)
     * trial distribution:
     * {1=1, 2=159, 3=1789, 4=4364, 5=4068, 6=2075, 7=377, 8=152, 9=73, 10=36, 11=11, 12=1, 13=1}
     * Solved=12456 | Total=13107 | Success rate=95.03318837262532
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()>=2 && ci.getNumberOfPositionalLocks()<=4 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=120, 3=1581, 4=4569, 5=4232, 6=2000, 7=374, 8=127, 9=62, 10=31, 11=7, 12=2, 13=1}
     * Solved=12503 | Total=13107 | Success rate=95.39177538719768
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()>=2 && ci.getNumberOfPositionalLocks()<=4 && trial<=4 && solutions.size()>2){
     * trial distribution
     * {1=1, 2=120, 3=1581, 4=4569, 5=4633, 6=1294, 7=504, 8=208, 9=101, 10=55, 11=29, 12=8, 13=2, 14=1, 15=1}
     * Solved=12198 | Total=13107 | Success rate=93.06477454795147
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()>=1 && ci.getNumberOfPositionalLocks()<=4 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=57, 3=1348, 4=4528, 5=4304, 6=2162, 7=473, 8=143, 9=52, 10=26, 11=11, 12=2}
     * Solved=12400 | Total=13107 | Success rate=94.60593575951782
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()%2==1 && ci.getNumberOfPositionalLocks()<=5 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=98, 3=1557, 4=4427, 5=3990, 6=1970, 7=599, 8=240, 9=110, 10=57, 11=30, 12=15, 13=7, 14=5, 15=1}
     * Solved=12043 | Total=13107 | Success rate=91.8822003509575
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()>=0 && ci.getNumberOfPositionalLocks()<=4 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=47, 3=806, 4=3917, 5=4976, 6=2706, 7=504, 8=98, 9=36, 10=11, 11=4, 12=1}
     * Solved=12453 | Total=13107 | Success rate=95.01029983978027
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()>=0 && ci.getNumberOfPositionalLocks()<=4 && trial<=4 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=47, 3=806, 4=3917, 5=5463, 6=2130, 7=506, 8=143, 9=55, 10=24, 11=10, 12=5}
     * Solved=12364 | Total=13107 | Success rate=94.33127336537729
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()>=0 && ci.getNumberOfPositionalLocks()<=4 && trial<=3 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=47, 3=806, 4=4758, 5=4708, 6=1869, 7=566, 8=188, 9=85, 10=43, 11=17, 12=10, 13=6, 14=3}
     * Solved=12189 | Total=13107 | Success rate=92.99610894941634
     *
     *
     * *****************************************
     * USING STRICT LIST OF PRIORITY CHARACTERS
     * *****************************************
     *
     * Configuration:
     * if(ci.getNumberOfPositionalLocks()>=2 && ci.getNumberOfPositionalLocks()<=4 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=119, 3=1616, 4=4818, 5=4384, 6=1747, 7=298, 8=88, 9=24, 10=10, 11=1, 12=1}
     * Solved=12685 | Total=13107 | Success rate=96.78034637979705
     *
     * Configuration:
     * Using char score
     * if(ci.getNumberOfPositionalLocks()>=2 && ci.getNumberOfPositionalLocks()<=4 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=119, 3=1674, 4=5006, 5=4354, 6=1626, 7=226, 8=69, 9=23, 10=7, 11=1, 12=1}
     * Solved=12780 | Total=13107 | Success rate=97.50514991989013
     *
     * Configuration:
     * Using char score
     * if(ci.getNumberOfPositionalLocks()>=1 && ci.getNumberOfPositionalLocks()<=4 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=55, 3=1663, 4=5455, 5=4335, 6=1315, 7=232, 8=45, 9=5, 10=1}
     * Solved=12824 | Total=13107 | Success rate=97.84084840161745
     *
     * Configuration:
     * Using char score
     * if(ci.getNumberOfPositionalLocks()>=0 && ci.getNumberOfPositionalLocks()<=4 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=25, 3=1669, 4=5427, 5=4228, 6=1431, 7=270, 8=48, 9=7, 10=1}
     * Solved=12781 | Total=13107 | Success rate=97.51277943083848
     *
     * Configuration:
     * Using char score
     * if(ci.getNumberOfPositionalLocks()>=0 && ci.getNumberOfPositionalLocks()<=4 && trial<=4 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=25, 3=1669, 4=5427, 5=4456, 6=1163, 7=250, 8=81, 9=25, 10=8, 11=1, 12=1}
     * Solved=12741 | Total=13107 | Success rate=97.20759899290455
     *
     * Configuration:
     * Using char score
     * trial distribution
     * {1=1, 2=55, 3=1663, 4=5455, 5=4392, 6=1224, 7=221, 8=67, 9=18, 10=9, 11=1, 12=1}
     * Solved=12790 | Total=13107 | Success rate=97.58144502937361
     *
     * **********************************************
     * word scoring changed to positional scoring
     * **********************************************
     * Configuration:
     *  > Using char score
     * if(ci.getNumberOfPositionalLocks()>=1 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=60, 3=1759, 4=6256, 5=3826, 6=992, 7=182, 8=26, 9=3, 10=1, 11=1}
     * Solved=12894 | Total=13107 | Success rate=98.37491416800184
     *
     * Configuration:
     *  > Using char score
     * if(ci.getNumberOfPositionalLocks()>=1 && trial<=4 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=60, 3=1759, 4=6256, 5=3976, 6=809, 7=174, 8=53, 9=14, 10=3, 11=1, 12=1}
     * Solved=12861 | Total=13107 | Success rate=98.12314030670635
     *
     * Configuration:
     *  > Using char score
     * if(ci.getNumberOfPositionalLocks()>=0 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=25, 3=1669, 4=5427, 5=4229, 6=1433, 7=268, 8=47, 9=7, 10=1}
     * Solved=12784 | Total=13107 | Success rate=97.53566796368352
     *
     * Configuration:
     *  > Using char score
     * if(ci.getNumberOfPositionalLocks()>=2 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=126, 3=1708, 4=5814, 5=4240, 6=1048, 7=144, 8=23, 9=2, 10=1}
     * Solved=12937 | Total=13107 | Success rate=98.70298313878081
     *
     * Configuration:
     *  > Using char score
     * if(ci.getNumberOfPositionalLocks()>=3 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=173, 3=1777, 4=5124, 5=4196, 6=1504, 7=251, 8=63, 9=12, 10=4, 11=2}
     * Solved=12775 | Total=13107 | Success rate=97.46700236514839
     *
     * *****************************
     * Thu Feb 24
     *
     * Configuration:
     * > Using char score
     * > Using char score in ViableComputeEngine with scores of ONLY priority chars
     * if(ci.getNumberOfPositionalLocks()>=2 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=126, 3=1653, 4=5738, 5=4246, 6=1127, 7=175, 8=35, 9=6}
     * Solved=12891 | Total=13107 | Success rate=98.35202563515679
     *
     * Configuration:
     * > Using char score
     * > Using char score in ViableComputeEngine with scores of ALL characters
     * if(ci.getNumberOfPositionalLocks()>=2 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=126, 3=1653, 4=5738, 5=4245, 6=1132, 7=175, 8=33, 9=4}
     * Solved=12895 | Total=13107 | Success rate=98.38254367895019
     *
     * Configuration
     * > Using char positional score for Advisor
     * > Using char frequency score for ViableComputeEngine with scores of only Priority Characters
     * if(ci.getNumberOfPositionalLocks()>=2 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=126, 3=1709, 4=5816, 5=4234, 6=1049, 7=145, 8=24, 9=2, 10=1}
     * Solved=12935 | Total=13107 | Success rate=98.68772411688411
     *
     * Configuration:
     * > Using char positional score for Advisor
     * > Using char frequency score for ViableComputeEngine with scores ALL Characters
     * if(ci.getNumberOfPositionalLocks()>=2 && trial<=5 && solutions.size()>2)
     * trial distribution
     * {1=1, 2=126, 3=1709, 4=5813, 5=4243, 6=1042, 7=145, 8=24, 9=2, 10=2}
     * Solved=12934 | Total=13107 | Success rate=98.68009460593576
     *
     * Configuration
     * > Using char positional score for Advisor
     * > Using char freq score for ViableComputeEngine with scores of only UNIQUE PRIORITY characters
     * trial distribution
     * {1=1, 2=126, 3=1709, 4=5818, 5=4239, 6=1045, 7=143, 8=23, 9=2, 10=1}
     * Solved=12938 | Total=13107 | Success rate=98.71061264972916
     *
     * Configuration:
     * > All the above +
     * if(ci.getNumberOfPositionalLocks()>=2 && trial<=5 && solutions.size()>2 && trial!=4)
     * trial distribution
     * {1=1, 2=126, 3=1709, 4=6574, 5=2620, 6=1796, 7=221, 8=46, 9=9, 10=4, 11=1}
     * Solved=12826 | Total=13107 | Success rate=97.85610742351415
     *
     * Configuration:
     * > All the above +
     * if(ci.getNumberOfPositionalLocks()>=2 && trial<=5 && solutions.size()>2 && trial!=3)
     * trial distribution
     * {1=1, 2=126, 3=2391, 4=3673, 5=5197, 6=1465, 7=189, 8=55, 9=7, 10=2, 11=1}
     * Solved=12853 | Total=13107 | Success rate=98.06210421911956
     *
     */

    private int numberOfPositionalLocksInHint(String s){
        int n=0;
        for(int i=0; i<s.length(); i++){
            if(s.charAt(i)=='G'){
                ++n;
            }
        }
        return n;
    }
}

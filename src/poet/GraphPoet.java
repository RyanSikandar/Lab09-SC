/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import java.io.File;
import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import graph.ConcreteGraph;
import graph.Graph;

/**
 * A graph-based poetry generator.
 * 
 * <p>GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph.
 * Vertices in the graph are words. Words are defined as non-empty
 * case-insensitive strings of non-space non-newline characters. They are
 * delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 * 
 * <p>For example, given this corpus:
 * <pre>    Hello, HELLO, hello, goodbye!    </pre>
 * <p>the graph would contain two edges:
 * <ul><li> ("hello,") -> ("hello,")   with weight 2
 *     <li> ("hello,") -> ("goodbye!") with weight 1 </ul>
 * <p>where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 * 
 * <p>Given an input string, GraphPoet generates a poem by attempting to
 * insert a bridge word between every adjacent pair of words in the input.
 * The bridge word between input words "w1" and "w2" will be some "b" such that
 * w1 -> b -> w2 is a two-edge-long path with maximum-weight weight among all
 * the two-edge-long paths from w1 to w2 in the affinity graph.
 * If there are no such paths, no bridge word is inserted.
 * In the output poem, input words retain their original case, while bridge
 * words are lower case. The whitespace between every word in the poem is a
 * single space.
 * 
 * <p>For example, given this corpus:
 * <pre>    This is a test of the Mugar Omni Theater sound system.    </pre>
 * <p>on this input:
 * <pre>    Test the system.    </pre>
 * <p>the output poem would be:
 * <pre>    Test of the system.    </pre>
 * 
 * <p>PS2 instructions: this is a required ADT class, and you MUST NOT weaken
 * the required specifications. However, you MAY strengthen the specifications
 * and you MAY add additional methods.
 * You MUST use Graph in your rep, but otherwise the implementation of this
 * class is up to you.
 */
public class GraphPoet {

    private final Graph<String> graph = new ConcreteGraph<>();

    // Abstraction function:
    //   Represents a word affinity graph where vertices are words, and edges are weighted by adjacency frequency.
    // Representation invariant:
    //   - Graph vertices represent case-insensitive words extracted from the corpus.
    //   - Edge weights are non-negative integers.
    // Safety from rep exposure:
    //   - The graph is encapsulated and not directly exposed.
    //   - The class does not expose any mutable references to internal state.

    /**
     * Create a new poet with the graph from corpus (as described above).
     *
     * @param corpus text file from which to derive the poet's affinity graph
     * @throws IOException if the corpus file cannot be found or read
     */
    public GraphPoet(File corpus) throws IOException {
        List<String> lines = Files.readAllLines(corpus.toPath());
        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            content.append(line).append(" ");
        }

        String[] words = content.toString().toLowerCase().split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            int currentWeight = graph.set(word1, word2, graph.targets(word1).getOrDefault(word2, 0) + 1);
        }

        checkRep();
    }

    private void checkRep() {
        for (String vertex : graph.vertices()) {
            for (int weight : graph.targets(vertex).values()) {
                assert weight >= 0 : "Edge weight must be non-negative";
            }
        }
    }

    /**
     * Generate a poem.
     *
     * @param input string from which to create the poem
     * @return poem (as described above)
     */
    public String poem(String input) {
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i].toLowerCase();
            String word2 = words[i + 1].toLowerCase();

            // Find the best bridge word
            String bridge = null;
            int maxWeight = 0;

            for (Map.Entry<String, Integer> target : graph.targets(word1).entrySet()) {
                String potentialBridge = target.getKey();
                int weight = target.getValue() + graph.targets(potentialBridge).getOrDefault(word2, 0);
                if (graph.targets(potentialBridge).containsKey(word2) && weight > maxWeight) {
                    bridge = potentialBridge;
                    maxWeight = weight;
                }
            }

            result.append(words[i]).append(" ");
            if (bridge != null) {
                result.append(bridge).append(" ");
            }
        }

        result.append(words[words.length - 1]);
        return result.toString();
    }

    @Override
    public String toString() {
        return "GraphPoet with graph: " + graph.toString();
    }
}
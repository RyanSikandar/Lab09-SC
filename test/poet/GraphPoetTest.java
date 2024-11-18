package poet;

import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import graph.ConcreteGraph;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class GraphPoetTest {

    @Test
    void testPoemGeneration() throws IOException {
        // Create a temporary file with a simple corpus
        File corpus = File.createTempFile("corpus", ".txt");
        corpus.deleteOnExit();
        Files.writeString(corpus.toPath(), "This is a test of the Mugar Omni Theater sound system.");

        GraphPoet poet = new GraphPoet(corpus);

        String input = "Test the system.";
        String expectedOutput = "Test of the system.";
        assertEquals(expectedOutput, poet.poem(input));
    }

    @Test
    void testPoemNoBridgeWords() throws IOException {
        File corpus = File.createTempFile("corpus", ".txt");
        corpus.deleteOnExit();
        Files.writeString(corpus.toPath(), "Hello world!");

        GraphPoet poet = new GraphPoet(corpus);

        String input = "Goodbye world.";
        String expectedOutput = "Goodbye world.";
        assertEquals(expectedOutput, poet.poem(input));
    }

    @Test
    void testEmptyInput() throws IOException {
        File corpus = File.createTempFile("corpus", ".txt");
        corpus.deleteOnExit();
        Files.writeString(corpus.toPath(), "Hello world!");

        GraphPoet poet = new GraphPoet(corpus);

        String input = "";
        String expectedOutput = "";
        assertEquals(expectedOutput, poet.poem(input));
    }

    @Test
    void testInvalidFile() {
        assertThrows(IOException.class, () -> new GraphPoet(new File("nonexistent.txt")));
    }


    @Test
    public void testToStringWithGraphData() throws IOException {
        // Arrange: Create a poet with a simple corpus
        File corpus = createTempCorpus("Hello world. Hello everyone.");
        GraphPoet poet = new GraphPoet(corpus);

        // Act: Call the toString() method
        String result = poet.toString();

        // Assert: Verify that the string contains graph details
        assertTrue(result.contains("GraphPoet with graph: "), "toString() should include 'GraphPoet with graph:'.");
        assertTrue(result.contains("hello"), "Graph should include the word 'hello'.");
        assertTrue(result.contains("world"), "Graph should include the word 'world'.");
        assertTrue(result.contains("everyone"), "Graph should include the word 'everyone'.");
    }


    // Helper to create a temporary corpus file
    private File createTempCorpus(String content) throws IOException {
        File tempFile = File.createTempFile("corpus", ".txt");
        tempFile.deleteOnExit();
        try (java.io.FileWriter writer = new java.io.FileWriter(tempFile)) {
            writer.write(content);
        }
        return tempFile;
    }
}

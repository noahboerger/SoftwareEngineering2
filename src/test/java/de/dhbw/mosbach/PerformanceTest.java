package de.dhbw.mosbach;

import de.dhbw.mosbach.file.parser.JSONMatchFieldParser;
import de.dhbw.mosbach.file.parser.MatchFieldParser;
import de.dhbw.mosbach.solve.YajisanKazusanSolver;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PerformanceTest {

    @Test
    public void doPerformanceTest() throws URISyntaxException {
        List<URL> urls = List.of(
                Objects.requireNonNull(getClass().getClassLoader().getResource("speed/test_10x10_1.json")),
                Objects.requireNonNull(getClass().getClassLoader().getResource("speed/test_10x10_2.json")),
                Objects.requireNonNull(getClass().getClassLoader().getResource("speed/test_10x10_3.json"))
        );

        for(URL url : urls) {

            MatchFieldParser parser = new JSONMatchFieldParser(Paths.get(Objects.requireNonNull(url).toURI()).toString());
            Assert.assertEquals(JSONMatchFieldParser.ParsingValidationResult.PARSED_SUCCESSFUL, parser.getParsingValidationResult());
            YajisanKazusanSolver solver = new YajisanKazusanSolver(parser.getMatchFieldOfParsedFile().orElse(null));
            long beginning = System.currentTimeMillis();
            solver.getSolvedMatchField();
            System.out.println("Time for Solving 10x10: " + (System.currentTimeMillis() - beginning));
        }
        //10x10 - maximal 7 Sekunden, normalerweise circa 1 Sekunde
        //12x12 - circa 170 Sekunden
    }
}

package de.dhbw.mosbach;

import de.dhbw.mosbach.file.parser.JSONMatchFieldParser;
import de.dhbw.mosbach.file.parser.MatchFieldParser;
import de.dhbw.mosbach.file.validator.FileValidator;
import de.dhbw.mosbach.file.validator.JSONFileValidator;
import de.dhbw.mosbach.solve.YajisanKazusanSolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

public class IntegrationsTest {

    @Test
    public void runYajisanKazusanSolverIT() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("parser/Test_Valid.json");

        FileValidator validator = new JSONFileValidator(Paths.get(Objects.requireNonNull(url).toURI()).toString());
        Assertions.assertEquals(JSONFileValidator.ValidationResult.VALID_FILE, validator.getValidationResult());

        MatchFieldParser parser = new JSONMatchFieldParser(Paths.get(url.toURI()).toString());
        Assertions.assertEquals(JSONMatchFieldParser.ParsingValidationResult.PARSED_SUCCESSFUL, parser.getParsingValidationResult());

        YajisanKazusanSolver solver = new YajisanKazusanSolver(parser.getMatchFieldOfParsedFile().orElseThrow(() -> new IllegalStateException("Field can not be null here")));
        Assertions.assertEquals(TestUtils.getSolvedTestMatchField(), solver.getSolvedMatchField());
    }
}

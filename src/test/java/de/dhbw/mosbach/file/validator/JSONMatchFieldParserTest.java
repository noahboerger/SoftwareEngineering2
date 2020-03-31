package de.dhbw.mosbach.file.validator;

import de.dhbw.mosbach.file.parser.JSONMatchFieldParser;
import de.dhbw.mosbach.file.parser.MatchFieldParser;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class JSONMatchFieldParserTest {

    @Test
    public void parseValidFileTest() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("parser/Test_Valid.json");
        MatchFieldParser parser = new JSONMatchFieldParser(Paths.get(url.toURI()).toString());
        Assert.assertEquals(JSONMatchFieldParser.ParsingValidationResult.PARSED_SUCCESSFUL, parser.getParsingValidationResult());
        Assert.assertEquals(5, parser.getMatchFieldOfParsedFile().get().getSize());
    }

    @Test
    public void parseNotExistingFileTest() throws URISyntaxException {
        MatchFieldParser parser = new JSONMatchFieldParser("resources/NotExisting.json");
        Assert.assertEquals(JSONMatchFieldParser.ParsingValidationResult.FILE_LOADING_ERROR, parser.getParsingValidationResult());
    }

    @Test
    public void parseInvalidFileTest() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("parser/Test_Invalid.json");
        MatchFieldParser parser = new JSONMatchFieldParser(Paths.get(url.toURI()).toString());
        Assert.assertEquals(JSONMatchFieldParser.ParsingValidationResult.FILE_NOT_VALID, parser.getParsingValidationResult());
    }
}

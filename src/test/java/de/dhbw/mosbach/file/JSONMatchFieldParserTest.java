package de.dhbw.mosbach.file;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class JSONMatchFieldParserTest {

    @Test
    public void parseValidFileTest() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("parser/Test_Valid.json");
        JSONMatchFieldParser parser = new JSONMatchFieldParser(Paths.get(url.toURI()).toString());
        Assert.assertEquals(JSONMatchFieldParser.ParsingValidationResult.PARSED_SUCCESSFUL, parser.getParsingValidationResult().get());
        Assert.assertEquals(5, parser.getMatchFieldOfParsedJSON().get().getSize());
    }

    @Test
    public void parseNotExistingFileTest() throws URISyntaxException {
        JSONMatchFieldParser parser = new JSONMatchFieldParser("resources/NotExisting.json");
        Assert.assertEquals(JSONMatchFieldParser.ParsingValidationResult.IO_EXCEPTION, parser.getParsingValidationResult().get());
    }

    @Test
    public void parseInvalidFileTest() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("parser/Test_Invalid.json");
        JSONMatchFieldParser parser = new JSONMatchFieldParser(Paths.get(url.toURI()).toString());
        Assert.assertEquals(JSONMatchFieldParser.ParsingValidationResult.FILE_NOT_VALID, parser.getParsingValidationResult().get());
    }
}

package de.dhbw.mosbach.file.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.mosbach.matchfield.MatchField;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class JSONMatchFieldParser implements MatchFieldParser {

    private final String filePathMatchFieldFile;
    private final JSONMatchFieldParser.ParsingResult parsingResult;

    public JSONMatchFieldParser(final String filePathMatchFieldFile) {
        this.filePathMatchFieldFile = filePathMatchFieldFile;
        parsingResult = parseFile();
    }

    private JSONMatchFieldParser.ParsingResult parseFile() {
        final File fieldFile = new File(filePathMatchFieldFile);
        if (!fieldFile.exists() || !fieldFile.isFile()) {
            return new ParsingResult(null, ParsingValidationResult.FILE_LOADING_ERROR);
        }
        MatchField matchField;
        try {
            final ObjectMapper mapper = new ObjectMapper();
            matchField = mapper.readValue(fieldFile, MatchField.class);
        } catch (IOException ie) {
            return new ParsingResult(null, ParsingValidationResult.FILE_NOT_VALID);
        }
        return new ParsingResult(matchField, ParsingValidationResult.PARSED_SUCCESSFUL);
    }

    @Override
    public Optional<MatchField> getMatchFieldOfParsedFile() {
        return Optional.ofNullable(parsingResult.matchField);
    }

    @Override
    public ParsingValidationResult getParsingValidationResult() {
        return parsingResult.parsingValidationResult;
    }

    private static class ParsingResult {
        private final MatchField matchField;
        private final MatchFieldParser.ParsingValidationResult parsingValidationResult;

        private ParsingResult(final MatchField matchField, final MatchFieldParser.ParsingValidationResult parsingValidation) {
            this.matchField = matchField;
            this.parsingValidationResult = parsingValidation;
        }
    }
}

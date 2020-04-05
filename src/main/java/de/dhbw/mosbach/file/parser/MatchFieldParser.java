package de.dhbw.mosbach.file.parser;

import de.dhbw.mosbach.matchfield.MatchField;

import java.util.Optional;

//Möglichkeit weitere Parser (XML, CSV etc. zu erstellen)
public interface MatchFieldParser {

    Optional<MatchField> getMatchFieldOfParsedFile();

    ParsingValidationResult getParsingValidationResult();

    enum ParsingValidationResult {
        FILE_LOADING_ERROR("Laden der Datei ist fehlgeschlagen!"),
        FILE_NOT_VALID("Die Datei beinhaltet kein valides Spielfeld!"),
        PARSED_SUCCESSFUL("Datei wurde erfolgreich geparsed.");

        private final String errorMessage;

        ParsingValidationResult(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

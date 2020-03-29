package de.dhbw.mosbach.file;

import de.dhbw.mosbach.matchfield.MatchField;
import de.dhbw.mosbach.matchfield.fields.Field;
import de.dhbw.mosbach.matchfield.fields.HintField;
import de.dhbw.mosbach.matchfield.fields.StandardField;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class JSONMatchFieldParser {

    private final String filePathMatchFieldFile;

    private final ParsingResult parsingResult;

    public JSONMatchFieldParser(String filePathMatchFieldFile) {
        this.filePathMatchFieldFile = filePathMatchFieldFile;
        parsingResult = parseFile();
    }

    public enum ParsingValidationResult {
        IO_EXCEPTION("Laden der Datei ist fehlgeschlagen!"),
        FILE_NOT_VALID("Die JSON beinhaltet kein valides Spielfeld!"),
        PARSED_SUCCESSFUL("JSON wurde erfolgreich geparsed.");

        private String errorMessage;

        ParsingValidationResult(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private static class ParsingResult {
        private final MatchField matchField;
        private final ParsingValidationResult parsingValidationResult;

        private ParsingResult(MatchField matchField, ParsingValidationResult parsingValidation) {
            this.matchField = matchField;
            this.parsingValidationResult = parsingValidation;
        }
    }

    private ParsingResult parseFile() {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePathMatchFieldFile), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            return new ParsingResult(null, ParsingValidationResult.IO_EXCEPTION);
        }
        String jsonString = contentBuilder.toString();
        jsonString = jsonString.replaceFirst("(?s)[}](?!.*?[}])", "");
        List<List<Field>> parsedField;
        try {
            parsedField = parseFullField(jsonString);
        } catch (IllegalArgumentException iae) {
            return new ParsingResult(null, ParsingValidationResult.FILE_NOT_VALID);
        }
        return new ParsingResult(new MatchField(parsedField), ParsingValidationResult.PARSED_SUCCESSFUL);
    }

    private List<List<Field>> parseFullField(String jsonFullFieldString) {
        List<List<Field>> parsedField = new ArrayList<>();
        if (!jsonFullFieldString.contains("\"rows\":")) {
            throw new IllegalArgumentException();
        }
        String jsonFullFieldStringTrimmed = jsonFullFieldString.substring(jsonFullFieldString.indexOf('{') + 1, jsonFullFieldString.lastIndexOf('}')).trim();
        for (String jsonRowString : firstLevelJSONArraySplit(jsonFullFieldStringTrimmed)) {
            parsedField.add(parseRowString(jsonRowString));
        }
        return parsedField;
    }

    private List<Field> parseRowString(String jsonRowString) {
        List<Field> parsedRow = new ArrayList<>();
        if (!jsonRowString.contains("\"fields\":") || amountOfCharInString('[', jsonRowString) != 1 || amountOfCharInString(']', jsonRowString) != 1) {
            throw new IllegalArgumentException();
        }
        String jsonRowStringTrimmed = jsonRowString.substring(jsonRowString.indexOf('[') + 1, jsonRowString.lastIndexOf(']')).trim();
        for (String jsonFieldString : firstLevelJSONArraySplit(jsonRowStringTrimmed)) {
            parsedRow.add(parseFieldString(jsonFieldString));
        }
        return parsedRow;
    }

    private Field parseFieldString(String jsonFieldString) {
        Map<String, String> jsonMap = parseSimpleKeyValues(jsonFieldString);
        if (!jsonMap.containsKey("TYPE")) {
            throw new IllegalArgumentException();
        }
        if (jsonMap.get("TYPE").contains("STANDARD")) {
            return new StandardField();
        } else if (jsonMap.get("TYPE").contains("HINT")) {
            if (jsonMap.containsKey("ARROW") && jsonMap.containsKey("AMOUNT")) {
                HintField.ArrowDirection arrowDirection = HintField.ArrowDirection.valueOf(jsonMap.get("ARROW"));
                int amount;
                try {
                    amount = Integer.parseInt(jsonMap.get("AMOUNT"));
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException();
                }
                return new HintField(arrowDirection, amount);
            }
        }
        throw new IllegalArgumentException();
    }

    private Map<String, String> parseSimpleKeyValues(String jsonString) {
        Map<String, String> returnMap = new HashMap<>();

        String[] attributeSplit = jsonString.split(",");
        for (String attribute : attributeSplit) {
            String[] keyValueSplit = attribute.split(":");
            if (keyValueSplit.length != 2) {
                throw new IllegalArgumentException();
            }
            String key = keyValueSplit[0].trim().replaceAll("\"", "").toUpperCase();
            String value = keyValueSplit[1].trim().replaceAll("\"", "").toUpperCase();
            returnMap.put(key, value);
        }
        return returnMap;
    }

    private List<String> firstLevelJSONArraySplit(String jsonString) {
        List<String> returnList = new ArrayList<>();
        jsonString = jsonString.trim();
        int intActStart = -1;
        int countOpenClose = -1;
        for (int i = 0; i < jsonString.length(); i++) {
            if (jsonString.charAt(i) == '{') {
                if (intActStart == -1) {
                    intActStart = i;
                    countOpenClose = 1;
                } else {
                    countOpenClose++;
                }
            } else if (jsonString.charAt(i) == '}') {
                countOpenClose--;
            }
            if (countOpenClose == 0) {
                returnList.add(jsonString.substring(intActStart + 1, i));
                intActStart = -1;
                countOpenClose = -1;
            }
        }
        return returnList;
    }

    private int amountOfCharInString(char searchingChar, String searchingString) {
        return (int) searchingString.chars().filter(x -> x == searchingChar).count();
    }

    public Optional<MatchField> getMatchFieldOfParsedJSON() {
        return Optional.ofNullable(parsingResult.matchField);
    }

    public Optional<ParsingValidationResult> getParsingValidationResult() {
        return Optional.ofNullable(parsingResult.parsingValidationResult);
    }
}
/*

http://www.objgen.com/json?demo=true

// Model & generate Live JSON data values
// interactively using a simple syntax.
// String is the default value type
product = Live JSON generator

// Number, Date & Boolean are also supported
// Specify types after property names
version n = 3.1
releaseDate d = 2014-06-25
demo b = true

// Tabs or spaces define complex values
person
  id number = 12345
  name = John Doe
  phones
    home = 800-123-4567mat
    mobile = 877-123-1234

  // Use [] to define simple type arrays
  email[] s = jd@example.com, jd@example.org
  dateOfBirth d = 1980-01-02
  registered b = true

  // Use [n] to define object arrays
  emergencyContacts[0]
    name s = Jane Doe
    phone s = 888-555-1212
    relationship = spouse
  emergencyContacts[1]
    name s = Justin Doe
    phone s = 877-123-1212
    relationship = parent

// See our Help page for additional info
// We hope you enjoy the tool!

Example:

rows[0]
  fields[0]
    type = HINT_FIELD
    arrow = RIGHT
    amount = 2
  fields[1]
    type = HINT_FIELD
    arrow = RIGHT
    amount = 2
  fields[2]
    type = STANDARD_FIELD
  fields[3]
    type = STANDARD_FIELD
  fields[4]
    type = HINT_FIELD
    arrow = DOWN
    amount = 1
rows[1]
  fields[0]
    type = STANDARD_FIELD
  fields[1]
    type = STANDARD_FIELD
  fields[2]
    type = HINT_FIELD
    arrow = DOWN
    amount = 1
  fields[3]
    type = STANDARD_FIELD
  fields[4]
    type = HINT_FIELD
    arrow = UP
    amount = 0
rows[2]
  fields[0]
    type = STANDARD_FIELD
  fields[1]
    type = HINT_FIELD
    arrow = RIGHT
    amount = 1
  fields[2]
    type = STANDARD_FIELD
  fields[3]
    type = HINT_FIELD
    arrow = LEFT
    amount = 2
  fields[4]
    type = STANDARD_FIELD
rows[3]
  fields[0]
    type = HINT_FIELD
    arrow = UP
    amount = 1
  fields[1]
    type = STANDARD_FIELD
  fields[2]
    type = HINT_FIELD
    arrow = UP
    amount = 0
  fields[3]
    type = STANDARD_FIELD
  fields[4]
    type = STANDARD_FIELD
rows[4]
  fields[0]
    type = HINT_FIELD
    arrow = RIGHT
    amount = 3
  fields[1]
    type = STANDARD_FIELD
  fields[2]
    type = STANDARD_FIELD
  fields[3]
    type = HINT_FIELD
    arrow = LEFT
    amount = 1
  fields[4]
    type = HINT_FIELD
    arrow = LEFT
    amount = 2
 */

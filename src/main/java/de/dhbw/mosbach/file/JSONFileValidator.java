package de.dhbw.mosbach.file;

import java.io.File;

public class JSONFileValidator {
    private final String filePathMatchFieldFile;
    private final File matchFieldFile;
    private final ValidationResult validationResult;


    public JSONFileValidator(String filePathMatchFieldFile) {
        this.filePathMatchFieldFile = filePathMatchFieldFile;
        this.matchFieldFile = new File(filePathMatchFieldFile);
        this.validationResult = analyzeFile();
    }

    public enum ValidationResult {
        FILE_PATH_EMPTY("Wähle zunächst eine Datei aus!"),
        FILE_NOT_EXISTING("Ausgewählte Datei existiert nicht!"),
        FILE_IS_NOT_A_JSON("Ausgewählte Datei ist keine JSON!"),
        VALID_FILE("Datei ist valide.");

        private String errorMessage;

        ValidationResult(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private ValidationResult analyzeFile() {
        if(filePathMatchFieldFile == null || filePathMatchFieldFile.isBlank()) return ValidationResult.FILE_PATH_EMPTY;
        if (fileNotExisting()) return ValidationResult.FILE_NOT_EXISTING;
        if (fileIsNoJSON()) return ValidationResult.FILE_IS_NOT_A_JSON;
        return ValidationResult.VALID_FILE;
    }

    private boolean fileNotExisting() {
        return !matchFieldFile.exists();
    }

    private boolean fileIsNoJSON() {
        return !matchFieldFile.isFile() || !matchFieldFile.getName().toLowerCase().endsWith(".json");
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
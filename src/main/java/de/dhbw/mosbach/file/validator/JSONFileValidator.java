package de.dhbw.mosbach.file.validator;

import java.io.File;

public class JSONFileValidator implements FileValidator {
    private final String filePathMatchFieldFile;
    private final File matchFieldFile;
    private final ValidationResult validationResult;


    public JSONFileValidator(final String filePathMatchFieldFile) {
        this.filePathMatchFieldFile = filePathMatchFieldFile;
        this.matchFieldFile = new File(filePathMatchFieldFile);
        this.validationResult = analyzeFile();
    }

    private ValidationResult analyzeFile() {
        if (filePathMatchFieldFile == null || filePathMatchFieldFile.isBlank()) {
            return ValidationResult.FILE_PATH_EMPTY;
        } else if (fileNotExisting()) {
            return ValidationResult.FILE_NOT_EXISTING;
        } else if (fileIsNoJSON()) {
            return ValidationResult.FILE_IS_NOT_A_JSON;
        }
        return ValidationResult.VALID_FILE;
    }

    private boolean fileNotExisting() {
        return !matchFieldFile.exists();
    }

    private boolean fileIsNoJSON() {
        return !matchFieldFile.isFile() || !matchFieldFile.getName().toUpperCase().endsWith(".JSON");
    }

    @Override
    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
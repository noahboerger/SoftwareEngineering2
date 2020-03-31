package de.dhbw.mosbach.file.validator;

//Möglichkeit weitere Validatoren (XML, CSV etc. zu erstellen)
public interface FileValidator {

    ValidationResult getValidationResult();

    enum ValidationResult {
        FILE_PATH_EMPTY("Wähle zunächst eine Datei aus!"),
        FILE_NOT_EXISTING("Ausgewählte Datei existiert nicht!"),
        FILE_IS_NOT_A_JSON("Ausgewählte Dateityp kann nicht gelesen werden!"),
        VALID_FILE("Datei ist valide.");

        private String errorMessage;

        ValidationResult(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

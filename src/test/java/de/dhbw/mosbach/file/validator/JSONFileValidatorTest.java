package de.dhbw.mosbach.file.validator;

import de.dhbw.mosbach.file.validator.JSONFileValidator;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class JSONFileValidatorTest {

    @Test
    public void ValidFileTest() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("validator/Test_Valid.json");
        FileValidator validator = new JSONFileValidator(Paths.get(url.toURI()).toString());
        Assert.assertEquals(JSONFileValidator.ValidationResult.VALID_FILE, validator.getValidationResult());
    }

    @Test
    public void EmptyFilePathTest() {
        FileValidator validator = new JSONFileValidator("");
        Assert.assertEquals(JSONFileValidator.ValidationResult.FILE_PATH_EMPTY, validator.getValidationResult());
    }

    @Test
    public void FileNotExistingTest() {
        FileValidator validator = new JSONFileValidator("resouces/NotExisting.json");
        Assert.assertEquals(JSONFileValidator.ValidationResult.FILE_NOT_EXISTING, validator.getValidationResult());
    }

    @Test
    public void FileIsNoJSONTest() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("validator/Test_No_JSON.txt");
        FileValidator validator = new JSONFileValidator(Paths.get(url.toURI()).toString());
        Assert.assertEquals(JSONFileValidator.ValidationResult.FILE_IS_NOT_A_JSON, validator.getValidationResult());
    }
}

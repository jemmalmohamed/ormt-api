package ma.org.ormt.core.validators.file;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {

    private long maxSizeBytes;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.maxSizeBytes = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null) {
            return true; // null values are validated with @NotNull
        }

        return file.getSize() <= maxSizeBytes;
    }
}

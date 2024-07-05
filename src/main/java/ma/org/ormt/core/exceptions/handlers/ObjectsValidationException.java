package ma.org.ormt.core.exceptions.handlers;

import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ObjectsValidationException extends RuntimeException {
    private final Map<String, Set<String>> errors;
}
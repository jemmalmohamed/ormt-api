package ma.org.ormt.security.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ResourceDto {
    private String name;
    private String displayName;
    private List<String> scopes;
}

package ma.org.ancfcc.pva.modules.mission.dto.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FieldXlsParams {

    private String key;

    private Value value;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Value {
        private int index;
        private String label;
        private String dateFormat;
    }
}

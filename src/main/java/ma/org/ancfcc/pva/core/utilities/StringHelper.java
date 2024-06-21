package ma.org.ancfcc.pva.core.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringHelper {

    public static String getSafeString(Object attribute) {
        return attribute != null ? attribute.toString() : "";
    }

}

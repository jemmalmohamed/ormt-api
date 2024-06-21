package ma.org.ancfcc.pva.core.utilities;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityInspector {

    public static boolean isFieldPresentInEntity(String field, Class<?> entityClass) {
        try {
            entityClass.getDeclaredField(field);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

}

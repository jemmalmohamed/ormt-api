package ma.org.ormt.core.utilities;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityInspector {

    public static boolean isFieldPresentInEntity(String field, Class<?> entityClass) {
        Class<?> current = entityClass;
        while (current != null) {
            try {
                current.getDeclaredField(field);
                return true;
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return false;
    }

}

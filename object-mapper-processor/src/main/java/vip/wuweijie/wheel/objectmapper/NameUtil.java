package vip.wuweijie.wheel.objectmapper;

import java.util.regex.Pattern;

/**
 * @author Wu Weijie
 */
public class NameUtil {

    public static final String INNER_SUFFIX = "__Inner";
    public static final String INNER_PREFIX = "__";
    public static final Pattern INNER_CLASS_PATTERN = Pattern.compile(INNER_PREFIX + "(\\w+)" + INNER_SUFFIX);

    public static String toGetterName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String toSetterName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String toVarName(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            return typeName;
        }
        if (Character.isLowerCase(typeName.charAt(0))) {

            return "_" + typeName;
        }
        return typeName.substring(0, 1).toLowerCase() + typeName.substring(1);
    }

    public static String toInnerClassName(String className) {
        return INNER_PREFIX + className + INNER_SUFFIX;
    }

    public static boolean isInnerClass(String className) {
        return INNER_CLASS_PATTERN.matcher(className).matches();
    }
}

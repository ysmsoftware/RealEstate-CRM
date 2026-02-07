package com.ysminfosolution.realestate.error.util;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidationMessageBuilder {

    private ValidationMessageBuilder() {}

    public static String build(FieldError error) {
        String path = error.getField();
        Object rejectedValue = error.getRejectedValue();

        List<String> parts = new ArrayList<>();
        Matcher matcher = Pattern
                .compile("(\\w+)(?:\\[(\\d+)])?")
                .matcher(path);

        while (matcher.find()) {
            String name = matcher.group(1);
            String index = matcher.group(2);

            if (index != null) {
                parts.add(singularize(name) + " " + (Integer.parseInt(index) + 1));
            } else {
                parts.add(prettify(name));
            }
        }

        return "Validation failed for "
                + String.join(" → ", parts)
                + ". Rejected value: "
                + formatRejectedValue(rejectedValue);
    }

    private static String singularize(String value) {
        return value.endsWith("s")
                ? value.substring(0, value.length() - 1)
                : value;
    }

    private static String prettify(String value) {
        return value.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
    }

    private static String formatRejectedValue(Object value) {
        return value == null ? "null" : "'" + value.toString() + "'";
    }
}

package com.jgfx.gui.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.jackson.Log4jJsonObjectMapper;

/**
 * Simple helper for naming
 */
public interface INameable {

    /**
     * @return returns the classes name, used for convince, it's lower case and unqualified by default
     */
    default String name() {
        return name(false);
    }

    /**
     * @return returns the classes name, used for convince
     */
    default String name(boolean lower) {
        return name(lower, false, true);
    }

    /**
     * If it's qualified then we want the package with the name
     *
     * @return returns the classes name, used for convince
     */
    default String name(boolean lower, boolean qualified, boolean removeExtension) {
        var name = qualified ? getClass().getName() : getClass().getSimpleName();
        if (removeExtension)
            name = StringUtils.removeEndIgnoreCase(name, "cmp");
        return lower ? name.toLowerCase() : name;
    }

    /**
     * @return returns a string that is properly indented
     */
    @SneakyThrows
    default String formatJson(String input) {
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(input, Object.class);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    }
}

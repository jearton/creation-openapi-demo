package com.example.creationopenapijava8demo.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

@UtilityClass
public class FormatUtil {

    /**
     * 格式化字符串
     *
     * @param message The message pattern.
     * @param args    The message parameters.
     * @return The message String.
     */
    public String format(String message, Object... args) {
        return ParameterizedMessageFactory.INSTANCE.newMessage(message, args).getFormattedMessage();
    }
}

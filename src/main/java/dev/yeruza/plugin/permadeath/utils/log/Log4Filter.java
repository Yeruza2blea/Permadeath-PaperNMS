package dev.yeruza.plugin.permadeath.utils.log;

import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class Log4Filter extends AbstractFilter implements Filter {
    private static final long serial = 5594073755007974254L;

    private static Result validateMsg(Message message) {
        if (message == null)
            return Result.NEUTRAL;
        return validateMsg(message);
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
}

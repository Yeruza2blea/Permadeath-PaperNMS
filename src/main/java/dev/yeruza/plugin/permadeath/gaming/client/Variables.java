package dev.yeruza.plugin.permadeath.gaming.client;

import java.util.regex.Pattern;

public interface Variables {
    Pattern VAR_BRACKETS = Pattern.compile("[{]([^{}]+)[}]");
    String TOTEM_PROB = "{totem_prob}";

    String TOTEMS_TOTAL = "{totems_total}";

    String CURRENT_DAY = "{current_day}";

    String MISSING_DAYS = "{missing_days}";

    String GONE_DAYS = "{gone_days}";

    String PLAYER  = "{player}";

    String PLAYERS_SIZE = "{players_size}";

    String TIME = "{time}";

    String LOCAL_DATE = "{local_date}";

    String LOCAL_TIME = "{local_time}";

    String LOCAL_DATETIME = "{local_datetime}";
}

package org.lab6.gui.localization;

import java.util.Locale;

public enum LanguagesEnum {
    RUSSIAN("Русский", new Locale("ru", "RU")),
    ESTONIAN("Eesti", new Locale("et", "EE")),
    CATALAN("Català", new Locale("ca", "ES")),
    ENGLISH_SA("English (South Africa)", new Locale("en", "ZA"));

    private final String displayName;
    private final Locale locale;

    LanguagesEnum(String displayName, Locale locale) {
        this.displayName = displayName;
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
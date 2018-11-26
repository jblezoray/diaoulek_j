package fr.jblezoray.diaoulek.userinterface;

enum AnsiEscapeColors {

    RESET  ("\u001B[0m"),

    BLACK  ("\033[0;30m"),
    RED    ("\033[0;31m"),
    GREEN  ("\033[0;32m"),
    YELLOW ("\033[0;33m"),
    BLUE   ("\033[0;34m"),
    PURPLE ("\033[0;35m"),
    CYAN   ("\033[0;36m"),
    WHITE  ("\033[0;37m");

    private final String code;

    AnsiEscapeColors(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}

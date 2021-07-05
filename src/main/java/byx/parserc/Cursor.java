package byx.parserc;

public final class Cursor {
    private final String input;
    private final int index;

    public Cursor(String input, int index) {
        this.input = input;
        this.index = index;
    }

    public String getInput() {
        return input;
    }

    public int getIndex() {
        return index;
    }

    public boolean end() {
        return index == input.length();
    }

    public char current() {
        return input.charAt(index);
    }

    public Cursor next() throws ParseException {

        return new Cursor(input, index + 1);
    }

    @Override
    public String toString() {
        return input.substring(index);
    }

    public String toFriendlyString() {
        return input + "\n" + " ".repeat(index) + "^";
    }
}

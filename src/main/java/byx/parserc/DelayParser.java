package byx.parserc;

public class DelayParser<T> implements Parser<T> {
    private Parser<T> parser;

    public void set(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public ParseResult<T> parse(Cursor cursor) throws ParseException {
        return parser.parse(cursor);
    }
}

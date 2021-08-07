package byx.parserc;

public class DelegateParser<T, E> implements Parser<T, E> {
    private Parser<T, E> parser = Parsers.empty();

    public void set(Parser<T, E> parser) {
        this.parser = parser;
    }

    @Override
    public ParseResult<T, E> parse(Cursor<E> cursor) throws ParseException {
        return parser.parse(cursor);
    }
}

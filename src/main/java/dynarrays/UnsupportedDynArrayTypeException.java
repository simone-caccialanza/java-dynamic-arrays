package dynarrays;

public class UnsupportedDynArrayTypeException extends RuntimeException {
    private static final String UNSUPPORTED_TYPE_MESSAGE = "Unsupported dyn array type: ";
    public UnsupportedDynArrayTypeException(Class<?> type) {
        super(UNSUPPORTED_TYPE_MESSAGE + type);
    }
}

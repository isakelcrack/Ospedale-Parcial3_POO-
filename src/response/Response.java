package response;

public class Response<T> {

    private final int statusCode;
    private final String message;
    private final T data;

    private Response(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> ok(String message, T data) {
        return new Response<>(200, message, data);
    }

    public static <T> Response<T> created(String message, T data) {
        return new Response<>(201, message, data);
    }

    public static <T> Response<T> badRequest(String message) {
        return new Response<>(400, message, null);
    }

    public static <T> Response<T> forbidden(String message) {
        return new Response<>(403, message, null);
    }

    public static <T> Response<T> notFound(String message) {
        return new Response<>(404, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
}

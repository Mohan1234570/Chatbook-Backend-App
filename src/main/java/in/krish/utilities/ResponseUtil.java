package in.krish.utilities;

import in.krish.binding.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<ApiResponse<T>> buildResponse(
            HttpStatus status, String message, T data) {

        ApiResponse<T> response = new ApiResponse<>(
                status.value(),
                message,
                data
        );

        return new ResponseEntity<>(response, status);
    }
}


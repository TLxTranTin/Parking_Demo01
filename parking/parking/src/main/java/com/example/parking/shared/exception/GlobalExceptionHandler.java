package com.example.parking.shared.exception;

import jakarta.validation.ConstraintViolationException;
import com.example.parking.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.MissingServletRequestParameterException;

@RestControllerAdvice // cái annotation này là của Spring 
// Nói cho spring biết là đây là class bắt lỗi cho toàn bộ RestController( nó giống như trạm bắt lỗi trung tâm )
// Nếu ko có thì Spring sẽ ko biết class này là class chuyên xử lý lỗi
// Khi đó các Method như handleBusinessException , handleResourceNotFoundException sẽ là các method thường sẽ ko đc tự động gọi khi có lỗi xảy ra
// Tóm lại có cái này thì controller nó sẽ sạch hơn rất nhiều
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class) // cái annotation này là của spring
    // Nếu có lỗi kiểu BusinessException thì hãy chạy hàm bên dưới để xử lý
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) { // hàm này làm j
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {// hàm này làm j
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // còn cái này trả về 404 NotFound
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Lỗi này xảy ra khi DTO validation thất bại 
    // Ví dụ trong dto có kiểm tra trống mà reqquest alf trống thì báo lỗi
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {// hàm này dùng khi ko tìm thấy dữ liệu thfi sẽ trả về lỗi 
        Map<String, String> errors = new HashMap<>(); // đang tạo ra 1 cái mmap để chứa danh sách lỗi validate : key   = tên field bị lỗi ,value = nội dung lỗi

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        }); // giải thích chi tiết tất cả các cái này cho tôi 
// Trong đó :
//  + ex : là object lỗi MethodArgumentNotValidException nó chứa thôi tin lỗi
//  + .getBlindingResult() : là hàm lấy ra thông tin sai ở đâu ( cho biết DTO vừa rồi sai ở đâu)
//  + .getFieldErrors() : là hàm lấy ra các field bị lỗi
//  + .forEach(error->{}) : là duyệt qua từng lỗi trong danh sách
//  +   error.getField() : là lấy tên field bị lỗi
//  + error.getDefaultMessage() : là lấy message bị lỗi
//  errors.put() là đưa lỗi vào map
        return ResponseEntity // đây là responese object dùng để tar về , nó có đầy đủ các thứu như:
        //  HTTP Status , header , body
                .badRequest()// nghĩa là HTTP status là 400 BadRequest
                .body(ApiResponse.error("Validation failed", errors));// đây là phần body của Json
// Ví dụ 
// {
//   "success": false,
//   "message": "Validation failed",
//   "data": {
//     "username": "Username không được để trống",
//     "password": "Password không được để trống"
//   }
// }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), "Request parameter is required");

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Missing required request parameter", errors));
    }
    @ExceptionHandler(Exception.class) // hàm này bắt tất cả các lỗi còn lại chưa có handle riêng
    public ResponseEntity<ApiResponse<?>>/* cái khai báo ResponseEntity<ApiResponse<?>> này nghĩa là sao ?  */ handleException(Exception ex) {// hàm này làm j
        ex.printStackTrace();
        return ResponseEntity
                .internalServerError() // đây là báo HTTP status là 500 lỗi phía server
                .body(ApiResponse.error("Internal server error"));// giải thích chi tiết tất cả các cái này cho tôi 
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(
            ConstraintViolationException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(error -> {
            String field = error.getPropertyPath().toString();
            errors.put(field, error.getMessage());
        });

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Validation failed", errors));
    }
}
// cái ResponseEntity<..> thì bên trong nó là body cảu Json 
// cái ApiResponse<?> này là 1 wildcard generic tức là nó có thể chứa dữ liệu bất kì 

// Nhiệm vụ của cái file này là bắt lỗi của toàn bộ hệ thống và trả về Responese Json đẹp , để thống nhất với frontend
//  Nếu không có file này thì khi lỗi xảy ra trả về cho frontend sẽ rất alf thô , và mỗi controller phải tự try catch
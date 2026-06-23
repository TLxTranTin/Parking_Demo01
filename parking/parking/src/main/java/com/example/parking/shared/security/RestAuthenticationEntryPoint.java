package com.example.parking.shared.security;

import com.example.parking.shared.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
// file này là anh em song sinh với RestAccessDeniedHandler nhưng nó xử lý lỗi khác
// File này dùng để xủ lý lỗi 401 Unauthorized ( chưa đăng nhập / ko có tokrn / token hết hạn)
// Tóm lại là khi Client gọi API cần đăng nhập nhưng chưa đăng nhập thì file này sẽ chạy và trả lỗi
//  401 Unauthorized dạng JSON chuẩn Format chung ApiResponse ko trả lung tung
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // Cái interface này là của Spring Security
    // Nó chuyên dùng để xử lý trường hợp User chưa xác thực tức là lỗi 401
    // Hiểu đơn giản là interface này sinh ra để tự động gọi khi xảy ra lỗi 401 và Spring sẽ tự lo từ a đến á ,
    //  Chúng ta chỉ cần định nghĩa lại response thôi
    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // ObjectMapper ở đây dùng để chuyển Java thành Json là 1 bean có sẵn của Jackson nên chỉ cần inject vào Constructor là đc
    }

    @Override
    public void commence(
    // Là hàm bắt buộc phải override khi implement AuthenticationEntryPoint
    // Spring Security sẽ tự gọi hàm này khi phát hiện request chưa xác thực hợp lệ 
    // Luồng chạy như này :
    //  - Client gọi API cần đăng nhập thì :
    //      + Spring Security kiểm tra
    //      + Không có Authentication hợp lệ 
    //      + Spring gọi RestAuthenticationEntryPoint.commence()
    //      + Trả về 401 JSON
    // - yêu cầu có đủ 3 tham số của hàm commence() là :
    //      + HttpServletRequest ( nó là Object đại diện cho request gồm các thông tin như : URL Method Header IP Parameter)
    //      + HttpServletResponse ( nó là Object response mà BE chuản bị trả về cho client dùng nó để set status context-type body)
    //      + AuthenticationException
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); 
        // 2 cái này set phần đầu 
        // còn cái object-mapper set phần body 
        objectMapper.writeValue(
                response.getOutputStream(), // cái này là sao vẫn ko hiểu lắm khúc này 
                ApiResponse.error("Unauthorized")
                // ở đây là chuyển cái Object ApiResponse thành nội dung của JSON
        );
    }
}
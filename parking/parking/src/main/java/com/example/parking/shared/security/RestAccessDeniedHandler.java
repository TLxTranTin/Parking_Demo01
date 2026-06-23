package com.example.parking.shared.security;

import com.example.parking.shared.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
// File này là để xử lý lỗi Forbidden(Là lỗi ko có quyền truy cập vào tài nguyên ) cho REST API 
// là khi user đã dăng nhập kiểm tra đúng token rồi mà ko đủ quyền để gọi API , thì sẽ gọi RestAccessDeniedHandler
// thì cái này giúp tar Json lỗi thay vì Json mặc định
// 1. File này dùng để :
//  - Xử lý trường hợp đã đăng nhập nhưng không có quyền
    // Ví dụ : 
    //  @PreAuthorize("hasRole('ADMIN')
    //  @GetMapping("/admin/users")
    //  public ApiResponse<?> getUsers(){...}
    // Thì khi STAFF gọi Api này:
    //  - Mặc dù token hợp lệ 
    //  - Nhưng role là STAFF mà API yêu cầu là ADMIN nên sẽ chặn 
    //  - Trả lỗi 403 Forbidden về FE 
    //  - Và trả về Json dạng :
    //      {
    //          "sucess": false,
    //          "message": "Forbidden",
    //          "data": null
    //      } 
    //   Cái này tùy vào ApiResponse viết như thế nào 
    // Nếu ko có file này thì Spring security sẽ trả về lỗi mặc định kiểu HTML hoặc Response ko đúng format chung của dự án
    // nên chúng ta cần định nghĩa file bắt lỗi để ép lỗi 403 cũng trả về đúng format RETS API
@Component 
// Cái này báo đây là Bean thì mới inject vào file khác ko có thì ko tự inject đc
// Bean này thường inject vào file SecurityConfig là file phân quyền của cả hệ thống
// Thì khi ko có quyền sẽ tự chạy file này và trả về ApiResponse tương xứng
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    // AccessDeniedHandler là 1 inteerface của Spring Sevurity nó chuyên xử lý lỗi 403 forbidden
    // Tức là nếu có đnăg nhập mà ko đủ quyền 
    // Ngoài ra 401 là lỗi UnAuthorized là lỗi chưa đăng nhập , sai token , token hết hạn-
// AccessDeniedHandler là j , nó đc dùng ở file nào khác
    private final ObjectMapper objectMapper;
    // cái ObjectMapper này là sao ? nó đâu ra thường thì nó đc sử dụng để làm gì ?
    public RestAccessDeniedHandler(ObjectMapper objectMapper) { 
        // Thì Spring Boot đã tự cấu hình Jackson và tạo sẵn Bean ObjectMapper rồi 
        // Nên khi svieets như này thì Spring sẽ tự inject vào 
        // ObjectMapper là class của thư viện Jackson ( dủng để chuyển Object Java <==> JSON và ngược lại )
        // Nó dùng để chuyên object java thành Json , hoặc Json thành object java 
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle( // Đây là hàm bắt buộc phải Override vì class đang implement AccessDeniedHandler
        // Spring Security sẽ tự gọi hàm này khi lỗi 403 xảy ra 
        // Mình ko tự gọi hàm này trong Controller
// Luồng thực tế là :
// Request vào backend -> JWT Filter kiểm tra token -> SecurityContextHolder cso Authentication
//  -> Spring kiểm tra quyền -> Không đủ quyền -> Ném AccessDeniedHandler.handle() -> trả về JSON 403 cho FE
// ( ở đây ném 1 cách tự động ko cần lập trình viên phải code ) 
            HttpServletRequest request,
            HttpServletResponse response,                   
            AccessDeniedException accessDeniedException
            // Có 3 tham số trong handle() :
            //  - HttpServletRequest request đại diện cho object request mà client gửi lên
            //      + nó gồm Method , header  , ip , body ,...
            //  - thứ 2 là HttpServletResponse response  đại diện cho ob response BE chuẩn bị trả về cho Clinet 
            //      + dùng nó để set status , context , body cho response 
            //  - Thứ 3 là AccessDeniedException accessEdniedException là exception báo lỗi ko đủ quyền
            //      + Ví dụ message có thể là "Access Denied" trong code chúng ta thì chưa dùng nso
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // đây là set Status cho response SC_FORBIDDEN là 1 hằng số có giá trị 403
        // thì thay vì viết như trên thì ta có thể viết như sau :
        //  response.setStatus(403);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // cái này là báo cho client biết body response này là Json
        // MediaTpye.APPLICATION_JSON_VALUE có giá trị là application/json
        // thì thay vì viết như trên ta có thể viết nhu sau :
        // response.setContentType("application/json");
        objectMapper.writeValue(
                response.getOutputStream(), 
                ApiResponse.error("Forbidden") // Forbidden là sao 
        );// đoạn này là lấy Object ApiResponse.error("Forbidden") chuyển thành JSON rồi ghi nó trực tiếp vào Body của HTTP response
    }
}
// ApiResponse là 1 object java dùng để chuẩn hóa response trả về cho frontend
// Nói dễ hiểu : thay vì mỗi Api trả dữ liệu lung tung , mình sẽ bọc tất cả vào 1 format chung
// Do lập trình  viên định nghĩa
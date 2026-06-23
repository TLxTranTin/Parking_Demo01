package com.example.parking.auth.adapter.in.web;

import com.example.parking.auth.adapter.in.web.dto.LoginRequest;
import com.example.parking.auth.adapter.in.web.dto.LoginResponse;
import com.example.parking.auth.adapter.in.web.dto.RegisterRequest;
import com.example.parking.auth.adapter.in.web.dto.RegisterResponse;
import com.example.parking.auth.application.port.in.ILoginUseCase;
import com.example.parking.auth.application.port.in.IRegisterUserUseCase;
import com.example.parking.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Tổng thể thì class này là cửa vào API cho Auth. Frontend muốn login/register thì sẽ gọi vào class này


// =>RestController = biển báo tao là Controller
@RestController // cái annotation này dùng để báo cho Spring biết đây là class controller dùng để nhận HTTP request và trả dữ liệu Json về cho FE
// Mô hình là FE gọi API -> Spring tìm controller phù hợp -> chạy method trong controller -> trả về Json cho Fe
// Nếu tìm ko ra method tương ứng thì BE ẽ tra về 404 Not Found
@RequestMapping("/api/auth") // cái này là annotation đặt đường dẫn gốc cho tất cả các API trong class này 
// Nó nói rằng tất cả endpoint trong AuthController đều bắt đầu = "/api/auth"
public class AuthController {

    private final IRegisterUserUseCase registerUserUseCase;
    private final ILoginUseCase loginUseCase;
// Trên là 2 biến interface để chúng ta gọi nghiệp vụ 1 cách gián tiếp 
// Để cho controller ko phải tự xử lý nghiệp vụ mà controller nó chỉ nhận request rồi cho các thk bên dưới xử lý
// Tại sao lại dùng interface như : IRegisterUserUseCase , ILoginUseCase mà ko gọi thẳng thằng Service
// Vì chúng ta đang đi theo hướng Clean Architecture + Hexagonal
// Cách này giúp Controller ko phụ thuộc cứng vào class Service cụ thể , giúp code dễ dàng thay đổi đúng ý tưởng của kiến trúc sạch
    public AuthController( // đây là constructor injection để sử dụng dịch vụ do 2 thk Interface nó cung cấp và Spring nó sẽ tự tìm bean phù hợp rồi truyền vào constructor (là bean Service đó)
        // Nói 1 cách dễ hiểu là Spring tự bơm AuthService vào AuthController thông qua hàm constructor
            IRegisterUserUseCase registerUserUseCase,
            ILoginUseCase loginUseCase
            // Ví dụ AuthService là class đang Implement cho 2 interface này và đang đc Spring quản lý dưới dạng Bean Service và khi chạy thì 
            // Spring sẽ tự gắn nóp vào cho cái Interface này , khi nào cần thì sẽ chạy dịch vụ của thằng AuthService
            // Nói cách khác là như truyền 1 Object AuthService nó nằm dưới dạng interface
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }
// Quy ước thường dùng của các phương thức HTTP :
// GET lấy dữ liệu 
// POST Tạo mới / thực hiện hành động
// PUT Cập nhật toàn bộ 
// PATCH Cập nhật 1 phần 
// DELETE Xóa
    @PostMapping("/register") // cái truyền vào mấy cái này là đường dẫn API
    // cái này là kết hợp với đường dẫn gốc ở trên tạo thành : phương thức + đường dẫn chính xác:
    // Post là phương thức , nếu là GetMapping("/register") thfi phương thức là Get , kết hợp với đường dẫn gốc ta có :
    // Post /api/auth/register
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
        // Phân tích 1 chút thì :
        // RegisterResponse đây là dữ liệu thật của chức năng register
        // Là cái nội dung sẽ trả lại cho FE
        // Json:
        //      {
        //              "id":"id",.....
        //      
        //      }
        // Còn ApiResponse<> đây là lớp bọc chung cho tất cả Response mà chúng ta định nghĩa
        // Là cái quy chuẩn mà chúng ta đưa ra ( xem ở file ApiResponse ở Shared)
        // Còn ResponseEntity là class của Spring dùng để trả lại toàn bọ gói tin HTTP response gồm nhiều thứ hơn như :
        //      + HTTP status
        //      + Header nếu cần 
        //      + Body 
        //      ( Muốn biết thêm xem khuôn dạng gói tin HTTP nhé )
        // => từ đó chúng ta có thể định nghĩa cách mà trả về cho FE chi tiết hơn dễ trả lại Status hơn
        //
        // Giống như việc chúng ta muốn gửi bưu phẩm qua bưu điện vậy , lúc đầu chúng ta phải tự đóng gói rồi dán tem đem đến bưu cục rồi bưu cục lại đóng gói để vận chuyển 
        // thì trong quá trình đóng gói đó cần các thông tin như trạng thái , trạm tiếp theo , địa chỉ , ... 
        
            @Valid @RequestBody RegisterRequest request
            // Cái Annotation Valid này là dùng để kích hoạt Validation trong DTO
            // Thì trong DTO chúng ta có rất nhiều cái Annotation để Validation như NotBlank , Size ,....
            // Thì ở Controller có cái này thì trc lúc chạy nó sẽ kiểm tra xem có vi phạm cái Validation ko , nếu có thì sẽ trả lại lỗi
            // lỗi có dạng là validation là đc GlobalExceptionHandler bắt lỗi và trả về cho FE

            // Vậy câu hỏi là ko có nó thì sao ?
            //  - Nếu ko có thì nếu giả sử dữ liệu rỗng và ko check ở đây thì nó sẽ lọt vào Service/UseCase 
            //  dẫn đến các trường hợp buồn cười như là lưu dữ liệu trống vào database , hay lưu dữ liệu ko mong muốn 
            // Nói thẳng ra nó như bảo vệ đứng trc Controller vậy , bắt kiếm tra khi đi vào nghiệp vụ , nếu ko qua đc trhfi báo lỗi cho Fe
            //
            // =====================================================
            // Tiếp đến là cái Annotation RequestBody :
            //  Nó như báo cho Spring rằng , hãy lấy dữ liệu Json trong body của HTTP request
            //  rồi chuyển thành Object đứng sau nó ở đây là RegisterRequest
            // Thì Spring sẽ tự động Map sang từng trường và cất dữ liệu vào đó tạo thành object RegisterRequest 
            // Nói cách đơn giản hơn nó là cái dùng để  chuyển dữ liệu từ trong HTTP thành object mà BE có thể xử lý

    ) {
        RegisterResponse response = registerUserUseCase.register(request);
        // Ở đây chúng ta bắt đầu đi vào nghiệp vụ thật của thằng Interface cung cấp bằng cách gọi hàm của chính interface đó
        // Interface nhưu cái thằng trung gian vậy , hình dung như lúc ta đi du lịch thì có thằng hướng dẫn viên du lịch , chúng ta chỉ cần trả tiền cho nó thì nó sẽ tự 
        //      + đặt xe 
        //      + thuê phòng 
        //      + Đặt vé vào cổng
        //      +...
        // Tóm lại chỉ là người đứng trung gian chứ nó ko phải là thằng lái , ko phải là thằng chủ cho thuê phòng , ko phải chủ du lịch ma fchir là người trung gian 
        // Để khi chúng ta muốn kiểu ko đi bằng xe này nữa thì chỉ cần nói vs nó và ko cần bt nó làm như thế nào mà vẫn đạt đc mục đích
        // ResponseEntity là j ở đâu ra , tác dụng nó là j , tại sao phải dùng nó , nếu dko dùng thì sao 
        return ResponseEntity.ok( // cái hàm .ok() này là sẽ tạo ra 1 gói tin HTTP có status là 200 OK và body là nội dung truyền vào hàm ok()
                ApiResponse.success("Register successfully", response)
        );
    }

    @PostMapping("/login")
    // Tương tự với PostMapping("/login") thì sẽ là : Post /api/auth/login
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = loginUseCase.login(request);

        return ResponseEntity.ok( // ResponseEntity ở đây như đại diện để cho chúng ta thao tác với gói tin HTTP
                ApiResponse.success("Login successfully", response)
        );
    }
}
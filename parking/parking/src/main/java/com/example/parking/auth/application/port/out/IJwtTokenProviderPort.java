package com.example.parking.auth.application.port.out;

import com.example.parking.auth.domain.model.AuthUser;

public interface IJwtTokenProviderPort {

    // Tại sao chúng ta lại xem đây là file nằm trong mục Port out ( tức là dịch vụ bên ngoài)
    // - Đây là 1 file rất quan trọng . Nó là cổng giao tiếp giữa tầng Application và dịch vụ JWT bên ngoài
    // - Lý do cho vào cổng out là vì JWT ko phải nghiệp vụ cốt lõi của hệ thống
    // Nó phụ thuộc vào mấy thứ kỹ thuật như :
    //  - Thư viện JJWT
    //  -Secret Key
    //  - Expiration time
    //  - Thuật toán ký token
    //  - Spring Security Filter
    // Những thứ này là kỹ thuật từ bên ngoài và chúng ta phải xem nó như 1 cái hộp đen và chỉ đóng gói lại thành các chức năng 
    //   để cho logic nghiệp vụ của chúng ta sử dụng nó và nó chạy hay diễn ra như thế nào thì ko quan tâm , vì vậy mới có interface này
    String generateToken(AuthUser user);// hàm này có tác dụng j 
// Hàm này dùng để tạo token khi chúng ta login thành công
    boolean validateToken(String token);// hàm này có tác dụng j 
// Hàm này để kiểm tra token có hợp lệ hay không
    String extractUsername(String token);// hàm này có tác dụng j 
// Hàm này dùng để lấy username từ token
    String extractRole(String token);// hàm này có tác dụng j 
// Hàm này dùng để lấy vai trò ra từ token
    Long extractUserId(String token);// hàm này có tác dụng j 
// Hàm này dùng để lấy id ra từ token

// cái này rất hữu ích , nếu chúng ta gửi thẳng ID cảu user lên thì họ chỉ cần gửi lại 1 gói tin với ID khác thì có thể sử dụng dịch 
// vụ của người khác => ko nên 
}

// Luồng hoạt động thật trong hệ thống:
// Khi Fe gửi username + pass
// thì nó sẽ vào hệ thống và gặp thk controller 
// controller.login()
// loginUseCase chạy
// kiểm tra username + pass
// đúng tạo token 
// JwtTokenProvidePort.generateToken(user)
// rồi kẹp token vào Dto xong trả về cho FE

// JWT là cơ chế kỹ thuật để xác thực request. Nó ko phải bản chất của nghiệp vụ gửi xe
// Cái Adapter thật sẽ ở đâu ? 
// - Cái implermentation thật có thể nằm ở adapter -> security , bên trong nó sẽ là nơi triển khai cách mà nó cung cấp dịch vụ





// câu hỏi ngoài lề : Token là gì ?
// Token là vé xác nhận cái Requets này là của ai  , 
// chỉ có những client có token mới sử dụng đc các chức năng nâng cao của 1 hệ thống

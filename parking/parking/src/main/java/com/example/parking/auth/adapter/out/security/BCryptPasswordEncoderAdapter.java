package com.example.parking.auth.adapter.out.security;

import com.example.parking.auth.application.port.out.IPasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
// PaswwordEncoder là 1 interface cảu spring security nó dùng để mã hóa password và kiểm tra passwword người dùng có đúng với password mã hóa hay ko 
// Bản thân nó chx nói lên là nmos sử dụng thuật toán nào , và thuật toán ở đây chúng ta sử dụng là BCryptPasswordEncoder
import org.springframework.stereotype.Component;

@Component// cái này là j , nó từ đâu ra , tại sao phải dùng nó , ko có nó thì sao , trường hợp mới dùng nó
// cái annotation này là của spring , nói rằng đây alf 1 class quan trọng hãy tạo và quản lý nó giúp tôi
// tóm lại nó là bean 
// nó cũng như @Service cx báo đây là bean cho spring quản lý , nhưng @Service là 1 dạng đặc biệt của component
// điểm khác nhau của @Component và @Service là @Component là kêu Spring quản lý dành cho những class chung chung con @Service thfi dùng cho các class nghiệp vụ chính
// @Component
// → Tôi là một object Spring cần quản lý.

// @Service
// → Tôi là một object Spring cần quản lý, và tôi xử lý nghiệp vụ.

// @Repository
// → Tôi là một object Spring cần quản lý, và tôi làm việc với database.

// @Controller / @RestController
// → Tôi là một object Spring cần quản lý, và tôi nhận request HTTP.

public class BCryptPasswordEncoderAdapter implements IPasswordEncoderPort {

    private final PasswordEncoder passwordEncoder; // cái này là j , nó từ đâu ra , tại sao phải dùng nó , ko có nó thì sao , trường hợp mới dùng nó
    // cái final là như const bên C++ và nó bắt chúng ta phải gán cho nó 1 giá trị 
    public BCryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) { // cái này là j , nó từ đâu ra , tại sao phải dùng nó , ko có nó thì sao , trường hợp mới dùng nó
        // đây là 1 hàm constructer 
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        // đây là 1 hàm encode khi truyền vào 1 chuỗi thì nó sẽ má hóa thành 1 dãy hashcode
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) { // cái này là j , nó từ đâu ra , tại sao phải dùng nó , ko có nó thì sao , trường hợp mới dùng nó
        // trên là hàm encode thì cũng cần 1 hàm để xác minh coi nhập vafoi có đúng encode ko chứ để check đăng nhập
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

// DIP là nguyên lý thiết kế code 
//      Hiểu đơn giản là DIP trả lời cho câu hỏi class nên phụ thuộc vào ai , 
//      nó phát biểu là class nên phụ thuộc vào interface chứ ko nên phụ thuộc vào implement
//          - Nếu mà phụ thuộc và trực tiếp class cụ thể thì sau này đổi class thì phải can thiệp vào code cũ nên ko đẹp
//          - Vì trong dự án lớn ko ai muốn vì 1 chi tiết nhỏ mà thay đổi code của logic nghiệp vụ lớn cả,
//            Tức là phải lặn vào AuthService để chỉnh lại.( mà vấn đề là càng sửa code nghiệp vụ càng nhiều nguy cơ lỗi càng lớn)
//            mà thay vào đó chúng ta đi đến implement và thay đổi nó thôi
// còn DI là cách đưa object vào class
//      


// MỘT VẤN ĐỀ NỮA VỀ IMPLEMENT
// đó là xử lý như thế nào khi có nhiều implement cho 1 cái interface là chúng ta có thể 
// Thứ nhất là đặt annotation trong class implementation và thứ 2 là tạo factory để xử lý nên chọn cái nào trong logic nghiệp vụ
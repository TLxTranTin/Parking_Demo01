package com.example.parking.auth.adapter.out.security;

import com.example.parking.auth.application.port.out.IJwtTokenProviderPort;
import com.example.parking.auth.domain.model.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
 
// Nhiệm vụ của class này là Tạo JWT token khi login thành công 
// Kiểm tra token frontend gửi lên có hợp lệ hay không 
// Đọc username/ role / userid từ token
// Tóm lại nó là thằng tạo ra token và đọc token 
// vì sao nó nằm ở adapter.out.security 
// vì class này nó dùng thư viện bên ngoài :
//  + io.jsonwebtoken.Jwts
//  + io.jsonwebtoken.Claims
//  + io.jsonwebtoken.JwtException
//  + javax.crypto.SecretKey
// Những cái trên là những class chính của thư viện JJWT dùng để tạo JWT và đọc/kiểm tra JWT
//  VÍ dụ :
//  ``` 
//  String token = Jwts.builder()
//      .subject(username)
//      .claim("role" , role)
//      .isuedAt(new Date())
//      .expiration(new Date(System.currentTimeMillis()+expirationMs))
//      .signWith(secretKey)
//      .compact();
//  ```
// Hiểu đơn giản Jwts.builder() là tôi muốn bắt đầu tạo 1 JWT mới
// còn khi đọc token :
// Claims claims = Jwts.parser()
//      .verifyWith(secretKey)
//      .build()
//      .parseSignedClaims(token)
//      .getPayload();
// Nghĩa là tôi muốn kiểm tra chữ ký token , rồi lấy dữ liệu bên trong token ra
// Claims là phần dữ liệu bên trong JWTs
// Trong đó Claims chính là phần payload
// Phân biệt giữ PayLoad và body của HTTP
// 1. Body là phần dữ liệu của HTTP request/response
// ví dụ bên fe gửi lên be
// POST /api/auth/login
//json
//  {
//    "username": "staff01",        | đây là phần 
//    "password": "123456"          | body
//  }
//  
//  Trong spring boot sẽ nhận dữ liệu từ body bằng annotation RequestBody để chuyển dữ liệu thành object mong muốn
// =======================
// còn Payload là phần dữ liệu nằm bên trong jWT
// Thì khi login thành công BE sẽ tạo token kiểu 
// header.payload.signature
// thì phần payload này có thể chứa dữ liệu mà chúng ta quy định cho nó , 1 điều quan trọng là payload ko nên chứa mật khẩu
//================================================================
// JwtException là lỗi chung liên quan đến JWT
// Lỗi này có thể xảy ra khi:
// Token sai định dạng
// Token hết hạn
// Token bị chỉnh sửa
// Token đăng kí bằng secret khác
// Token ko hợp lệ
// Khi có lỗi thì nó sẽ giúp cho chúng ta ném lỗi lên ( code ném lỗi này nó nằm trong thư viện Jwt)
// ===============================================================
// SecretKey là key bí mật dùng để ký token và kiếm tra token
// Ví dụ chúng ra có secret trong application.properties
// app.jwt.secret = <your-super-secret-key-your-super-secret-key>


@Component // Component là báo cho Spring rằng hãy tạo ra 1 bean ( là 1 object của class và được quản lý)
// khi khai báo là 1 bean thì chúng ta có thể sử dụng thông qua interface đúng ko ? ( mới có thể viết hàm constructor injection)
// Khi nó ko phải là bean thì chúng ta phải tự new:
// Ví dụ : IJwtTokenProviderPort jwtProvider = new JwtTokenProviderAdapter(); 
// Tức là tự khai báo ra thằng Nghiệp vụ logic vào , còn khi là bean thì sẽ do Spring quản lý 
public class JwtTokenProviderAdapter implements IJwtTokenProviderPort {

    private final SecretKey secretKey; 
    // SecretKey là keieur dữ liệu của thư viện ngoài , nó là kiểu dữ liệu biểu diễn khóa bí mật dùng trong mã hóa / ký dữ liệu
    // Trong Jwt dùng để :
    // Ký token khi backend tạo token
    // Kiểm tra chữ ký khi FE gửi token
    private final long expirationMs;
    // expiration là thời gian sống của token tính bằng milis
    // ví dụ trong application.properities có:
    // app.jwt.expiration-ms = 86400000 <=> 24h
    // thì lúc này sau 24h token sẽ hết hạn
    // =================================================================================
    // Value là annotation của Spring dùng để lấy giá trị từ file cấu hình
    // Ví Dụ :
    //  @Value("${app.jwt.secret}") String secret
    // thì nó sẽ tìm trong file cấu hình là chỗ nào có dùng có tên là : app.jwt.secret 
    // sau đó sẽ lấy giá trị sau dấu = để gán cho cái biến sau cái annotation này
    public JwtTokenProviderAdapter(
            @Value("${app.jwt.secret}") String secret, // cái annotation ở đây là làm j  , nội dung truyền vào cho nó là j , biến này là j
            @Value("${app.jwt.expiration-ms}"/*cú pháp có $ là j v thấy lạ v*/) long expirationMs // cái annotation ở đây là làm j  , nội dung truyền vào cho nó là j , biến này là j
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); 
        //  Keys là 1 class của thư viện JJWT
        // Ở đây có hàm secret.getBytes(StandardCharsets.UTF_8) , vậy nó là j:
        //  secret ở đây đng là kiểu dữ liệu String
        // Những thuật toán ký JWT ko dùng chữ trực tiếp mà nó dùng dữ liệu dạng byte nên phải chuyển sang byte
        //  - Dữ liệu truyền vào là StandardCharsets.UTF_8 là bảng mã UTF_8 để chuyển chữ thành byte đúng theo bảng mã
        // Hàm hmacShaKeyFor là dùng để tạo SecretKey theo thuật toán HMAC
        this.expirationMs = expirationMs;
        // đây là hàm dựng cho Class bean này , và khi Spring tạo bean thì nó sẽ chạy hàm này:
        //  Nó sẽ làm những công việc như sau:
        //  Đọc app.jwt.secret từ application.properties
        //  Đọc app.jwt.expiration-ms từ application.properties
        //  Truyền 2 giá trị đó vào constructor
        //  Chuyển secret String thành dữ liệu dạng SecretKey của thưu viện bên ngoài để sử dụng
    }

    @Override
    public String generateToken(AuthUser user) { // hàm này là hàm tạo ra token khi login thành công
        Date now = new Date(); // ngày giờ tính tới mili-s
        Date expiryDate = new Date(now.getTime() + expirationMs); // cái này là đang tính thời diếm hết hạn token
        // Hàm getTime() sẽ trả về thời gain dạng số miliseconds cách mốc UTC bao nhiêu đó

        return Jwts.builder()
                .subject(user.getUsername())
                // hàm này đang gắn subject cho token , và cái subject này là 1 phần của payload
                // tham số đầu tiên gắn vào token là subject của token là 1 chuỗi j đó
                .claim("userId", user.getId())
                // Cái Claim là hàm thêm dữ liệu phụ vào token là thông tin nằm trong payload của JWT
                .claim("role", user.getRole().name())
                // chúng ta biết getRole() sẽ trả về enum và nó chỉ là enum và cái hàm name() là chuyển từ enum sang String để gắn vào role
                .issuedAt(now)
                // hàm này ghi thời điểm phát hành token
                .expiration(expiryDate)
                // hàm này ghi thời điểm token hết hạn 
                .signWith(secretKey, Jwts.SIG.HS256)
                // Hàm này ký token bằng secretKey và thuật toán sử dụng để ký token 
                // Nhờ chữ ký này BE biết token có bị sửa hay ko 
                .compact();
                // hàm này báo hoàn tất việc tạo JWT và chuyển nó thành chuỗi String
                // Kết quả cuối là String : xxxxx.yyyyy.zzzz header.payload.signture 
                // thì signature là chữ ký bảo vệ token
        // Tổng thể các đống này là cách tạo JWT theo kiểu method chainning 
        // Ở trên nó đang tạo buider:
        //  + gắn subject
        //  + gắn claim userid
        //  + gắn claim role
        //  + gắn ngày tạo 
        //  + gắn ngày hết hạn 
        //  + ký token
        //  + xuất ra chuỗi token
    }

    @Override
    public boolean validateToken(String token) { // đây là hàm kiểm tra token có hợp lệ hay ko
        try {
            extractAllClaims(token); 
            return true;
        } catch (JwtException | IllegalArgumentException ex) { // dấu "|" là cú pháp để multi catch trong java"
        // nghĩa là nếu là lỗi dạng JwtException hoặc IllegalArgumentException thì đều chạy block catch này
        // IllegalArgumentException là excpetion có sẵn trong java
        // nó xảy ra khi tham số triueenf vào là ko hợp lệ , token = null 
        // token = ""
        // token sai dữ liệu

        // Vì sao token lại dùng try catch mà ko quăng lên cho GlobalExceptionHandler 
        // Vì token đc validate trong JWT Filter chủ yếu xảy ra trước khi vào controller , nếu token sai thì đây ko phải lỗi bình thường mà đây là lỗi xác thực( 401 Unauthorized)
        // và GlobalExceptionHandler chủ yếu bắt lỗi ở tầng Controller trở xuống
            return false;
        }
        // ý nghĩa cả đoạn như sau , nó sẽ kiểm tả token có hợp lệ hay ko bằng gọi hàm extractAllClaims(token);
        // Nếu token :
        //  + đúng format
        //  + đúng chữ ký
        //  + chưa hết hạn 
        //  + parse được
        //      => thì nó sẽ trả về true
        // nếu token :
        //  + sai format
        //  + sai chữ ký
        //  + bị sửa
        //  + hết hạn
        //  + rỗng/null
        // thì parse sẽ ném lỗi và catch sẽ bắt và trả về false
    // JwtException đến từ thư viện JJWT nó đại diện cho các lỗi liên quan JWT ví dụ:
    //  + Token định dạng sai 
    //  + Token sai chữ ký
    //  + Token hết hạn 
    //  + Token không parse được
    // nhiều exception cụ thể của JWT là con của JwtException

    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
        // hàm extracAllClaims(token) là chuyển token ra thành dạng mà java có thể hiểu đc
        // sau đó có hàm getSubject để lấy ra name trong claims vì lúc đầu khi tạo token chúng ta để name là subject
        // cuối cùng là trả về String name
        // Bản thân của hàm này là trả về String
    }

    @Override
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
        // lấy Claim tên "role" trong token và ép kiểu nó thành String
        // hàm get ở đây nó đang trả về Object nghĩa là mọi kiểu dữ liệu đều nằm trong đó , chúng ta cần ép nó về kiểu String để trả về
    }

    @Override
    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId"); 
        // Claim hoạt động giống như map nó chứa dữ liệu dnagj key-value
        // Thì khi hàm extractAllClaims(token) thì nó sẽ thành dữ liệu mà java có thể hiểu là :
        // Ví dụ:
        //  {
        //      "sub": "staff01",
        //      "userId": 1,
        //      "role": "STAFF"
        //  }
        //
        // khi này chúng ta sẽ lấy dữ liệu từ từng field ra bằng hàm get() và hàm get sẽ nó ko biết dữ liệu đang kiểu j
        //  nên nó sẽ trả về dữ liệu kiểu Object nên ta thấy :
        // Object userID = extractAllClaims(token).get(userId);

        if (userId instanceof Number number) { 
            // number là class cha của các kiểu số wrapper trong java(Integer , Long , Double , Float , BigDecimal)
            // Nói đơn giản nếu userId là 1 kiểu dữ liệu số nào đó thì ta xử lý nó như Number
            return number.longValue(); // chuyển sang kiểu long
        }

        return Long.valueOf(userId.toString()); 
        // đây là trường hợp dự phòng nếu userId ko phải là kiểu số mà là chuỗi 
        // Thì nó sẽ chuyển thành String , xong sau đó chuyển thành Long
    }

    private Claims extractAllClaims(String token) { 
        // hàm này dùng để :
        //  + Đọc token 
        //  + kiểm tra chữ ký
        //  + Kiểm tra tính hợp lệ của token
        //  + Lấy payload/claims bên trong token
        return Jwts.parser() // Tạo 1 parser để chuẩn bị đọc token
                .verifyWith(secretKey) // parser này dùng secretKey để kiểm tra chữ ký token
                .build() // đây là tạo parser hoàn chỉnh
                .parseSignedClaims(token) // bắt đầu đọc token , kiểm tra token , rồi parser ra claims
                .getPayload(); // lấy phần payload bên trong token
                // => kiểu dữ liệu của payload của thk token là Claims
    }
    // Claims là kiểu dữ liệu của thư viện JJWT
    // nó biểu diễn phần payload của JWT , nó gần giống MAP 
    // Những có thêm các hàm tiện ích
    //  + getSubject(); lấy tiêu đề
    //  + getExpiration(); lấy thời gian kết thúc
    //  + getIssueAt(); lấy thời gin tạo Token
    //  + get("role"); lấy field
}
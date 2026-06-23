package com.example.parking.shared.exception;

public class ResourceNotFoundException extends RuntimeException {
    // cái RuntimeException này là 1 class có sẵn trong java , cần có nó vè java chỉ cho throw những object trong nhóm throwable
    // và tất nhiên nếu ko cso nó thì chúng ta ko thể throw lỗi đc

    public ResourceNotFoundException(String message) { // hàm này để làm j
        super(message);// super() này là j , nó từ đâu ra , tại sao phải dùng nó , ko có nó thì sao , trường hợp mới dùng nó
    }
}
// class này dùng để tạo lỗi riêng của hệ thống khi ko tìm thấy dữ liệu ví dụ :
// Thay vì viết lỗi chung chung như:
//      Không tìm thấy user
//      Không tìm thấy xe
//      Không tìm thấy slot đỗ
//      Không tìm thấy parking session
// mình tạo lỗi có tên rõ ràng:
//  throw new ResourceNotFoundException("Không tìm thấy xe với biển số: 59A-12345");
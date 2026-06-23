package com.example.parking.auth.application.port.out;

public interface IPasswordEncoderPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
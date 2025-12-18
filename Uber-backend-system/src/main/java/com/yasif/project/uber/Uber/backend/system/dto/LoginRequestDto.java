package com.yasif.project.uber.Uber.backend.system.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginRequestDto {

    private String email;
    private String password;

}

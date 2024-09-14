package com.paulohsmelo.crmservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserRoleDTO {

    @Pattern(regexp = "admin|user", message = "Invalid role value, options: admin | user")
    private String role;
}

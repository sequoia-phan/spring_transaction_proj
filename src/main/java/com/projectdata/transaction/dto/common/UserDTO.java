package com.projectdata.transaction.dto.common;

import com.projectdata.transaction.model.UserRole;
import com.projectdata.transaction.model.UserStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String userName;
    private String email;
    private UserRole role;
    private UserStatus status;
    // Note: no passwordHash for security
}

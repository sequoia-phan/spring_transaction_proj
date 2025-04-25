package com.projectdata.transaction.dto.response;

import com.projectdata.transaction.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private Long id;
    private String userName;
    private String email;
    private UserRole role;
}

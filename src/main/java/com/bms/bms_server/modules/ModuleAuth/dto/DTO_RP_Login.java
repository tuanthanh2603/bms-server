package com.bms.bms_server.modules.ModuleAuth.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DTO_RP_Login {
    String token;
    String fullName;
    String companyName;
    Long employeeId;
    Long companyId;
    Boolean authenticated;
}

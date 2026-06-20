package ma.org.ormt.modules.dashboard.tbd.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbdAssignationDto {

    private String cibleType;
    private Long cibleId;
    private String cibleNom;
}

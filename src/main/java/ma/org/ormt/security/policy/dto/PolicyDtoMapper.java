package ma.org.ormt.security.policy.dto;

import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;

@Mapper()
public interface PolicyDtoMapper extends BaseDtoMapper<PolicyRepresentation, PolicyDto> {

}
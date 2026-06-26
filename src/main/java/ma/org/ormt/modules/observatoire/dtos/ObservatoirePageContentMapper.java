package ma.org.ormt.modules.observatoire.dtos;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.observatoire.dtos.request.ObservatoirePageContentRequestDto;
import ma.org.ormt.modules.observatoire.models.ObservatoirePageContent;

@Component
@RequiredArgsConstructor
public class ObservatoirePageContentMapper {

    private final ObjectMapper objectMapper;

    public ObservatoirePageContentDto mapToDto(ObservatoirePageContent entity) {
        return ObservatoirePageContentDto.builder()
                .id(entity.getId())
                .kicker(entity.getKicker())
                .badgeTitle(entity.getBadgeTitle())
                .badgeSubtitle(entity.getBadgeSubtitle())
                .heroTagline(entity.getHeroTagline())
                .introText(entity.getIntroText())
                .visionText(entity.getVisionText())
                .missionText(entity.getMissionText())
                .partnershipText(entity.getPartnershipText())
                .objectives(readList(entity.getObjectivesJson(), new TypeReference<List<String>>() {
                }))
                .pillars(readList(entity.getPillarsJson(), new TypeReference<List<ObservatoirePillarDto>>() {
                }))
                .team(readList(entity.getTeamJson(), new TypeReference<List<ObservatoireTeamMemberDto>>() {
                }))
                .actions(readList(entity.getActionsJson(), new TypeReference<List<ObservatoireActionCardDto>>() {
                }))
                .actif(entity.isActif())
                .published(entity.isPublished())
                .build();
    }

    public ObservatoirePageContent mapToEntity(ObservatoirePageContentRequestDto requestDto) {
        ObservatoirePageContent entity = ObservatoirePageContent.builder().build();
        updateEntity(entity, requestDto);
        return entity;
    }

    public void updateEntity(ObservatoirePageContent entity, ObservatoirePageContentRequestDto requestDto) {
        entity.setKicker(requestDto.getKicker());
        entity.setBadgeTitle(requestDto.getBadgeTitle());
        entity.setBadgeSubtitle(requestDto.getBadgeSubtitle());
        entity.setHeroTagline(requestDto.getHeroTagline());
        entity.setIntroText(requestDto.getIntroText());
        entity.setVisionText(requestDto.getVisionText());
        entity.setMissionText(requestDto.getMissionText());
        entity.setPartnershipText(requestDto.getPartnershipText());
        entity.setObjectivesJson(writeJson(requestDto.getObjectives()));
        entity.setPillarsJson(writeJson(requestDto.getPillars()));
        entity.setTeamJson(writeJson(requestDto.getTeam()));
        entity.setActionsJson(writeJson(requestDto.getActions()));
        entity.setActif(Boolean.TRUE.equals(requestDto.getActif()));
        entity.setPublished(Boolean.TRUE.equals(requestDto.getPublished()));
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Erreur lors de la serialisation du contenu Observatoire", exception);
        }
    }

    private <T> List<T> readList(String json, TypeReference<List<T>> typeReference) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Erreur lors de la lecture du contenu Observatoire", exception);
        }
    }
}
package org.example.hacaton.mapper;


import org.example.hacaton.dto.response.HackathonListResponse;
import org.example.hacaton.dto.response.HackathonResponse;
import org.example.hacaton.model.hackathon.Hackathon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HackathonMapper {

    @Mapping(source = "title", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "maxTeams", target = "maxTeams")
    HackathonListResponse toHackathonListResponse(Hackathon hackathon);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "fullDescription", target = "fullDescription")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "manager.user.firstname", target = "managerName")
    @Mapping(source = "teamSize", target = "teamSize")
    @Mapping(source = "maxTeams", target = "maxTeams")
    HackathonResponse toHackathonResponse(Hackathon hackathon);
}
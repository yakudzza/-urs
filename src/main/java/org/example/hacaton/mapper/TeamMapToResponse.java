package org.example.hacaton.mapper;


import org.example.hacaton.dto.request.TeamResponse;
import org.example.hacaton.dto.response.TeamCreateResponse;
import org.example.hacaton.dto.response.TeamStatusResponse;
import org.example.hacaton.model.hackathon.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.example.hacaton.model.user.User;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TeamMapToResponse {

    @Mapping(source = "hackathon.title", target = "hackathonTitle")
    @Mapping(source = "members", target = "members", qualifiedByName = "mapMembers")
    TeamCreateResponse toTeamCreateResponse(Team team);

    @Named("mapMembers")
    default Set<String> mapMembers(Set<User> users) {
        return users.stream()
                .map(User::getFirstname)
                .collect(Collectors.toSet());

    }

    @Mapping(target = "membersCount", expression = "java(team.getMembers().size())")
    @Mapping(target = "status", expression = "java(team.getStatus().name())")
    TeamStatusResponse toTeamStatusResponse(Team team);

    TeamResponse toTeamResponse(Team team);
}
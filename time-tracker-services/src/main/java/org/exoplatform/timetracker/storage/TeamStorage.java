/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.timetracker.storage;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.*;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.timetracker.dto.Team;
import org.exoplatform.timetracker.dto.TeamMember;
import org.exoplatform.timetracker.service.TeamService;
import org.gatein.api.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Storage service to access / load and save Teams. This service will be
 * used , as well, to convert from JPA entity to DTO.
 */
public class TeamStorage {

  private final String PARENT_GROUP = "/organization/teams";
  private final IdentityManager identityManager;
  private final OrganizationService organizationService;
  private final GroupHandler groupHandler;
  private final MembershipHandler membershipHandler;
  private static Boolean requestStarted = false;
  private static final Log log = ExoLogger.getLogger(TeamStorage.class);

  public TeamStorage(OrganizationService organizationService, IdentityManager identityManager) {
    this.organizationService = organizationService;
    this.groupHandler = organizationService.getGroupHandler();
    this.membershipHandler = organizationService.getMembershipHandler();
    this.identityManager = identityManager;
  }


  /////////////////////////Team storage /////////////////////////////////////////

  public Team createTeam(Team team) throws Exception {
    if (team == null) {
      throw new IllegalArgumentException("Team is mandatory");
    }
    try {
    startRequest();
      Group groupParent = groupHandler.findGroupById(PARENT_GROUP);
    Group group = groupHandler.createGroupInstance();
    group.setId(team.getName().replaceAll(" ","_"));
    group.setLabel(team.getName());
    group.setGroupName(team.getName());
    group.setDescription(team.getDescription());
    group.setParentId(PARENT_GROUP);
    groupHandler.addChild(groupParent, group, true);
      endRequest();
      return toDTO(group);
    } catch (Exception e) {
      //todo
    } finally {
      endRequest();
    }

    return null;
  }

  public Team updateTeam(Team team) {
    if (team == null) {
      throw new IllegalArgumentException("Team is mandatory");
    }
    String teamId = team.getId();
    Group group = null;
    try {
      group = groupHandler.findGroupById(teamId);
      if (group == null) {
        throw new EntityNotFoundException("Group with id " + teamId + " wasn't found");
      }
      startRequest();
      group.setLabel(team.getName());
      group.setGroupName(team.getName());
      group.setDescription(team.getDescription());
      groupHandler.saveGroup(group,true);
      endRequest();
    } catch (Exception e) {
      //todo
    } finally {
      endRequest();
    }

    return toDTO(group);
  }

  public void deleteTeam(String teamId) throws Exception {
    try {
      startRequest();

    Group group = groupHandler.findGroupById(teamId);
    if (group == null) {
      throw new EntityNotFoundException("froup with id " + teamId + " not found");
    }

    groupHandler.removeGroup(group,true);
      endRequest();
  } catch (Exception e) {
    //todo
  } finally {
    endRequest();
  }
  }

  public Team getTeamById(String teamId)  throws Exception {
    Group group = groupHandler.findGroupById(teamId);
    return toDTO(group);
  }

  public List<Team> getTeams()  throws Exception {
    Group parentGroup = groupHandler.findGroupById(PARENT_GROUP);
    return groupHandler.findGroups(parentGroup).stream().map(this::toDTO).collect(Collectors.toList());
  }

  public List<Team> getTeamsByUser(String userName) throws Exception {

    return groupHandler.findGroupsOfUser(userName).stream().map(this::toDTO).collect(Collectors.toList());
  }


  public List<TeamMember> getMembersByTeam(String teamId)  throws Exception {
    try {
      Group group = groupHandler.findGroupById(teamId);
      List<Membership> memberships = Arrays.asList(membershipHandler.findAllMembershipsByGroup(group).load(0, -1));
      return memberships.stream().map(teamMemberEntity -> {
        try {
          return toDTO(teamMemberEntity);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }).collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  public Team toDTO(Group group) {
    try {
      if (group == null) {
        return null;
      }
      return new Team(group.getId(),
              group.getLabel(),
              group.getDescription());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }



  public List<Team> toDtos(List<Group> teams){
    return teams.stream().map(this::toDTO).collect(Collectors.toList());
  }

  public List<Team> toDtos_(List<String> teams){
    return teams.stream().map(this::getDtoByGroupId).collect(Collectors.toList());
  }

  public Team getDtoByGroupId(String groupId){
    try {
      Group group = groupHandler.findGroupById(groupId);
      return toDTO(group);
    } catch (Exception e) {
      //Todo
    }
    return null;
  }

/////////////////////////Team Fields storage /////////////////////////////////////////

  public void createTeamMember(TeamMember teamMember) throws Exception {
    if (teamMember == null) {
      throw new IllegalArgumentException("TeamMember is mandatory");
    }
    try {
      startRequest();
    Group group = groupHandler.findGroupById(teamMember.getTeam().getId());
    User user = organizationService.getUserHandler().findUserByName(teamMember.getUserName());
    MembershipType membershipType = organizationService.getMembershipTypeHandler().findMembershipType("member");
    membershipHandler.linkMembership(user,group,membershipType,true);
      endRequest();
  } catch (Exception e) {
        //todo
        } finally {
        endRequest();
        }
  }


  public void deleteTeamMember(String teamMemberId) throws Exception {
    try {
      startRequest();
    membershipHandler.removeMembership(teamMemberId,true);
      endRequest();
        } catch (Exception e) {
        //todo
        } finally {
        endRequest();
        }
  }

  public void deleteAllTeamMembersByTeam(String teamId) throws Exception {
    try {
      startRequest();
    Group group = groupHandler.findGroupById(teamId);
    for(Membership membership : membershipHandler.findAllMembershipsByGroup(group).load(0,-1)){
      membershipHandler.removeMembership(membership.getId(),true);
    }
      endRequest();
        } catch (Exception e) {
        //todo
        } finally {
        endRequest();
        }
  }


  public TeamMember getTeamMemberById(String teamMemberId) throws Exception {
    return toDTO(membershipHandler.findMembership(teamMemberId));

  }


  public TeamMember  getMemberByTeamUserAndRole(String teamId, String userName, String role) throws Exception {
    return toDTO(membershipHandler.findMembershipByUserGroupAndType(userName,teamId,role));
  }


  public TeamMember toDTO(Membership teamMemberEntity) throws Exception {
    if (teamMemberEntity == null) {
      return null;
    }
    TeamMember teamMember = new TeamMember(teamMemberEntity.getId(),
            teamMemberEntity.getUserName(),
            teamMemberEntity.getMembershipType(),
            toDTO(groupHandler.findGroupById(teamMemberEntity.getGroupId())));
    Identity identity =identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,teamMember.getUserName());
    if(identity != null) {
      teamMember.setFullName(identity.getProfile().getFullName());
    }
    return teamMember;
  }


  private void endRequest() {
    if (requestStarted && organizationService instanceof ComponentRequestLifecycle) {
      try {
        ((ComponentRequestLifecycle) organizationService).endRequest(PortalContainer.getInstance());
      } catch (Exception e) {
        log.warn(e.getMessage(), e);
      }
      requestStarted = false;
    }
  }

  private void startRequest() {
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).startRequest(PortalContainer.getInstance());
      requestStarted = true;
    }
  }

}

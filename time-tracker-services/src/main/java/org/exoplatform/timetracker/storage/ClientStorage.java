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

import java.util.List;
import java.util.stream.Collectors;

import org.gatein.api.EntityNotFoundException;

import org.exoplatform.timetracker.dao.ClientDAO;
import org.exoplatform.timetracker.dto.Client;
import org.exoplatform.timetracker.entity.ClientEntity;

/**
 * Storage service to access / load and save Clients. This service will be
 * used , as well, to convert from JPA entity to DTO.
 */
public class ClientStorage {

  private final ClientDAO clientDAO;

  public ClientStorage(ClientDAO clientDAO) {
    this.clientDAO = clientDAO;
  }

  public Client createClient(Client client) throws Exception {
    if (client == null) {
      throw new IllegalArgumentException("Client is mandatory");
    }
    ClientEntity clientEntity = toEntity(client);
    client.setId(null);
    clientEntity = clientDAO.create(clientEntity);
    return toDTO(clientEntity);
  }

  public Client updateClient(Client client) throws Exception {
    if (client == null) {
      throw new IllegalArgumentException("Client is mandatory");
    }
    Long clientId = client.getId();
    ClientEntity clientEntity = clientDAO.find(client.getId());
    if (clientEntity == null) {
      throw new EntityNotFoundException("Client with id " + clientId + " wasn't found");
    }

    clientEntity = toEntity(client);
    clientEntity = clientDAO.update(clientEntity);

    return toDTO(clientEntity);
  }

  public void deleteClient(long clientId) throws EntityNotFoundException {
    if (clientId <= 0) {
      throw new IllegalArgumentException("ClientId must be a positive integer");
    }
    ClientEntity clientEntity = clientDAO.find(clientId);
    if (clientEntity == null) {
      throw new EntityNotFoundException("Client with id " + clientId + " not found");
    }
    clientDAO.delete(clientEntity);
  }

  public Client getClientById(long ClientId) {
    if (ClientId <= 0) {
      throw new IllegalArgumentException("ClientId must be a positive integer");
    }
    ClientEntity ClientEntity = clientDAO.find(ClientId);
    return toDTO(ClientEntity);
  }

  public List<Client> getClients() {
    List<ClientEntity> applicatiions = clientDAO.findAll();
    return applicatiions.stream().map(this::toDTO).collect(Collectors.toList());
  }

  public long countClients() {
    return clientDAO.count();
  }

  public Client toDTO(ClientEntity clientEntity) {
    if (clientEntity == null) {
      return null;
    }
    return new Client(clientEntity.getId(),
                        clientEntity.getCode(),
                        clientEntity.getLabel());
  }

  public ClientEntity toEntity(Client client) {
    if (client == null) {
      return null;
    }
    return new ClientEntity(client.getId(),
                              client.getCode(),
                              client.getLabel());
  }

}
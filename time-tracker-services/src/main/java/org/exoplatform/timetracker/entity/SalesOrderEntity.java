/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds AsSALES_ORDERciation
 * contact@meeds.io
 * This program is free SALES_ORDERftware; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free SALES_ORDERftware Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free SALES_ORDERftware Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.timetracker.entity;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

/**
 * <p>SalesOrderEntity class.</p>
 *
 * @author Krout MedAmine
 * @version $Id: $Id
 */
@Entity(name = "SalesOrderEntity")
@ExoEntity
@Table(name = "ADDONS_TT_SALES_ORDER")
@Data
@NamedQueries({})
public class SalesOrderEntity {

  @Id
  @SequenceGenerator(name = "SEQ_SALES_ORDER_ID", sequenceName = "SEQ_SALES_ORDER_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_SALES_ORDER_ID")
  @Column(name = "ID")
  private Long         id;

  @Column(name = "NAME")
  private String       name;

  @Column(name = "DESCRIPTION")
  private String       description;

  @ManyToOne
  @JoinColumn(name = "CLIENT_ID")
  private ClientEntity clientEntity;

  /**
   * <p>Constructor for SalesOrderEntity.</p>
   */
  public SalesOrderEntity() {
  }

  /**
   * <p>Constructor for SalesOrderEntity.</p>
   *
   * @param id a {@link java.lang.Long} object.
   * @param name a {@link java.lang.String} object.
   * @param description a {@link java.lang.String} object.
   * @param clientEntity a {@link org.exoplatform.timetracker.entity.ClientEntity} object.
   */
  public SalesOrderEntity(Long id, String name, String description, ClientEntity clientEntity) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.clientEntity = clientEntity;
  }

}

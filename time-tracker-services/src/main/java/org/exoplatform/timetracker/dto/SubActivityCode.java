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
package org.exoplatform.timetracker.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>SubActivityCode class.</p>
 *
 * @author Krout MedAmine
 * @version $Id: $Id
 */

@Data
public class SubActivityCode implements Serializable {

  private Long         id;

  private String       code;

  private String       label;

  private ActivityCode activityCode;

  /**
   * <p>Constructor for SubActivityCode.</p>
   */
  public SubActivityCode() {
  }

  /**
   * <p>Constructor for SubActivityCode.</p>
   *
   * @param id a {@link java.lang.Long} object.
   * @param code a {@link java.lang.String} object.
   * @param label a {@link java.lang.String} object.
   * @param activityCode a {@link org.exoplatform.timetracker.dto.ActivityCode} object.
   */
  public SubActivityCode(Long id, String code, String label, ActivityCode activityCode) {
    this.id = id;
    this.code = code;
    this.label = label;
    this.activityCode = activityCode;
  }

}

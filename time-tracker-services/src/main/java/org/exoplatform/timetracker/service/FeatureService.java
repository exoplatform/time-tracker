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
package org.exoplatform.timetracker.service;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.timetracker.dto.Feature;
import org.exoplatform.timetracker.storage.FeatureStorage;

/**
 * A Service to access and store Activities
 */
public class FeatureService {

  private static final Log      LOG = ExoLogger.getLogger(FeatureService.class);

  private final FeatureStorage featureStorage;

  public FeatureService(FeatureStorage featureStorage) {
    this.featureStorage = featureStorage;

  }

  /**
   * Create new Feature that will be available for all users. If the Feature
   * already exits an {@link EntityExistsException} will be thrown.
   * 
   * @param feature Feature to create
   * @return stored {@link Feature} in datasource
   * @throws Exception when Feature already exists or an error occurs while
   *           creating Feature or its attached image
   */
  public Feature createFeature(Feature feature) throws Exception {
    if (feature == null) {
      throw new IllegalArgumentException("Feature is mandatory");
    }
/*    Feature existingFeature = featureStorage.getFeatureById(feature.getId());
    if (existingFeature != null) {
      throw new EntityExistsException("An Feature with same code exists");

    }*/
    return featureStorage.createFeature(feature);
  }

  /**
   * Update an existing Feature on datasource. If the Feature doesn't exit an
   * {@link EntityNotFoundException} will be thrown.
   * 
   * @param Feature dto to update on store
   * @param username username storing Feature
   * @return stored {@link Feature} in datasource
   * @throws Exception when {@link EntityNotFoundException} is thrown or an error
   *           occurs while saving Feature
   */
  public Feature updateFeature(Feature Feature, String username) throws Exception {
    if (Feature == null) {
      throw new IllegalArgumentException("Feature is mandatory");
    }
    if (StringUtils.isBlank(username)) {
      throw new IllegalArgumentException("username is mandatory");
    }
    Long FeatureId = Feature.getId();
    if (FeatureId == null) {
      throw new EntityNotFoundException("Feature with null id wasn't found");
    }
    Feature storedFeature = featureStorage.getFeatureById(FeatureId);
    if (storedFeature == null) {
      throw new EntityNotFoundException("Feature with id " + FeatureId + " wasn't found");
    }
    return featureStorage.updateFeature(Feature);
  }

  /**
   * Delete Feature identified by its id and check if username has permission to
   * delete it.
   * 
   * @param featureId technical identifier of Feature
   * @param username user currently deleting Feature
   * @throws EntityNotFoundException if Feature wasn't found
   * @throws IllegalAccessException if user is not allowed to delete Feature
   */
  public void deleteFeature(Long featureId, String username) throws EntityNotFoundException, IllegalAccessException {
    if (featureId == null || featureId <= 0) {
      throw new IllegalArgumentException("FeatureId must be a positive integer");
    }
    if (StringUtils.isBlank(username)) {
      throw new IllegalArgumentException("username is mandatory");
    }

    Feature storedFeature = featureStorage.getFeatureById(featureId);
    if (storedFeature == null) {
      throw new EntityNotFoundException("Feature with id " + featureId + " not found");
    }
    featureStorage.deleteFeature(featureId);
  }

  /**
   * Retrieves the list of Activities with offset, limit and a keyword that can be
   * empty
   *
   * @return List of {@link Feature} that contains the list of Activities
   */
  public List<Feature> getFeaturesList() {
    return featureStorage.getFeatures();
  }

}
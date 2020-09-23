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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.timetracker.dto.Filter;
import org.exoplatform.timetracker.dto.FilterField;
import org.exoplatform.timetracker.dto.FilterModel;
import org.exoplatform.timetracker.storage.FilterStorage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Service to access and store Activities
 */
public class FilterService {

    private static final Log LOG = ExoLogger.getLogger(FilterService.class);

    private final FilterStorage filterStorage;

    public FilterService(FilterStorage filterStorage) {
        this.filterStorage = filterStorage;

    }

    /**
     * Create new Filter that will be available for all users. If the Filter
     * already exits an {@link EntityExistsException} will be thrown.
     *
     * @param filter Filter to create
     * @return stored {@link Filter} in datasource
     * @throws Exception when Filter already exists or an error occurs while
     *                   creating Filter or its attached image
     */
    public FilterModel createFilter(FilterModel filter, String userName) throws Exception {
        if (filter == null) {
            throw new IllegalArgumentException("Filter is mandatory");
        }
        List<FilterField> fields = new ArrayList<>();
        filter.getFilter().setUserName(userName);
        Filter filter_ = filterStorage.createFilter(filter.getFilter());
        for (FilterField filterField : filter.getFilterFields()) {
            filterField.setFilter(filter_);
            fields.add(filterStorage.createFilterField(filterField));
        }
        return new FilterModel(filter_, getFields(filter_.getId()));
    }


    /**
     * Delete Filter identified by its id and check if username has permission to
     * delete it.
     *
     * @param filterId technical identifier of Filter
     * @throws EntityNotFoundException if Filter wasn't found
     * @throws IllegalAccessException  if user is not allowed to delete Filter
     */
    public void deleteFilter(Long filterId) throws EntityNotFoundException, IllegalAccessException {
        if (filterId == null || filterId <= 0) {
            throw new IllegalArgumentException("FilterId must be a positive integer");
        }

        Filter storedFilter = filterStorage.getFilterById(filterId);
        if (storedFilter == null) {
            throw new EntityNotFoundException("Filter with id " + filterId + " not found");
        }
        filterStorage.deleteAllFilterFieldsByFilter(filterId);
        filterStorage.deleteFilter(filterId);
    }

    /**
     * Retrieves the list of Activities with offset, limit and a keyword that can be
     * empty
     *
     * @return List of {@link FilterModel} that contains the list of Activities
     */
    public JSONArray getFiltersList(String userName) {
        JSONArray filterModels = new JSONArray();
        try {
            List<Filter> filters = filterStorage.getFiltersByUserName(userName);
            for (Filter filter : filters) {
                JSONObject filterObj = new JSONObject();
                JSONObject filterDetails = new JSONObject();
                filterDetails.put("name",filter.getName());
                filterDetails.put("id",filter.getId());
                filterDetails.put("userName",filter.getUserName());
                filterObj.put("filter",filterDetails);
                filterObj.put("fields",getFields(filter.getId()));
                filterModels.put(filterObj);
            }
        } catch (JSONException e) {
            LOG.error("Cannot get filters for user {}", userName, e);
        }
        return filterModels;
    }


    public JSONObject getFields(long filterId) {
        List<FilterField> fieldsList = filterStorage.getFilterFieldsByFilter(filterId);
        JSONObject fieldsJson = new JSONObject();

        for (FilterField filterField : fieldsList) {
            try {
                fieldsJson.put(filterField.getName(), filterField.getValue());
            } catch (Exception e) {
                LOG.error("Cannot get fildes for filter {}", filterId, e);
            }

        }
        return fieldsJson;
    }

}
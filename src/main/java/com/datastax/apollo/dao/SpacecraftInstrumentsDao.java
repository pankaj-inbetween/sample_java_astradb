package com.datastax.apollo.dao;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import com.datastax.apollo.entity.SpacecraftLocationOverTime;
import com.datastax.apollo.entity.SpacecraftPressureOverTime;
import com.datastax.apollo.entity.SpacecraftSpeedOverTime;
import com.datastax.apollo.entity.SpacecraftTemperatureOverTime;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;

/**
 * Operation to work with instruments
 */
@Dao
public interface SpacecraftInstrumentsDao {
    
    /**
     * Search for temperature readings.
     * TODO : We could replace the query provider here
     */
    @Select(customWhereClause = "spacecraft_name= :spacecraftName AND journey_id= :journeyId")
    PagingIterable<SpacecraftTemperatureOverTime> getTemperatureReading(
            String spacecraftName, UUID JourneyId,
            Function<BoundStatementBuilder, BoundStatementBuilder> setAttributes);
    
    /**
     * Search for temperature readings.
     */
    @QueryProvider(providerClass = SpacecraftInstrumentsQueryProvider.class, 
       entityHelpers = { SpacecraftTemperatureOverTime.class, SpacecraftPressureOverTime.class, 
                         SpacecraftLocationOverTime.class, SpacecraftSpeedOverTime.class})
    PagingIterable<SpacecraftTemperatureOverTime> getTemperatureReading(
            String spacecraftName, UUID JourneyId, Optional<Integer> pageSize, Optional<String> pagingState);

    /**
     * Upsert a temperature reading.
     *
     * @param reading
     *      The temperature reading
     * @return
     *      if statement was applied
     */
    @Insert
    boolean upsertTemperature(SpacecraftTemperatureOverTime reading);

    /**
     * Upsert a location reading.
     *
     * @param reading
     *      The location reading
     * @return
     *      if statement was applied
     */
    @Insert
    boolean upsertLocation(SpacecraftLocationOverTime reading);

    /**
     * Upsert a pressure reading.
     *
     * @param reading
     *      The pressure reading
     * @return
     *      if statement was applied
     */
    @Insert
    boolean upsertPressure(SpacecraftPressureOverTime reading);

    /**
     * Upsert a speed reading.
     *
     * @param reading
     *      The speed reading
     * @return
     *      if statement was applied
     */
    @Insert
    boolean upsertSpeed(SpacecraftSpeedOverTime reading);

    /**
     * Search for pressure readings.
     */
    @QueryProvider(providerClass = SpacecraftInstrumentsQueryProvider.class, 
       entityHelpers = { SpacecraftTemperatureOverTime.class, SpacecraftPressureOverTime.class, 
                         SpacecraftLocationOverTime.class, SpacecraftSpeedOverTime.class})
    PagingIterable<SpacecraftPressureOverTime> getPressureReading(
            String spacecraftName, UUID JourneyId, Optional<Integer> pageSize, Optional<String> pagingState);
    
    /**
     * Search for speed readings.
     */
    @QueryProvider(providerClass = SpacecraftInstrumentsQueryProvider.class, 
       entityHelpers = { SpacecraftTemperatureOverTime.class, SpacecraftPressureOverTime.class, 
                         SpacecraftLocationOverTime.class, SpacecraftSpeedOverTime.class})
    PagingIterable<SpacecraftSpeedOverTime> getSpeedReading(
            String spacecraftName, UUID JourneyId, Optional<Integer> pageSize, Optional<String> spagingState);
    
    /**
     * Search for location readings.
     */
    @QueryProvider(providerClass = SpacecraftInstrumentsQueryProvider.class, 
       entityHelpers = { SpacecraftTemperatureOverTime.class, SpacecraftPressureOverTime.class, 
                         SpacecraftLocationOverTime.class, SpacecraftSpeedOverTime.class})
    PagingIterable<SpacecraftLocationOverTime> getLocationReading(
            String spacecraftName, UUID JourneyId, Optional<Integer> pageSize, Optional<String> pagingState);
    
    /**
     * Insert instruments.
     */
    @QueryProvider(providerClass = SpacecraftInstrumentsQueryProvider.class, 
            entityHelpers = { SpacecraftTemperatureOverTime.class, SpacecraftPressureOverTime.class, 
                              SpacecraftLocationOverTime.class, SpacecraftSpeedOverTime.class})
    void insertInstruments(
            SpacecraftTemperatureOverTime temperature, SpacecraftPressureOverTime pressure,  
            SpacecraftSpeedOverTime speed, SpacecraftLocationOverTime location);
}

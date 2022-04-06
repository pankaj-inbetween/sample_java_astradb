package com.datastax.apollo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.datastax.apollo.dao.SessionManager;
import com.datastax.apollo.dao.SpacecraftInstrumentsDao;
import com.datastax.apollo.dao.SpacecraftJourneyDao;
import com.datastax.apollo.dao.SpacecraftMapper;
import com.datastax.apollo.dao.SpacecraftMapperBuilder;
import com.datastax.apollo.entity.LocationUdt;
import com.datastax.apollo.entity.SpacecraftJourneyCatalog;
import com.datastax.apollo.entity.SpacecraftLocationOverTime;
import com.datastax.apollo.entity.SpacecraftPressureOverTime;
import com.datastax.apollo.entity.SpacecraftSpeedOverTime;
import com.datastax.apollo.entity.SpacecraftTemperatureOverTime;
import com.datastax.apollo.model.PagedResultWrapper;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.uuid.Uuids;

/**
 * Implementation of Service for controller
 * 
 */
@Component
public class ApolloService {

    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApolloService.class);
   
    /** Driver Daos. */
    private SpacecraftJourneyDao     spacecraftJourneyDao;
    private SpacecraftInstrumentsDao spacecraftInstrumentsDao;
    
    
    /**
     * Insert reading into database.
     *
     * @param itemCount
     *      current item count
     */
    public void preload(int itemCount, String spacecraftName, UUID journeyId) {
        SpacecraftLocationOverTime    loc   = new SpacecraftLocationOverTime();
        loc.setJourney_id(journeyId);
        loc.setSpacecraft_name(spacecraftName);
        loc.setLocation_unit("km");
        SpacecraftTemperatureOverTime temp  = new SpacecraftTemperatureOverTime();
        temp.setSpacecraft_name(spacecraftName);
        temp.setJourney_id(journeyId);
        temp.setTemperature_unit("fahrenheit");
        SpacecraftPressureOverTime    press = new SpacecraftPressureOverTime();
        press.setJourney_id(journeyId);
        press.setSpacecraft_name(spacecraftName);
        press.setPressure_unit("kPa");
        SpacecraftSpeedOverTime       speed = new SpacecraftSpeedOverTime();temp.setJourney_id(journeyId);
        speed.setJourney_id(journeyId);
        speed.setSpacecraft_name(spacecraftName);
        speed.setSpeed_unit("km/h");
        Instant start = Instant.now();
        for (int i = 0; i < itemCount; i++) {
            temp.setReading_time(start);
            press.setReading_time(start);
            speed.setReading_time(start);
            
            LocationUdt udt = new LocationUdt(0,0,0);
            loc.setLocation(udt);
            loc.setReading_time(start);
            temp.setTemperature(Double.valueOf(69.3));
            press.setPressure(Double.valueOf(100.5));
            speed.setSpeed(Double.valueOf(30000));
            getSpaceCraftInstrumentsDao().insertInstruments(temp,press, speed, loc);
            
            // Compute next value
            start = start.plusSeconds(1);
            udt.setX_coordinate(udt.getX_coordinate()+1);
            udt.setY_coordinate(udt.getY_coordinate()+1);
            udt.setZ_coordinate(udt.getZ_coordinate()+5);
            temp.setTemperature(createRandomValue(temp.getTemperature()));
            press.setPressure(createRandomValue(press.getPressure()));
            speed.setSpeed(createRandomValue(speed.getSpeed()));
        }
    }
    
    private double createRandomValue(double lastValue) {
        double up = Math.random() * 2;
        double percentMove = (Math.random() * 1.0) / 100;
        if (up < 1) {
          lastValue -= percentMove * lastValue;
        } else {
          lastValue += percentMove * lastValue;
        }
        return lastValue;
      }
    
    /**
     * Find all spacecrafts in the catalog.
     */
    public List< SpacecraftJourneyCatalog > findAllSpacecrafts() {
        // no paging we don't expect more than 5k journeys
        return getSpaceCraftJourneyDao().findAll().all(); 
    }
    
    /**
     * Find all journeys for a spacecraft.
     * 
     * @param spacecraftName
     *      unique spacecraft name (PK)
     * @return
     *      list of journeys
     */
    public List < SpacecraftJourneyCatalog > findAllJourneysForSpacecraft(String spacecraftName) {
        // no paging we don't expect more than 5k journeys
        return getSpaceCraftJourneyDao().findAllJourneysForSpacecraft(spacecraftName).all();
    }
    
    /**
     * Search by primary key, unique record expect.
     *
     * @param spacecraftName
     *      unique spacecraft name (PK)
     * @param journeyid
     *      journey unique identifier
     * @return
     *      journey details if it exists
     */
    public Optional< SpacecraftJourneyCatalog > findJourneyById(String spacecraftName, UUID journeyId) {
        return getSpaceCraftJourneyDao().findById(spacecraftName, journeyId);
    }
    
    /**
     * Create a new {@link SpacecraftJourneyCatalog}.
     *
     * @param spacecraftName
     *       unique spacecraft name (PK)
     * @param summary
     *       short description
     * @return
     *       generated journey id
     */
    public UUID createSpacecraftJourney(String spacecraftName, String summary) {
        UUID journeyUid = Uuids.timeBased();
        LOGGER.info("Creating journey {} for spacecraft {}", journeyUid, spacecraftName);
        SpacecraftJourneyCatalog dto = new SpacecraftJourneyCatalog();
        dto.setName(spacecraftName);
        dto.setSummary(summary);
        dto.setStart(Instant.now());
        dto.setEnd(Instant.now().plus(1000, ChronoUnit.MINUTES));
        dto.setActive(false);
        dto.setJourneyId(journeyUid);
        getSpaceCraftJourneyDao().upsert(dto);
        return journeyUid;
    }
    
    /**
     * Retrieve temperature readings for a journey.
     *
     * @param spacecraftName
     *      name of spacecrafr
     * @param journeyId
     *      journey identifier
     * @param pageSize
     *      page size
     * @param pageState
     *      page state
     * @return
     *      result page
     */
    public PagedResultWrapper<SpacecraftTemperatureOverTime> getTemperatureReading(
            String spacecraftName, UUID journeyId, 
            Optional<Integer> pageSize, Optional<String> pageState) {
        PagingIterable<SpacecraftTemperatureOverTime> daoResult = 
                getSpaceCraftInstrumentsDao().getTemperatureReading(spacecraftName, journeyId, pageSize, pageState);
        return new PagedResultWrapper<SpacecraftTemperatureOverTime>(daoResult, 
                pageSize.isPresent() ? pageSize.get() : 0);
    }

    /**
     * Create a new {@link SpacecraftTemperatureOverTime} for each item in the array.
     *
     * @param readings
     *       An array unique temperature readings
     * @return
     *       true if successful
     */
    public boolean insertTemperatureReading(SpacecraftTemperatureOverTime[] readings) {
        for (int i=0; i< readings.length; i++) {
            getSpaceCraftInstrumentsDao().upsertTemperature(readings[i]);
        }
        return true;
    }

    /**
     * Create a new {@link SpacecraftLocationOverTime} for each item in the array.
     *
     * @param readings
     *       An array unique location readings
     * @return
     *       true if successful
     */
    public boolean insertLocationReading(SpacecraftLocationOverTime[] readings) {
        for (int i=0; i< readings.length; i++) {
            getSpaceCraftInstrumentsDao().upsertLocation(readings[i]);
        }
        return true;
    }

    /**
     * Create a new {@link SpacecraftPressureOverTime} for each item in the array.
     *
     * @param readings
     *       An array unique pressure readings
     * @return
     *       true if successful
     */
    public boolean insertPressureReading(SpacecraftPressureOverTime[] readings) {
        for (int i=0; i< readings.length; i++) {
            getSpaceCraftInstrumentsDao().upsertPressure(readings[i]);
        }
        return true;
    }

    /**
     * Create a new {@link SpacecraftSpeedOverTime} for each item in the array.
     *
     * @param readings
     *       An array unique pressure readings
     * @return
     *       true if successful
     */
    public boolean insertSpeedReading(SpacecraftSpeedOverTime[] readings) {
        for (int i=0; i< readings.length; i++) {
            getSpaceCraftInstrumentsDao().upsertSpeed(readings[i]);
        }
        return true;
    }

    
    /**
     * Retrieve pressure readings for a journey.
     *
     * @param spacecraftName
     *      name of spacecrafr
     * @param journeyId
     *      journey identifier
     * @param pageSize
     *      page size
     * @param pageState
     *      page state
     * @return
     *      result page
     */
    public PagedResultWrapper<SpacecraftPressureOverTime> getPressureReading(
            String spacecraftName, UUID journeyId, 
            Optional<Integer> pageSize, Optional<String> pageState) {
        PagingIterable<SpacecraftPressureOverTime> daoResult = 
                getSpaceCraftInstrumentsDao().getPressureReading(spacecraftName, journeyId, pageSize, pageState);
        return new PagedResultWrapper<SpacecraftPressureOverTime>(daoResult, 
                pageSize.isPresent() ? pageSize.get() : 0);
    }
    
    /**
     * Retrieve speed readings for a journey.
     *
     * @param spacecraftName
     *      name of spacecrafr
     * @param journeyId
     *      journey identifier
     * @param pageSize
     *      page size
     * @param pageState
     *      page state
     * @return
     *      result page
     */
    public PagedResultWrapper<SpacecraftSpeedOverTime> getSpeedReading(
            String spacecraftName, UUID journeyId, 
            Optional<Integer> pageSize, Optional<String> pageState) {
        PagingIterable<SpacecraftSpeedOverTime> daoResult = 
                getSpaceCraftInstrumentsDao().getSpeedReading(spacecraftName, journeyId, pageSize, pageState);
        return new PagedResultWrapper<SpacecraftSpeedOverTime>(daoResult, 
                pageSize.isPresent() ? pageSize.get() : 0);
    }
    
    /**
     * Retrieve speed readings for a journey.
     *
     * @param spacecraftName
     *      name of spacecrafr
     * @param journeyId
     *      journey identifier
     * @param pageSize
     *      page size
     * @param pageState
     *      page state
     * @return
     *      result page
     */
    public PagedResultWrapper<SpacecraftLocationOverTime> getLocationReading(
            String spacecraftName, UUID journeyId, 
            Optional<Integer> pageSize, Optional<String> pageState) {
        PagingIterable<SpacecraftLocationOverTime> daoResult = 
                getSpaceCraftInstrumentsDao().getLocationReading(spacecraftName, journeyId, pageSize, pageState);
        return new PagedResultWrapper<SpacecraftLocationOverTime>(daoResult, 
                pageSize.isPresent() ? pageSize.get() : 0);
    }
    
    protected synchronized SpacecraftJourneyDao getSpaceCraftJourneyDao() {
        if (spacecraftJourneyDao == null) {
            CqlSession cqlSession   = SessionManager.getInstance().connectToApollo();
            SpacecraftMapper mapper = new SpacecraftMapperBuilder(cqlSession).build();
            this.spacecraftJourneyDao = mapper.spacecraftJourneyDao(cqlSession.getKeyspace().get());
        }
        return spacecraftJourneyDao;
    }
    
    protected synchronized SpacecraftInstrumentsDao getSpaceCraftInstrumentsDao() {
        if (spacecraftInstrumentsDao == null) {
            CqlSession cqlSession   = SessionManager.getInstance().connectToApollo();
            SpacecraftMapper mapper = new SpacecraftMapperBuilder(cqlSession).build();
            this.spacecraftInstrumentsDao = mapper.spacecraftInstrumentsDao(cqlSession.getKeyspace().get());
        }
        return spacecraftInstrumentsDao;
    }
    
    /**
     * Properly close CqlSession
     */
    @PreDestroy
    public void cleanUp() {
        SessionManager.getInstance().close();
    }
    
}

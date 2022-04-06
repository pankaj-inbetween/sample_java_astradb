package com.datastax.apollo.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.datastax.apollo.entity.SpacecraftLocationOverTime;
import com.datastax.apollo.entity.SpacecraftPressureOverTime;
import com.datastax.apollo.entity.SpacecraftSpeedOverTime;
import com.datastax.apollo.entity.SpacecraftTemperatureOverTime;
import com.datastax.apollo.model.PagedResultWrapper;
import com.datastax.apollo.service.ApolloService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@Api(
   value = "api/spacecraft/{spacecraftName}/{journeyId}/instruments", 
   description = "Works with Instruments")
@RequestMapping("api/spacecraft/{spacecraftName}/{journeyId}/instruments")
public class InstrumentsController {
    
    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentsController.class);
    
    /** Service implementation Injection. */
    private ApolloService apolloService;

    /**
     * Constructor.
     *
     * @param spacecraftService
     *      service implementation
     */
    public InstrumentsController(ApolloService apolloService) {
        this.apolloService = apolloService;
    }
    
    /**
     * Retrieve temperature metrics
     */
    @GetMapping(value="/temperature", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Retrieve temperature reading for a journey", response = List.class)
    @ApiResponse(code = 200, message = "Retrieve temperature reading for a journey")
    public ResponseEntity<PagedResultWrapper<SpacecraftTemperatureOverTime>> getTemperatureReading(
            @ApiParam(name="spacecraftName", value="Spacecraft name",example = "gemini3",required=true )
            @PathVariable(value = "spacecraftName") String spacecraftName,
            @ApiParam(name="journeyId", value="Identifer for journey",example = "abb7c000-c310-11ac-8080-808080808080",required=true )
            @PathVariable(value = "journeyId") UUID journeyId, 
            @ApiParam(name="pagesize", value="Requested page size, default is 10", required=false )
            @RequestParam("pagesize") Optional<Integer> pageSize,
            @ApiParam(name="pagestate", value="Use to retrieve next pages", required=false )
            @RequestParam("pagestate") Optional<String> pageState) {
        LOGGER.info("Retrieving temperature readings for spacecraft {} and journey {}", spacecraftName, journeyId);
        PagedResultWrapper<SpacecraftTemperatureOverTime> res = apolloService.getTemperatureReading(spacecraftName, journeyId, pageSize, pageState);
        return ResponseEntity.ok(res);
    }
    
    /**
     * Retrieve temperature metrics
     */
    @GetMapping(value="/pressure", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Retrieve pressure reading for a journey", response = List.class)
    @ApiResponse(code = 200, message = "Retrieve pressure reading for a journey")
    public ResponseEntity<PagedResultWrapper<SpacecraftPressureOverTime>> getPressureReading(
            @ApiParam(name="spacecraftName", value="Spacecraft name",example = "gemini3",required=true )
            @PathVariable(value = "spacecraftName") String spacecraftName,
            @ApiParam(name="journeyId", value="Identifer for journey",example = "abb7c000-c310-11ac-8080-808080808080",required=true )
            @PathVariable(value = "journeyId") UUID journeyId, 
            @ApiParam(name="pagesize", value="Requested page size, default is 10", required=false )
            @RequestParam("pagesize") Optional<Integer> pageSize,
            @ApiParam(name="pagestate", value="Use to retrieve next pages", required=false )
            @RequestParam("pagestate") Optional<String> pageState) {
        LOGGER.info("Retrieving pressure readings for spacecraft {} and journey {}", spacecraftName, journeyId);
        return ResponseEntity.ok(apolloService.getPressureReading(spacecraftName, journeyId, pageSize, pageState));
    } 
    
    /**
     * Retrieve speed metrics
     */
    @GetMapping(value="/speed", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Retrieve speed reading for a journey", response = List.class)
    @ApiResponse(code = 200, message = "Retrieve speed reading for a journey")
    public ResponseEntity<PagedResultWrapper<SpacecraftSpeedOverTime>> getSpeedReading(
            @ApiParam(name="spacecraftName", value="Spacecraft name",example = "gemini3",required=true )
            @PathVariable(value = "spacecraftName") String spacecraftName,
            @ApiParam(name="journeyId", value="Identifer for journey",example = "abb7c000-c310-11ac-8080-808080808080",required=true )
            @PathVariable(value = "journeyId") UUID journeyId, 
            @ApiParam(name="pagesize", value="Requested page size, default is 10", required=false )
            @RequestParam("pagesize") Optional<Integer> pageSize,
            @ApiParam(name="pagestate", value="Use to retrieve next pages", required=false )
            @RequestParam("pagestate") Optional<String> pageState) {
        LOGGER.info("Retrieving pressure readings for spacecraft {} and journey {}", spacecraftName, journeyId);
        return ResponseEntity.ok(apolloService.getSpeedReading(spacecraftName, journeyId, pageSize, pageState));
    } 
    
    /**
     * Retrieve location metrics
     */
    @GetMapping(value="/location", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Retrieve location reading for a journey", response = List.class)
    @ApiResponse(code = 200, message = "Retrieve locartion reading for a journey")
    public ResponseEntity<PagedResultWrapper<SpacecraftLocationOverTime>> getLocationReading(
            @ApiParam(name="spacecraftName", value="Spacecraft name",example = "gemini3",required=true )
            @PathVariable(value = "spacecraftName") String spacecraftName,
            @ApiParam(name="journeyId", value="Identifer for journey",example = "abb7c000-c310-11ac-8080-808080808080",required=true )
            @PathVariable(value = "journeyId") UUID journeyId, 
            @ApiParam(name="pagesize", value="Requested page size, default is 10", required=false )
            @RequestParam("pagesize") Optional<Integer> pageSize,
            @ApiParam(name="pagestate", value="Use to retrieve next pages", required=false )
            @RequestParam("pagestate") Optional<String> pageState) {
        LOGGER.info("Retrieving pressure readings for spacecraft {} and journey {}", spacecraftName, journeyId);
        return ResponseEntity.ok(apolloService.getLocationReading(spacecraftName, journeyId, pageSize, pageState));
    }


    @PostMapping(value="/temperature", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Save temperature reading for a journey", response = List.class)
    @ApiResponse(code = 200, message = "Saved temperature reading for a journey")
    public ResponseEntity<String> saveTemperatureReadings(
            @ApiParam(name="spacecraftName", value="Spacecraft name",example = "gemini3",required=true )
            @PathVariable(value = "spacecraftName") String spacecraftName,
            @ApiParam(name="journeyId", value="Identifier for journey",example = "abb7c000-c310-11ac-8080-808080808080",required=true )
            @PathVariable(value = "journeyId") UUID journeyId,
            @RequestBody SpacecraftTemperatureOverTime[] readings)
    {
        LOGGER.info("Saving temperature readings for spacecraft {} and journey {}", spacecraftName, journeyId);
        boolean res =  apolloService.insertTemperatureReading(readings);

        if (res) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value="/location", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Save location reading for a journey", response = List.class)
    @ApiResponse(code = 200, message = "Saved location reading for a journey")
    public ResponseEntity<String> saveLocationReadings(
            @ApiParam(name="spacecraftName", value="Spacecraft name",example = "gemini3",required=true )
            @PathVariable(value = "spacecraftName") String spacecraftName,
            @ApiParam(name="journeyId", value="Identifier for journey",example = "abb7c000-c310-11ac-8080-808080808080",required=true )
            @PathVariable(value = "journeyId") UUID journeyId,
            @RequestBody SpacecraftLocationOverTime[] readings)
    {
        LOGGER.info("Saving location readings for spacecraft {} and journey {}", spacecraftName, journeyId);
        boolean res =  apolloService.insertLocationReading(readings);

        if (res) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value="/pressure", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Save pressure reading for a journey", response = List.class)
    @ApiResponse(code = 200, message = "Saved pressure reading for a journey")
    public ResponseEntity<String> savePressureReadings(
            @ApiParam(name="spacecraftName", value="Spacecraft name",example = "gemini3",required=true )
            @PathVariable(value = "spacecraftName") String spacecraftName,
            @ApiParam(name="journeyId", value="Identifier for journey",example = "abb7c000-c310-11ac-8080-808080808080",required=true )
            @PathVariable(value = "journeyId") UUID journeyId,
            @RequestBody SpacecraftPressureOverTime[] readings)
    {
        LOGGER.info("Saving pressure readings for spacecraft {} and journey {}", spacecraftName, journeyId);
        boolean res =  apolloService.insertPressureReading(readings);

        if (res) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value="/speed", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Save speed reading for a journey", response = List.class)
    @ApiResponse(code = 200, message = "Saved speed reading for a journey")
    public ResponseEntity<String> saveSpeedReadings(
            @ApiParam(name="spacecraftName", value="Spacecraft name",example = "gemini3",required=true )
            @PathVariable(value = "spacecraftName") String spacecraftName,
            @ApiParam(name="journeyId", value="Identifier for journey",example = "abb7c000-c310-11ac-8080-808080808080",required=true )
            @PathVariable(value = "journeyId") UUID journeyId,
            @RequestBody SpacecraftSpeedOverTime[] readings)
    {
        LOGGER.info("Saving speed readings for spacecraft {} and journey {}", spacecraftName, journeyId);
        boolean res =  apolloService.insertSpeedReading(readings);

        if (res) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}

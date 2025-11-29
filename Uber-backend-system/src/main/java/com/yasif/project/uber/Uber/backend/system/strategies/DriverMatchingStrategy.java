package com.yasif.project.uber.Uber.backend.system.strategies;

import com.yasif.project.uber.Uber.backend.system.dto.RideRequestDto;
import com.yasif.project.uber.Uber.backend.system.entities.Driver;

import java.util.List;

public interface DriverMatchingStrategy {
     List<Driver>  findMatchingDriver(RideRequestDto rideRequestDto);
}

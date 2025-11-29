package com.yasif.project.uber.Uber.backend.system.strategies;

import com.yasif.project.uber.Uber.backend.system.dto.RideRequestDto;

public interface RideFareCalculationStrategy {
    double calculateFare(RideRequestDto rideRequestDto);
}

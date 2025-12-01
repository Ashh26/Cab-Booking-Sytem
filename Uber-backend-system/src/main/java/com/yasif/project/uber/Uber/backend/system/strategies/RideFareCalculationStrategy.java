package com.yasif.project.uber.Uber.backend.system.strategies;

import com.yasif.project.uber.Uber.backend.system.entities.RideRequest;

public interface RideFareCalculationStrategy {

    double RIDE_FARE_MULTIPLIER = 10;

    double calculateFare(RideRequest rideRequest);
}

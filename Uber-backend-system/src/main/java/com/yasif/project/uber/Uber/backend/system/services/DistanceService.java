package com.yasif.project.uber.Uber.backend.system.services;

import org.locationtech.jts.geom.Point;

public interface DistanceService {

    double calculateDistance(Point src,Point dest);

}

package com.yasif.project.uber.Uber.backend.system.repositories;

import com.yasif.project.uber.Uber.backend.system.entities.Rider;
import com.yasif.project.uber.Uber.backend.system.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiderRepository  extends JpaRepository<Rider,Long> {
    Optional<Rider> findByUser(User user);
}

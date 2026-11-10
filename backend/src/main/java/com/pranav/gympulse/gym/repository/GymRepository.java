package com.pranav.gympulse.gym.repository;

import com.pranav.gympulse.gym.model.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymRepository extends JpaRepository<Gym,Long> {
}

package com.pranav.gympulse.user.repository;

import com.pranav.gympulse.user.model.entity.User;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Id> {

   User findById(long id);

   User findByEmail(String email);
}

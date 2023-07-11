package com.medinet.infrastructure.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {


    boolean existsByEmail(String email);

    UserEntity findByEmail(String userName);


    void deleteByEmail(String byEmail);

}

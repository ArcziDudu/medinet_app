package com.medinet.infrastructure.repository.jpa;

import com.medinet.business.dao.AppointmentDao;
import com.medinet.infrastructure.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, Integer> {

    void deleteByAppointmentId(@Param("appointmentID") Integer appointmentID);

    @Query("""
             SELECT app FROM AppointmentEntity app WHERE app.status = :status
            """)
    List<AppointmentEntity> findAllByStatus(@Param("status") String status);
}

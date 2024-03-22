package com.shelter.bot.repository;


import com.shelter.bot.entity.ShelterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelterRepository extends JpaRepository<ShelterEntity, Long> {

}

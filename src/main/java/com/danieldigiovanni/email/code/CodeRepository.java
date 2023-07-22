package com.danieldigiovanni.email.code;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeRepository extends JpaRepository<Code, Long> {

    List<Code> getCodesByEmail(String email);

}

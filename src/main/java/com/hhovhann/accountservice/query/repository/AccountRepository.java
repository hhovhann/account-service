package com.hhovhann.accountservice.query.repository;

import com.hhovhann.accountservice.query.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
}

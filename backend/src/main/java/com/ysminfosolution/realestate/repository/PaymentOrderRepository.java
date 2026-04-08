package com.ysminfosolution.realestate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, UUID> {

}

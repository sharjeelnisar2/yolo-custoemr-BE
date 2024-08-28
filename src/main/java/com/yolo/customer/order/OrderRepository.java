package com.yolo.customer.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByUserId(Integer userID, Pageable pageable);

    Page<Order> findByOrderStatusIdAndUserId(Integer orderStatusId, Integer userId, Pageable pageable);

    Order findByCode(String code);
}

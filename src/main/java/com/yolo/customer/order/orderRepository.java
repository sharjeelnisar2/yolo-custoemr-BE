package com.yolo.customer.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface orderRepository  extends JpaRepository<order, Integer> {

}

package com.yolo.customer.idea;

<<<<<<< Updated upstream
=======
//import com.yolo.customer.order.Order;
>>>>>>> Stashed changes
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
}

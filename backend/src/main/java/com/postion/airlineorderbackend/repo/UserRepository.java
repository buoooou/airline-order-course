package com.postion.airlineorderbackend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @NonNull
  Optional<User> findById(@NonNull Long id);

  Optional<User> findByIdAndPassword(Long id, String password);

  Optional<User> findByUsername(String username);

  Optional<User> findByUsernameAndPassword(String username, String password);

  @Query("select o from Order o inner join o.user u where u.id=:userId")
  List<Order> findOrdersByUserId(@Param("userId") Long userId);

}

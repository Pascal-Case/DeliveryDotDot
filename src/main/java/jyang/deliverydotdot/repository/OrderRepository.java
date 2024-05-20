package jyang.deliverydotdot.repository;

import jyang.deliverydotdot.domain.PurchaseOrder;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.type.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<PurchaseOrder, Long> {

  @Query(
      "SELECT o FROM PurchaseOrder o " +
          "WHERE o.store = :store " +
          "ORDER BY o.createdAt DESC"
  )
  Slice<PurchaseOrder> findByStore(Store store, Pageable pageable);

  @Query(
      "SELECT o FROM PurchaseOrder o " +
          "WHERE o.store = :store " +
          "AND ("
          + "o.orderNumber LIKE %:query% OR "
          + "o.user.phone LIKE %:query% OR "
          + "o.phone LIKE %:query% OR "
          + "o.user.name LIKE %:query% "
          + ") " +
          "ORDER BY o.createdAt DESC"
  )
  Slice<PurchaseOrder> findByStoreAndQuery(Store store, @Param("query") String query,
      Pageable pageable);

  @Query(
      "SELECT o FROM PurchaseOrder o " +
          "WHERE o.store = :store " +
          "AND o.orderStatus = :status " +
          "ORDER BY o.createdAt DESC"
  )
  Slice<PurchaseOrder> findByStoreAndStatus(Store store, @Param("status") OrderStatus status,
      Pageable pageable);

  @Query(
      "SELECT o FROM PurchaseOrder o " +
          "WHERE o.store = :store " +
          "AND o.orderStatus = :status " +
          "AND ("
          + "o.orderNumber LIKE %:query% OR "
          + "o.user.phone LIKE %:query% OR "
          + "o.phone LIKE %:query% OR "
          + "o.user.name LIKE %:query% "
          + ") " +
          "ORDER BY o.createdAt DESC"
  )
  Slice<PurchaseOrder> findByStoreAndStatusAndQuery(Store store,
      @Param("status") OrderStatus status,
      @Param("query") String query,
      Pageable pageable);

  @Query(
      "SELECT o FROM PurchaseOrder o " +
          "WHERE o.user = :user " +
          "ORDER BY o.createdAt DESC"
  )
  Slice<PurchaseOrder> findByUser(User user, Pageable pageable);

  @Query(
      "SELECT o FROM PurchaseOrder o " +
          "WHERE o.user = :user " +
          "AND o.orderStatus = :status " +
          "ORDER BY o.createdAt DESC"
  )
  Slice<PurchaseOrder> findByUserAndStatus(User user, OrderStatus status, Pageable pageable);

  @Query(
      "SELECT o FROM PurchaseOrder o " +
          "WHERE o.user = :user " +
          "AND ("
          + "o.orderNumber LIKE %:query% OR "
          + "o.store.storeName LIKE %:query% "
          + ") " +
          "ORDER BY o.createdAt DESC"
  )
  Slice<PurchaseOrder> findByUserAndQuery(User user, String query, Pageable pageable);

  @Query(
      "SELECT o FROM PurchaseOrder o " +
          "WHERE o.user = :user " +
          "AND o.orderStatus = :status " +
          "AND ("
          + "o.orderNumber LIKE %:query% OR "
          + "o.store.storeName LIKE %:query% "
          + ") " +
          "ORDER BY o.createdAt DESC"
  )
  Slice<PurchaseOrder> findByUserAndStatusAndQuery(User user, OrderStatus status, String query,
      Pageable pageable);
}

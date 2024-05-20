package jyang.deliverydotdot.service;

import jakarta.validation.Valid;
import jyang.deliverydotdot.domain.Delivery;
import jyang.deliverydotdot.domain.PurchaseOrder;
import jyang.deliverydotdot.domain.Rider;
import jyang.deliverydotdot.dto.rider.CompleteDeliveryForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.DeliveryRepository;
import jyang.deliverydotdot.type.DeliveryStatus;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;

  private final RedisService redisService;

  private final OrderService orderService;

  private final S3Service s3Service;


  @Transactional
  public void createDelivery(Rider rider, Long orderId) {

    PurchaseOrder purchaseOrder = orderService.getOrderById(orderId);

    if (deliveryRepository.existsByPurchaseOrder(purchaseOrder)) {
      throw new RestApiException(ErrorCode.ALREADY_EXIST_DELIVERY);
    }

    deliveryRepository.save(
        Delivery.builder()
            .rider(rider)
            .purchaseOrder(purchaseOrder)
            .deliveryStatus(DeliveryStatus.ASSIGNED)
            .build());

    redisService.deleteOrderLocation(orderId);
  }

  @Transactional
  public void startDelivery(Rider rider, Long deliveryId) {
    Delivery delivery = getDeliveryById(deliveryId);

    validateDeliveryOwner(rider, delivery);
  }


  @Transactional
  public void completeDelivery(Rider rider, Long deliveryId,
      @Valid CompleteDeliveryForm completeDeliveryForm) {
    Delivery delivery = getDeliveryById(deliveryId);

    validateDeliveryOwner(rider, delivery);

    String imageUrl = null;
    if (completeDeliveryForm.getDeliveryCompleteImage() != null) {
      imageUrl =
          s3Service.uploadDeliveryImage(completeDeliveryForm.getDeliveryCompleteImage());
    }

    delivery.complete(imageUrl);
  }

  @Transactional
  public void cancelDelivery(Rider rider, Long deliveryId) {
    Delivery delivery = getDeliveryById(deliveryId);

    validateDeliveryOwner(rider, delivery);
  }

  public void validateDeliveryOwner(Rider rider, Delivery delivery) {
    if (!delivery.getRider().equals(rider)) {
      throw new RestApiException(ErrorCode.NOT_OWNER_DELIVERY);
    }
  }

  private Delivery getDeliveryById(Long deliveryId) {
    return deliveryRepository.findById(deliveryId)
        .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_DELIVERY));
  }
}

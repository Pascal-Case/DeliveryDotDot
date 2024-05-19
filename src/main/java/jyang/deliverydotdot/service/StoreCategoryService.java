package jyang.deliverydotdot.service;

import jyang.deliverydotdot.domain.StoreCategory;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.StoreCategoryRepository;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StoreCategoryService {

  private final StoreCategoryRepository storeCategoryRepository;

  /**
   * 가게 카테고리 유효성 검사
   *
   * @param storeCategoryId 가게 카테고리 ID
   */

  public void validateStoreCategory(Long storeCategoryId) {
    if (!storeCategoryRepository.existsById(storeCategoryId)) {
      log.warn("존재하지 않는 가게 카테고리입니다. storeCategoryId={}", storeCategoryId);
      throw new RestApiException(ErrorCode.STORE_CATEGORY_NOT_FOUND);
    }
  }

  /**
   * 가게 카테고리 조회
   *
   * @param storeCategoryId 가게 카테고리 ID
   * @return 가게 카테고리
   */
  public StoreCategory getStoreCategoryById(Long storeCategoryId) {
    return storeCategoryRepository.findById(storeCategoryId)
        .orElseThrow(() -> {
          log.warn("존재하지 않는 가게 카테고리입니다. storeCategoryId={}", storeCategoryId);
          return new RestApiException(ErrorCode.STORE_CATEGORY_NOT_FOUND);
        });
  }

}

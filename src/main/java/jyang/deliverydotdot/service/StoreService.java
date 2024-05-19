package jyang.deliverydotdot.service;

import java.time.LocalTime;
import java.util.List;
import jyang.deliverydotdot.domain.Menu;
import jyang.deliverydotdot.domain.MenuCategory;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.domain.StoreCategory;
import jyang.deliverydotdot.domain.StoreImage;
import jyang.deliverydotdot.dto.store.StoreRegisterForm;
import jyang.deliverydotdot.dto.store.StoreUpdateForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.MenuCategoryRepository;
import jyang.deliverydotdot.repository.MenuRepository;
import jyang.deliverydotdot.repository.StoreRepository;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

  private final StoreRepository storeRepository;

  private final MenuCategoryRepository menuCategoryRepository;

  private final MenuRepository menuRepository;

  private final StoreCategoryService storeCategoryService;

  private final LocationService locationService;

  private final S3Service s3Service;

  private final StoreImageService storeImageService;

  /**
   * 가게 등록
   *
   * @param partner           파트너
   * @param storeRegisterForm 가게 등록 폼
   */
  @Transactional
  public void registerStore(Partner partner, StoreRegisterForm storeRegisterForm) {
    // 가게 등록 유효성 검사
    validateRegisterStore(storeRegisterForm);

    // 가게 카테고리 조회
    StoreCategory storeCategory =
        storeCategoryService.getStoreCategoryById(storeRegisterForm.getStoreCategoryId());

    // 주소로 좌표 조회
    Point coordinates =
        locationService.getCoordinatesFromAddress(storeRegisterForm.getStoreAddress());

    // 가게 생성
    Store store = buildStore(partner, storeRegisterForm, storeCategory, coordinates);

    // 가게 저장
    storeRepository.save(store);

    // 가게 이미지 업로드 및 저장
    uploadAndSaveStoreImages(store, storeRegisterForm.getStoreImage1(),
        storeRegisterForm.getStoreImage2(), storeRegisterForm.getStoreImage3());
  }

  /**
   * 가게 정보 수정
   *
   * @param partner         파트너
   * @param storeUpdateForm 가게 수정 폼
   */

  @Transactional
  public void updateStore(Partner partner, StoreUpdateForm storeUpdateForm) {
    // 가게 조회
    Store store = findStore(storeUpdateForm.getStoreId());

    // 가게 소유자 확인
    validateStoreOwner(partner, store);

    // 가게 수정 유효성 검사
    validateUpdateStore(storeUpdateForm);

    // 가게 정보 업데이트
    store.update(storeUpdateForm);

    // 가게 이미지 업로드 및 업데이트
    updateStoreImages(store, storeUpdateForm.getStoreImage1(), storeUpdateForm.getStoreImage2(),
        storeUpdateForm.getStoreImage3());
  }

  /**
   * 가게 삭제
   *
   * @param partner 파트너
   */
  @Transactional
  public void deleteStore(Partner partner, Long storeId) {
    // 가게 조회
    Store store = findStore(storeId);

    // 가게 소유자 확인
    validateStoreOwner(partner, store);

    // Store에 연결된 MenuCategory 삭제
    for (MenuCategory menuCategory : store.getMenuCategories()) {
      deleteMenuCategoryAndMenu(menuCategory);
    }

    // 가게 삭제
    storeRepository.delete(store);

    // 가게 이미지 삭제
    List<StoreImage> storeImages = storeImageService.getStoreImagesByStore(store);
    for (StoreImage storeImage : storeImages) {
      s3Service.delete(storeImage.getImageUrl());
    }
    storeImageService.deleteStoreImages(store);
  }

  @Transactional
  protected void deleteMenuCategoryAndMenu(MenuCategory menuCategory) {

    List<Menu> menus = menuCategory.getMenus();

    // MenuCategory에 연결된 Menu 삭제
    menuRepository.deleteAll(menus);

    // MenuCategory 삭제
    menuCategoryRepository.delete(menuCategory);

  }

  /**
   * Build Store
   */
  private Store buildStore(Partner partner, StoreRegisterForm storeRegisterForm,
      StoreCategory storeCategory, Point coordinates) {
    return Store.builder()
        .partner(partner)
        .storeName(storeRegisterForm.getStoreName())
        .storeCategory(storeCategory)
        .storeAddress(storeRegisterForm.getStoreAddress())
        .coordinates(coordinates)
        .registrationNumber(storeRegisterForm.getRegistrationNumber())
        .holiday(storeRegisterForm.getHoliday())
        .openTime(storeRegisterForm.getOpenTime())
        .closeTime(storeRegisterForm.getCloseTime())
        .lastOrderTime(storeRegisterForm.getLastOrderTime())
        .description(storeRegisterForm.getDescription())
        .averageRating(0.0)
        .reviewCount(0)
        .build();
  }

  /**
   * 가게 이미지 업로드 및 저장
   *
   * @param store       가게
   * @param storeImages 가게 이미지
   */
  private void uploadAndSaveStoreImages(Store store, MultipartFile... storeImages) {
    for (int i = 0; i < storeImages.length; i++) {
      MultipartFile storeImage = storeImages[i];
      if (storeImage != null) {
        // 이미지 업로드
        String storeImageUrl = s3Service.uploadStoreImage(storeImage);

        // 이미지 정보 저장
        storeImageService.saveStoreImageData(store, storeImageUrl, i + 1);
      }
    }
  }

  /**
   * 가게 이미지 업로드 및 업데이트
   *
   * @param store       가게
   * @param storeImages 가게 이미지
   */
  private void updateStoreImages(Store store, MultipartFile... storeImages) {
    for (int i = 0; i < storeImages.length; i++) {
      MultipartFile storeImage = storeImages[i];
      if (storeImage != null) {
        // 기존 이미지 정보
        StoreImage prevStoreImage =
            storeImageService.getStoreImageByStoreAndImageOrder(store, i + 1);

        // 기존 이미지 삭제
        if (prevStoreImage != null) {
          s3Service.delete(prevStoreImage.getImageUrl());
        }

        // 새 이미지 업로드
        String newStoreImageUrl = s3Service.uploadStoreImage(storeImage);

        // 이미지 정보 업데이트
        storeImageService.updateStoreImageData(store, newStoreImageUrl, i + 1);
      }
    }
  }

  /**
   * ID로 가게 찾기
   *
   * @param storeId 가게 ID
   * @return 가게
   */
  public Store findStore(Long storeId) {
    return storeRepository.findById(storeId)
        .orElseThrow(() -> {
          log.warn("가게를 찾을 수 없습니다. storeId={}", storeId);
          return new RestApiException(ErrorCode.STORE_NOT_FOUND);
        });
  }


  /**
   * 가게 등록 유효성 검사
   *
   * @param storeRegisterForm 가게 등록 폼
   */
  private void validateRegisterStore(StoreRegisterForm storeRegisterForm) {

    isValidRegistrationNumber(storeRegisterForm.getRegistrationNumber());

    storeCategoryService.validateStoreCategory(storeRegisterForm.getStoreCategoryId());

    validateStoreForm(
        storeRegisterForm.getHoliday(),
        storeRegisterForm.getOpenTime(),
        storeRegisterForm.getCloseTime(),
        storeRegisterForm.getLastOrderTime());
  }

  /**
   * 가게 수정 유효성 검사
   *
   * @param storeUpdateForm 가게 수정 폼
   */
  private void validateUpdateStore(StoreUpdateForm storeUpdateForm) {
    validateStoreForm(
        storeUpdateForm.getHoliday(),
        storeUpdateForm.getOpenTime(),
        storeUpdateForm.getCloseTime(),
        storeUpdateForm.getLastOrderTime());
  }

  /**
   * StoreForm 유효성 검사
   */

  private void validateStoreForm(Integer holiday, LocalTime openTime, LocalTime closeTime,
      LocalTime lastOrderTime) {
    if (holiday != null) {
      isValidHoliday(holiday);
    }
    if (openTime != null) {
      isValidTime(openTime);
    }
    if (closeTime != null) {
      isValidTime(closeTime);
    }
    if (lastOrderTime != null) {
      isValidTime(lastOrderTime);
    }
  }

  /**
   * 가게 소유자 확인
   *
   * @param partner 파트너
   * @param store   가게
   */
  public void validateStoreOwner(Partner partner, Store store) {
    if (!store.getPartner().getPartnerId().equals(partner.getPartnerId())) {
      log.warn("가게 소유자가 아닙니다. partner={}, store={}",
          partner.getPartnerId(), store.getPartner().getPartnerId());
      throw new RestApiException(ErrorCode.NOT_STORE_OWNER);
    }
  }


  /**
   * 시간 유효성 검사
   *
   * @param time 시간
   */

  private void isValidTime(LocalTime time) {
    try {
      DateTimeFormat.forPattern("HH:mm").parseLocalTime(time.toString());
    } catch (IllegalArgumentException e) {
      log.warn("잘못된 시간 형식입니다. time={}", time);
      throw new RestApiException(ErrorCode.INVALID_TIME_FORMAT);
    }
  }

  /**
   * 휴일 유효성 검사
   *
   * @param holiday 휴일
   */

  private void isValidHoliday(Integer holiday) {
    if (holiday < 1 || holiday > 7) {
      log.warn("잘못된 휴일 형식입니다. holiday={}", holiday);
      throw new RestApiException(ErrorCode.INVALID_HOLIDAY_FORMAT);
    }
  }


  /**
   * 사업자 등록번호 유효성 검사
   *
   * @param registrationNumber 사업자 등록번호
   */
  private void isValidRegistrationNumber(String registrationNumber) {
    if (storeRepository.existsByRegistrationNumber(registrationNumber)) {
      log.warn("이미 등록된 사업자 등록번호입니다. registrationNumber={}", registrationNumber);
      throw new RestApiException(ErrorCode.ALREADY_REGISTERED_REGISTRATION_NUMBER);
    }
  }


}

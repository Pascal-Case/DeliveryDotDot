package jyang.deliverydotdot.service;

import java.util.List;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.domain.StoreImage;
import jyang.deliverydotdot.repository.StoreImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreImageService {

  private final StoreImageRepository storeImageRepository;

  @Transactional
  public void saveStoreImageData(Store store, String imageUrl, int imageIndex) {
    storeImageRepository.save(StoreImage.builder()
        .store(store)
        .imageOrder(imageIndex)
        .imageUrl(imageUrl)
        .build()
    );
  }

  public StoreImage getStoreImageByStoreAndImageOrder(Store store, int imageOrder) {
    return storeImageRepository.findByStoreAndImageOrder(store, imageOrder)
        .orElse(null);
  }

  public void updateStoreImageData(Store store, String imageUrl, int imageIndex) {
    StoreImage storeImage = getStoreImageByStoreAndImageOrder(store, imageIndex);
    storeImage.update(imageUrl, imageIndex);
  }

  public void deleteStoreImages(Store store) {
    storeImageRepository.deleteAllByStore(store);
  }

  public List<StoreImage> getStoreImagesByStore(Store store) {
    return storeImageRepository.findAllByStore(store);
  }
}
